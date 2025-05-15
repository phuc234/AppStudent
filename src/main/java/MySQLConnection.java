import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/studentt";
    private static final String USER = "root";
    private static final String PASSWORD = "ăâêồ̉"; // Thay bằng mật khẩu thực

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
