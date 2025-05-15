
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import Toaster.Toaster;
import Utils.UIUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class StudentManagerApp extends JFrame {
    private JTextField tfMaSo, tfHoTen, tfDiem1, tfDiem2, tfDiem3;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> cbFilter;
    private java.util.List<SinhVien> danhSach = new ArrayList<>();

    public StudentManagerApp() {
        setTitle("Quản Lý Sinh Viên");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextField tfTimKiem = new JTextField(20);
        JButton btnTim = new JButton("Tìm kiếm");
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Nhập mã hoặc tên:"));
        searchPanel.add(tfTimKiem);
        searchPanel.add(btnTim);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 5, 5));
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

        // Bảng hiển thị
        tableModel = new DefaultTableModel(new Object[]{"Mã số", "Họ tên", "ĐTB", "Xếp loại"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa ô nào cả
            }
        };


        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        Map<String, Integer> hocLucOrder = Map.of("Giỏi", 1, "Khá", 2, "Trung bình", 3, "Yếu", 4);
        sorter.setComparator(3, (o1, o2) -> Integer.compare(hocLucOrder.getOrDefault(o1, 5), hocLucOrder.getOrDefault(o2, 5)));

        cbFilter = new JComboBox<>(new String[]{"Tất cả", "Giỏi", "Khá", "Trung bình", "Yếu"});
        cbFilter.addActionListener(e -> {
            String selected = (String) cbFilter.getSelectedItem();
            if ("Tất cả".equals(selected)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 3));
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Lọc học lực:"));
        filterPanel.add(cbFilter);
        filterPanel.add(searchPanel);
        topPanel.add(filterPanel, BorderLayout.SOUTH);


        JScrollPane scrollPane = new JScrollPane(table);

        // Panel nút
        JPanel buttonPanel = new JPanel();
        JButton btnThem = new JButton("Thêm");
        JButton btnXoa = new JButton("Xóa");
        JButton btnSua = new JButton("Sửa");
        JButton btnTai = new JButton("Tải");
        JButton btnThongke = new JButton("Thống kê");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnTai);
        buttonPanel.add(btnThongke);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện nút Thêm
        btnThem.addActionListener(e -> {
            String maSo = tfMaSo.getText();
            String hoTen = tfHoTen.getText();
            SinhVienDAO dao = new SinhVienDAO();
            String diem1Str = tfDiem1.getText().trim();
            String diem2Str = tfDiem2.getText().trim();
            String diem3Str = tfDiem3.getText().trim();
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
                    tfMaSo.requestFocus();
                    return;
                }


                SinhVien sv = new SinhVien(maSo, hoTen, d1, d2, d3);
                danhSach.add(sv);
                tableModel.addRow(new Object[]{maSo, hoTen, sv.getDiemTrungBinh(), sv.getXepLoai()});
                dao.addStudent(sv);
                cleadfil();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
            }
        });

        // Sự kiện nút Xóa
        btnXoa.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
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
        });

        // Sự kiện nút Sửa
        btnSua.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            SinhVienDAO dao = new SinhVienDAO();
            if (selectedRow != -1) {
                SinhVien sv = danhSach.get(selectedRow);

                // Hiển thị thông tin sinh viên vào các trường nhập liệu
                tfMaSo.setText(sv.getMaSo());
                tfHoTen.setText(sv.getHoTen());
                tfDiem1.setText(String.valueOf(sv.getDiem1()));
                tfDiem2.setText(String.valueOf(sv.getDiem2()));
                tfDiem3.setText(String.valueOf(sv.getDiem3()));

                // Thay đổi văn bản của nút Thêm thành Cập nhật
                btnThem.setText("Cập nhật");

                // Gỡ bỏ tất cả ActionListener hiện có từ nút "Thêm"
                btnThem.removeActionListener(btnThem.getActionListeners()[0]);

                // Thêm sự kiện mới cho nút "Cập nhật"
                btnThem.addActionListener(evt -> {
                    // Cập nhật thông tin vào đối tượng sinh viên
                    sv.setMaSo(tfMaSo.getText());
                    sv.setHoTen(tfHoTen.getText());
                    sv.setDiem1(Float.parseFloat(tfDiem1.getText()));
                    sv.setDiem2(Float.parseFloat(tfDiem2.getText()));
                    sv.setDiem3(Float.parseFloat(tfDiem3.getText()));

                    // Cập nhật lại dữ liệu trong bảng
                    tableModel.setValueAt(sv.getMaSo(), selectedRow, 0);
                    tableModel.setValueAt(sv.getHoTen(), selectedRow, 1);
                    tableModel.setValueAt(sv.getDiemTrungBinh(), selectedRow, 2);
                    tableModel.setValueAt(sv.getXepLoai(), selectedRow, 3);

                    // Cập nhật cơ sở dữ liệu
                    boolean isUpdated = dao.updateStudent(sv);
                    if (isUpdated) {
                        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                        cleadfil();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                    }

                    btnThem.setText("Thêm");
                    btnThem.removeActionListener(btnThem.getActionListeners()[0]);
                    btnThem.addActionListener(evt1 -> {
                        String maSo = tfMaSo.getText();
                        String hoTen = tfHoTen.getText();
                        try {
                            float d1 = Float.parseFloat(tfDiem1.getText());
                            float d2 = Float.parseFloat(tfDiem2.getText());
                            float d3 = Float.parseFloat(tfDiem3.getText());
                            SinhVien newSV = new SinhVien(maSo, hoTen, d1, d2, d3);
                            danhSach.add(newSV);
                            tableModel.addRow(new Object[]{maSo, hoTen, newSV.getDiemTrungBinh(), newSV.getXepLoai()});
                            insertStudent(newSV);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Điểm phải là số!");
                        }
                    });
                });
            } else {
                JOptionPane.showMessageDialog(this, "Chưa chọn sinh viên để sửa!");
            }
        });



        // Sự kiện nút Tải
        btnTai.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file PDF");
            String selectedHocluc = (String) cbFilter.getSelectedItem();
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
            Font font = new Font(baseFont, 12, Font.NORMAL);

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
        });

        btnTim.addActionListener(e -> {
            String keyword = tfTimKiem.getText().trim().toLowerCase();
            String selectedHocLuc = (String) cbFilter.getSelectedItem();

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

        btnThongke.addActionListener(e -> {
            if (danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có sinh viên trong danh sách.");
                return;
            }

            int tongSV = danhSach.size();
            double tongDiem = 0;
            int soDat = 0;
            SinhVien svMax = danhSach.get(0);
            SinhVien svMin = danhSach.get(0);

            List<SinhVien> gioi = new ArrayList<>();
            List<SinhVien> yeu = new ArrayList<>();

            for (SinhVien sv : danhSach) {
                float diemTB = Float.parseFloat(sv.getDiemTrungBinh());
                tongDiem += diemTB;
                if (diemTB >= 5) soDat++;


                if (diemTB >= 8) gioi.add(sv);
                else if (diemTB < 5) yeu.add(sv);
            }

            double diemTrungBinh = tongDiem / tongSV;

            StringBuilder sb = new StringBuilder();
            sb.append("📊 THỐNG KÊ SINH VIÊN:\n");
            sb.append("Tổng số sinh viên: ").append(tongSV).append("\n");
            sb.append("Điểm trung bình tất cả: ").append(String.format("%.2f", diemTrungBinh)).append("\n");
            sb.append("Số sinh viên đạt (>=5): ").append(soDat).append("\n");
            sb.append("Số sinh viên giỏi (>=8): ").append(gioi.size()).append("\n");
            sb.append("Số sinh viên yếu (<5): ").append(yeu.size()).append("\n");

            JOptionPane.showMessageDialog(this, sb.toString());
        });

        loadDataToTable();

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer(){
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
    }
    private void insertStudent(SinhVien sv) {
        try (Connection conn = MySQLConnection.getConnection()) {
            String maSo = sv.getMaSo();
            if (SinhVienDAO.kiemTraTonTaiMaSo(maSo)) {
                JOptionPane.showMessageDialog(null, "Mã số đã tồn tại. Không thể thêm mới!");
            }else {
                String sql = "INSERT INTO sinhvien (maso, hoten, diem1, diem2, diem3) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, sv.getMaSo());
                stmt.setString(2, sv.getHoTen());
                stmt.setFloat(3, sv.getDiem1());
                stmt.setFloat(4, sv.getDiem2());
                stmt.setFloat(5, sv.getDiem3());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void cleadfil(){
        tfMaSo.requestFocus();
        tfMaSo.setText("");
        tfHoTen.setText("");
        tfDiem1.setText("");
        tfDiem2.setText("");
        tfDiem3.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerApp().setVisible(true));
    }

}


