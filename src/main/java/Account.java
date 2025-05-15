import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {

    // Đăng ký tài khoản
    public static boolean register(String username, String password) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Có thể mã hóa mật khẩu ở đây nếu muốn
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Đăng ký thất bại: " + e.getMessage());
            return false;
        }
    }

    // Kiểm tra đăng nhập
    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có dòng dữ liệu => đăng nhập đúng

        } catch (SQLException e) {
            System.out.println("Lỗi đăng nhập: " + e.getMessage());
            return false;
        }
    }

    // Kiểm tra tài khoản đã tồn tại chưa
    public static boolean isUsernameExists(String username) {
        String sql = "SELECT * FROM account WHERE username = ?";
        try (Connection conn =MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra tài khoản: " + e.getMessage());
            return false;
        }
    }
}
