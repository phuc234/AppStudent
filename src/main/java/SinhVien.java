import java.io.Serializable;

public class SinhVien implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maSo;
    private String hoTen;
    private float diem1, diem2, diem3;

    // Constructor
    public SinhVien(String maSo, String hoTen, float diem1, float diem2, float diem3) {
        this.maSo = maSo;
        this.hoTen = hoTen;
        this.diem1 = diem1;
        this.diem2 = diem2;
        this.diem3 = diem3;
    }

    // Getters và Setters
    public String getMaSo() {
        return maSo;
    }

    public void setMaSo(String maSo) {
        this.maSo = maSo;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public float getDiem1() {
        return diem1;
    }

    public void setDiem1(float diem1) {
        this.diem1 = diem1;
    }

    public float getDiem2() {
        return diem2;
    }

    public void setDiem2(float diem2) {
        this.diem2 = diem2;
    }

    public float getDiem3() {
        return diem3;
    }

    public void setDiem3(float diem3) {
        this.diem3 = diem3;
    }

    // Tính điểm trung bình
    public String getDiemTrungBinh() {
        float dtb = (diem1 + diem2 + diem3) / 3;
        float rounded = Math.round(dtb * 10) / 10.0f;
        if (rounded == (int) rounded) {
            return String.valueOf((int) rounded); // Hiển thị như "7"
        } else {
            return String.valueOf(rounded); // Hiển thị như "7.3"
        }
    }

    // Xếp loại
    public String getXepLoai() {
        float dtb = (diem1 + diem2 + diem3) / 3;
        float rounded = Math.round(dtb * 10) / 10.0f;

        if (rounded >= 8.0f) return "Giỏi";
        else if (rounded >= 6.5f) return "Khá";
        else if (rounded >= 5.0f) return "Trung bình";
        else return "Yếu";
    }
}
