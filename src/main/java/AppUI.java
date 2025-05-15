import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppUI extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField,maso,hoten, diem1Field, diem2Field, diem3Field;
    private JButton btnThem;
    private JComboBox<String> filterCombo;
    private TableRowSorter<DefaultTableModel> sorter;
    private java.util.List<SinhVien> danhSach = new ArrayList<>();

    public AppUI() {
        setTitle("Quản Lý Sinh Viên");
        setUndecorated(true);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        initComponents();
        addWindowDragFunctionality();
        setVisible(true);
    }

    private void initComponents() {

        add(inputPanel());

        // Tạo tiêu đề cột
        String[] columnNames = {"Mã số", "Họ tên", "ĐTB", "Xếp loại"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBounds(20, 170, 850, 250);
        add(scrollPane);

        // Ô tìm kiếm và lọc
        searchField = new JTextField();
        searchField.setBounds(400, 130, 180, 30);
        add(searchField);

        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.setBounds(600, 130, 100, 30);
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            String selectedHocLuc = (String) filterCombo.getSelectedItem();

            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    String maSo = entry.getStringValue(0).toLowerCase();
                    String hoTen = entry.getStringValue(1).toLowerCase();
                    String xepLoai = entry.getStringValue(3);

                    boolean matchesKeyword = maSo.contains(keyword) || hoTen.contains(keyword);
                    boolean matchesHocLuc = selectedHocLuc.equals("Tất cả") || xepLoai.equals(selectedHocLuc);

                    return matchesKeyword && matchesHocLuc ;
                }
            });
        });
        add(searchBtn);

