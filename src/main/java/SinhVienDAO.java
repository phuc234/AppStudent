import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {

    // Lấy tất cả sinh viên từ cơ sở dữ liệu
    public List<SinhVien> getAll() {
        List<SinhVien> list = new ArrayList<>();
        String query = "SELECT * FROM sinhvien";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String maSo = rs.getString("maso");
                String hoTen = rs.getString("hoten");
                float diem1 = rs.getFloat("diem1");
                float diem2 = rs.getFloat("diem2");
                float diem3 = rs.getFloat("diem3");
                list.add(new SinhVien(maSo, hoTen, diem1, diem2, diem3));
            }

        } catch (SQLException e) {
            // Log lỗi rõ ràng và thông báo cho người dùng
            e.printStackTrace();
            System.out.println("Lỗi khi truy xuất dữ liệu sinh viên.");
        }
        return list;
    }

    // Thêm một sinh viên mới vào cơ sở dữ liệu
    public boolean addStudent(SinhVien sinhVien) {
        String query = "INSERT INTO sinhvien (maso, hoten, diem1, diem2, diem3) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sinhVien.getMaSo());
            pstmt.setString(2, sinhVien.getHoTen());
            pstmt.setFloat(3, sinhVien.getDiem1());
            pstmt.setFloat(4, sinhVien.getDiem2());
            pstmt.setFloat(5, sinhVien.getDiem3());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi thêm sinh viên vào cơ sở dữ liệu.");
            return false;
        }
    }

    // Xóa một sinh viên khỏi cơ sở dữ liệu
    public boolean deleteStudent(String maSo) {
        String query = "DELETE FROM sinhvien WHERE maso = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSo);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xóa sinh viên khỏi cơ sở dữ liệu.");
            return false;
        }
    }
    public static boolean kiemTraTonTaiMaSo(String maSo) {
        String sql = "SELECT 1 FROM sinhvien WHERE maso = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maSo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Cập nhật thông tin sinh viên
    public boolean updateStudent(SinhVien sinhVien) {
        String query = "UPDATE sinhvien SET hoten = ?, diem1 = ?, diem2 = ?, diem3 = ? WHERE maso = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sinhVien.getHoTen());
            pstmt.setFloat(2, sinhVien.getDiem1());
            pstmt.setFloat(3, sinhVien.getDiem2());
            pstmt.setFloat(4, sinhVien.getDiem3());
            pstmt.setString(5, sinhVien.getMaSo());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật thông tin sinh viên.");
            return false;
        }
    }
}
