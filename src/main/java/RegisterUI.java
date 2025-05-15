
import Utils.*;
import Toaster.Toaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RegisterUI extends JFrame {

    private final Toaster toaster;
    private TextFieldUsername usernameField;
    private TextFieldPassword passwordField;
    private TextFieldPassword confirmPasswordField;
    Account acc = new Account();
    JButton registerBtn = new JButton() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = UIUtils.get2dGraphics(g);
            super.paintComponent(g2);

            g2.setColor(UIUtils.COLOR_INTERACTIVE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.setFont(UIUtils.FONT_GENERAL_UI);
            g2.setColor(Color.WHITE);
            FontMetrics metrics = g2.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth("Đăng ký")) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString("Đăng ký", x, y);
        }
    };
    public RegisterUI() {
        JPanel panel = getMainPanel();

        addUsernameField(panel);
        addPasswordField(panel);
        addConfirmPasswordField(panel);
        addRegisterButton(panel);
        addLoginRedirect(panel);


        this.add(panel);
        this.pack();
        this.setVisible(true);
        this.toFront();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);

        toaster = new Toaster(panel);
    }

    private JPanel getMainPanel() {
        this.setUndecorated(true);

        Dimension size = new Dimension(800, 400);
        JPanel panel = new JPanel();
        panel.setSize(size);
        panel.setPreferredSize(size);
        panel.setBackground(UIUtils.COLOR_BACKGROUND);
        panel.setLayout(null);

        // Kéo cửa sổ
        MouseAdapter drag = new MouseAdapter() {
            int x, y;
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getXOnScreen();
                y = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getXOnScreen();
                int dy = e.getYOnScreen();
                setLocation(getX() + dx - x, getY() + dy - y);
                x = dx;
                y = dy;
            }
        };
        panel.addMouseListener(drag);
        panel.addMouseMotionListener(drag);

        return panel;
    }

    private void addUsernameField(JPanel panel) {
        usernameField = new TextFieldUsername();
        usernameField.setPlaceholder("Tên đăng nhập");
        usernameField.setBounds(275, 100, 250, 40);
        panel.add(usernameField);
    }

    private void addPasswordField(JPanel panel) {
        passwordField = new TextFieldPassword();
        passwordField.setPlaceholder("Mật khẩu");
        passwordField.setBounds(275, 150, 250, 40);
        panel.add(passwordField);
    }

    private void addConfirmPasswordField(JPanel panel) {
        confirmPasswordField = new TextFieldPassword();
        confirmPasswordField.setPlaceholder("Nhập lại mật khẩu");
        confirmPasswordField.setBounds(275, 200, 250, 40);
        panel.add(confirmPasswordField);
    }

    private void addRegisterButton(JPanel panel) {


        registerBtn.setBounds(275, 260, 250, 40);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleRegister();
            }
        });

        panel.add(registerBtn);
    }

    private void addLoginRedirect(JPanel panel) {

        panel.add(new HyperlinkText("Đã có tài khoản? Đăng nhập", 275, 320, () -> {
            this.dispose();
            new LoginUI();
        }));
    }

    private void handleRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            toaster.error("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!pass.equals(confirm)) {
            toaster.error("Mật khẩu không khớp.");
            return;
        }

        // Kiểm tra trùng tài khoản
        if (Account.isUsernameExists(user)) {
            toaster.error("Tên đăng nhập đã tồn tại.");
            return;
        }

        // Đăng ký
        if (Account.register(user, pass)) {
            // Thông báo thành công
            JDialog dialog = new JDialog(this, "🎉 Đăng ký thành công", true);
            dialog.setUndecorated(true);
            dialog.setSize(300, 150);
            dialog.setLayout(null);
            dialog.getContentPane().setBackground(new Color(60, 179, 113)); // Xanh pastel

            JLabel label = new JLabel("🎉 Đăng ký thành công!", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            label.setBounds(0, 30, 300, 30);
            dialog.add(label);

            JButton okButton = new JButton("OK");
            okButton.setBounds(100, 80, 100, 30);
            okButton.setBackground(Color.WHITE);
            okButton.setForeground(new Color(60, 179, 113));
            okButton.setFocusPainted(false);
            okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            okButton.addActionListener(ev -> dialog.dispose());
            dialog.add(okButton);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            new LoginUI();
            this.dispose();
        } else {
            toaster.error("Đăng ký thất bại. Vui lòng thử lại!");
        }
    }


//        registerBtn.addActionListener(e -> {
//
//            String username = usernameField.getText().trim();
//            String password = passwordField.getText().trim();
//            acc.register(username, password);
//            // Giả lập kiểm tra và lưu thông tin (bạn có thể thay bằng kiểm tra DB)
//            if (!username.isEmpty() && !password.isEmpty()) {
//                JDialog dialog = new JDialog(this, "🎉 Đăng ký thành công", true);
//                dialog.setUndecorated(true);
//                dialog.setSize(300, 150);
//                dialog.setLayout(null);
//                dialog.getContentPane().setBackground(new Color(60, 179, 113)); // Xanh lá pastel
//
//                JLabel label = new JLabel("🎉 Đăng ký thành công!", SwingConstants.CENTER);
//                label.setFont(new Font("Segoe UI", Font.BOLD, 16));
//                label.setForeground(Color.WHITE);
//                label.setBounds(0, 30, 300, 30);
//                dialog.add(label);
//
//                JButton okButton = new JButton("OK");
//                okButton.setBounds(100, 80, 100, 30);
//                okButton.setBackground(Color.WHITE);
//                okButton.setForeground(new Color(60, 179, 113));
//                okButton.setFocusPainted(false);
//                okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
//                okButton.addActionListener(ev -> dialog.dispose());
//                dialog.add(okButton);
//
//                dialog.setLocationRelativeTo(this);
//                dialog.setVisible(true);
//
//                new LoginUI();
//
//                this.dispose();
//            } else {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        });
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new RegisterUI().setVisible(true));
}
    }