//        sorter = new TableRowSorter<>(tableModel);
//        studentTable.setRowSorter(sorter);
//        Map<String, Integer> hocLucOrder = Map.of("Giỏi", 1, "Khá", 2, "Trung bình", 3, "Yếu", 4);
//        sorter.setComparator(3, (o1, o2) -> Integer.compare(hocLucOrder.getOrDefault(o1, 5), hocLucOrder.getOrDefault(o2, 5)));
//
//        filterCombo = new JComboBox<>(new String[]{"Tất cả", "Giỏi", "Khá", "Trung bình", "Yếu"});
//        filterCombo.setBounds(250, 130, 120, 30);
//        filterCombo.addActionListener(e -> {
//            String selected = (String) filterCombo.getSelectedItem();
//            if ("Tất cả".equals(selected)) {
//                sorter.setRowFilter(null);
//            } else {
//                sorter.setRowFilter(RowFilter.regexFilter(selected, 3));
//            }
//        });

        sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);
        sorter.setComparator(3, (o1, o2) -> {
            HocLuc hl1 = HocLuc.fromLabel(o1.toString());
            HocLuc hl2 = HocLuc.fromLabel(o2.toString());
            int order1 = hl1 != null ? hl1.getOrder() : Integer.MAX_VALUE;
            int order2 = hl2 != null ? hl2.getOrder() : Integer.MAX_VALUE;
            return Integer.compare(order1, order2);
        });
        filterCombo = new JComboBox<>(new String[]{"Tất cả", "Giỏi", "Khá", "Trung bình", "Yếu"});
        filterCombo.setBounds(250, 130, 120, 30);
        filterCombo.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            if ("Tất cả".equals(selected)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 3));
            }
        });


        studentTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String xepLoai = (String) value;

                if ("Giỏi".equals(xepLoai)) {
                    c.setForeground(Color.BLUE);
                } else if ("Khá".equals(xepLoai)) {
                    c.setForeground(new Color(0, 128, 0));
                } else if ("Trung bình".equals(xepLoai)) {
                    c.setForeground(Color.ORANGE);
                } else if ("Yếu".equals(xepLoai)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });


        add(filterCombo);

        // Các nút thao tác
        String[] btnLabels = {"Thêm", "Xóa", "Sửa", "Tải", "Thống kê"};
        int x = 20;
        for (String label : btnLabels) {
            JButton btn = new JButton(label);
            btn.setBounds(x, 420, 100, 30);
            add(btn);

            switch (label) {
                case "Thêm" -> {
                    btnThem = btn;
                    btn.addActionListener(e -> handleAdd());
                }
                case "Xóa" -> btn.addActionListener(e -> handleDelete());
                case "Sửa" -> btn.addActionListener(e -> handleEdit());
                case "Tải" -> btn.addActionListener(e -> handleDownLoad());
                case "Thống kê" -> btn.addActionListener(e -> handleStatistics());
            }

            x += 110;
        }

        // Label tiêu đề (nếu muốn)
        JLabel title = new JLabel("QUẢN LÝ SINH VIÊN");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(20, 20, 400, 30);
        add(title);
        loadDataToTable();
    }

    private JPanel inputPanel(){
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        inputPanel.setBounds(20, 60, 850, 60);

        JTextField tfMaSo, tfHoTen, tfDiem1, tfDiem2, tfDiem3;

        tfMaSo = new JTextField();
        tfHoTen = new JTextField();
        tfDiem1 = new JTextField();
        tfDiem2 = new JTextField();
        tfDiem3 = new JTextField();

        inputPanel.add(new JLabel("Mã số:"));
        inputPanel.add(new JLabel("Họ tên:"));
        inputPanel.add(new JLabel("Điểm 1:"));
        inputPanel.add(new JLabel("Điểm 2:"));
        inputPanel.add(new JLabel("Điểm 3:"));

        inputPanel.add(tfMaSo);
        inputPanel.add(tfHoTen);
        inputPanel.add(tfDiem1);
        inputPanel.add(tfDiem2);
        inputPanel.add(tfDiem3);

        this.diem1Field = tfDiem1;
        this.diem2Field = tfDiem2;
        this.diem3Field = tfDiem3;
        this.maso = tfMaSo;
        this.hoten = tfHoTen;

        return inputPanel;
    }

    private void addWindowDragFunctionality() {
        // Giống LoginUI - để kéo cửa sổ không khung
        MouseAdapter ma = new MouseAdapter() {
            int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(getLocation().x + x - lastX, getLocation().y + y - lastY);
                lastX = x;
                lastY = y;
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void loadDataToTable() {
        SinhVienDAO dao = new SinhVienDAO();
        List<SinhVien> list = dao.getAll();

        danhSach = list;
        tableModel.setRowCount(0);
        for (SinhVien sv : list) {
            tableModel.addRow(new Object[]{
                    sv.getMaSo(), sv.getHoTen(),
                    sv.getDiemTrungBinh(), sv.getXepLoai()
            });
        }
    }

    private void handleAdd() {
        String maSo = maso.getText();
        String hoTen = hoten.getText();
        SinhVienDAO dao = new SinhVienDAO();
        String diem1Str = diem1Field.getText().trim();
        String diem2Str = diem2Field.getText().trim();
        String diem3Str = diem3Field.getText().trim();
        try {

            if (maSo.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            float d1, d2, d3;
            try {
                d1 = Float.parseFloat(diem1Str);
                d2 = Float.parseFloat(diem2Str);
                d3 = Float.parseFloat(diem3Str);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số hợp lệ (không được chứa chữ).");
                return;
            }

            if (d1 < 0 || d1 > 10 || d2 < 0 || d2 > 10 || d3 < 0 || d3 > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10.");
                return;
            }

            if (dao.kiemTraTonTaiMaSo(maSo)) {
                JOptionPane.showMessageDialog(this, "Mã số đã tồn tại. Không thể thêm mới!");
                maso.requestFocus();
                return;
            }


            SinhVien sv = new SinhVien(maSo, hoTen, d1, d2, d3);
            danhSach.add(sv);
            tableModel.addRow(new Object[]{maSo, hoTen, sv.getDiemTrungBinh(), sv.getXepLoai()});
            dao.addStudent(sv);
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
        }
    }

    private void handleDelete() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            String maSo = (String) tableModel.getValueAt(selectedRow, 0);

            boolean isDeleted = new SinhVienDAO().deleteStudent(maSo);
            if (isDeleted) {
                danhSach.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Sinh viên đã được xóa!");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa sinh viên!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chưa chọn sinh viên để xóa!");
        }
    }

    private void handleEdit() {
        int selectedRow = studentTable.getSelectedRow();
        SinhVienDAO dao = new SinhVienDAO();

        if (selectedRow != -1) {
            SinhVien sv = danhSach.get(selectedRow);

            // Hiển thị thông tin sinh viên vào các ô nhập
            maso.setText(sv.getMaSo());
            hoten.setText(sv.getHoTen());
            diem1Field.setText(String.valueOf(sv.getDiem1()));
            diem2Field.setText(String.valueOf(sv.getDiem2()));
            diem3Field.setText(String.valueOf(sv.getDiem3()));

            btnThem.setText("Cập nhật");
            for (var listener : btnThem.getActionListeners()) {
                btnThem.removeActionListener(listener);
            }

            btnThem.addActionListener(evt -> {
                try {
                    float d1 = Float.parseFloat(diem1Field.getText().trim());
                    float d2 = Float.parseFloat(diem2Field.getText().trim());
                    float d3 = Float.parseFloat(diem3Field.getText().trim());

                    if (d1 < 0 || d1 > 10 || d2 < 0 || d2 > 10 || d3 < 0 || d3 > 10) {
                        JOptionPane.showMessageDialog(this, "Điểm phải nằm trong khoảng từ 0 đến 10.");
                        return;
                    }

                    sv.setHoTen(hoten.getText());
                    sv.setDiem1(d1);
                    sv.setDiem2(d2);
                    sv.setDiem3(d3);

                    // Cập nhật bảng
                    tableModel.setValueAt(sv.getHoTen(), selectedRow, 1);
                    tableModel.setValueAt(sv.getDiemTrungBinh(), selectedRow, 2);
                    tableModel.setValueAt(sv.getXepLoai(), selectedRow, 3);

                    if (dao.updateStudent(sv)) {
                        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                        loadDataToTable();
                        studentTable.getSelectionModel().clearSelection();
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                    }

                    // Đặt lại nút "Thêm"
                    btnThem.setText("Thêm");
                    for (var listener : btnThem.getActionListeners()) {
                        btnThem.removeActionListener(listener);
                    }
                    btnThem.addActionListener(e -> handleAdd());

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng điểm (số từ 0 đến 10).");
                }
            });

        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên để sửa.");
        }
    }


    private void handleDownLoad(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
        String selectedHocluc = (String) filterCombo.getSelectedItem();
        // Tạo tên file theo thời gian
        String fileName = "DanhSachSV_" + selectedHocluc +"_"+ LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("src/main/font/NotoSans-Italic-VariableFont_wdth,wght.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(baseFont, 12, com.itextpdf.text.Font.NORMAL);

        // Đặt tên file gợi ý
        fileChooser.setSelectedFile(new File(fileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Kiểm tra nếu chưa có đuôi ".pdf", thêm vào
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                // Tạo tài liệu PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Tiêu đề cho file PDF
                document.add(new Paragraph("Danh sách sinh viên",font));

                // Cấu trúc bảng trong PDF
                PdfPTable table = new PdfPTable(4);
                String[] headers = {"Mã số", "Họ tên", "Điểm trung bình", "Xếp loại"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, font));
                    table.addCell(cell);
                }
//
                List<SinhVien> list = danhSach.stream()
                        .filter(sv -> selectedHocluc == null || selectedHocluc.equals("Tất cả") || selectedHocluc.equals(sv.getXepLoai()))
                        .collect(Collectors.toList());

                // Thêm dữ liệu từ danh sách sinh viên vào bảng
                for (SinhVien sv : list) {
                    table.addCell(new PdfPCell(new Phrase(sv.getMaSo(), font)));
                    table.addCell(new PdfPCell(new Phrase(sv.getHoTen(), font)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(sv.getDiemTrungBinh()), font)));
                    table.addCell(new PdfPCell(new Phrase(sv.getXepLoai(), font)));
                }


                // Thêm bảng vào tài liệu PDF
                document.add(table);
                document.close();
                JOptionPane.showMessageDialog(this, "Lưu thành công: " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file PDF!");
            }
        }
    }

    private void handleStatistics() {

            Map<String, Integer> thongKe = new HashMap<>();

            int tongSV = danhSach.size();
            float tongDiem = 0;
            int soDat = 0;
            int gioi = 0;
            int yeu = 0;

            for (SinhVien sv : danhSach) {
                float diemTB = Float.parseFloat(sv.getDiemTrungBinh());
                tongDiem += diemTB;

                if (diemTB >= 5) soDat++;
                if (diemTB >= 8) gioi++;
                if (diemTB < 5) yeu++;

                String xl = sv.getXepLoai();
                thongKe.put(xl, thongKe.getOrDefault(xl, 0) + 1);
            }

            float diemTrungBinh = tongSV > 0 ? tongDiem / tongSV : 0;

            // === Biểu đồ thống kê xếp loại ===
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : thongKe.entrySet()) {
                dataset.addValue(entry.getValue(), "Số lượng", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Thống kê xếp loại", "Xếp loại", "Số sinh viên", dataset,
                    PlotOrientation.VERTICAL, false, true, false
            );
            
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());  // <-- dòng quan trọng
            ChartPanel chartPanel = new ChartPanel(chart);

            // === Thông tin thống kê số liệu ===
            StringBuilder sb = new StringBuilder();
            sb.append("📊 THỐNG KÊ SINH VIÊN\n");
            sb.append("Tổng số sinh viên: ").append(tongSV).append("\n");
            sb.append("Điểm trung bình toàn bộ: ").append(String.format("%.2f", diemTrungBinh)).append("\n");
            sb.append("Số sinh viên đạt (>=5): ").append(soDat).append("\n");
            sb.append("Số sinh viên giỏi (>=8): ").append(gioi).append("\n");
            sb.append("Số sinh viên yếu (<5): ").append(yeu).append("\n");


            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textArea.setBackground(new Color(248, 248, 248));
            textArea.setMargin(new Insets(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(580, 130));

            // === Tạo hộp thoại chứa cả biểu đồ và thống kê ===
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            panel.add(scrollPane, BorderLayout.SOUTH);

            JDialog dialog = new JDialog();
            dialog.setTitle("Thống kê sinh viên");
            dialog.setContentPane(panel);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
    }


    private void clearFields() {
        maso.setText("");
        hoten.setText("");
        diem1Field.setText("");
        diem2Field.setText("");
        diem3Field.setText("");
    }

    public enum HocLuc {
        GIOI("Giỏi", 1),
        KHA("Khá", 2),
        TRUNG_BINH("Trung bình", 3),
        YEU("Yếu", 4);

        private final String label;
        private final int order;

        HocLuc(String label, int order) {
            this.label = label;
            this.order = order;
        }

        public String getLabel() {
            return label;
        }

        public int getOrder() {
            return order;
        }

        // Hàm tiện ích để lấy theo label
        public static HocLuc fromLabel(String label) {
            for (HocLuc hl : values()) {
                if (hl.getLabel().equalsIgnoreCase(label)) {
                    return hl;
                }
            }
            return null;
        }
    }


    public static void main(String[] args) {
        new AppUI();
    }
}