package truyentranh.vl.model;

public class InfoItem {

    private String avatar, tentruyen, tenkhac, tacgia, tinhtrang, sochap, mota;

    public InfoItem(String avatar, String tentruyen, String tenkhac, String tacgia, String tinhtrang, String sochap, String mota) {
        this.avatar = avatar;
        this.tentruyen = tentruyen;
        this.tenkhac = tenkhac;
        this.tacgia = tacgia;
        this.tinhtrang = tinhtrang;
        this.sochap = sochap;
        this.mota = mota;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTentruyen() {
        return tentruyen;
    }

    public void setTentruyen(String tentruyen) {
        this.tentruyen = tentruyen;
    }

    public String getTenkhac() {
        return tenkhac;
    }

    public void setTenkhac(String tenkhac) {
        this.tenkhac = tenkhac;
    }

    public String getTacgia() {
        return tacgia;
    }

    public void setTacgia(String tacgia) {
        this.tacgia = tacgia;
    }

    public String getTinhtrang() {
        return tinhtrang;
    }

    public void setTinhtrang(String tinhtrang) {
        this.tinhtrang = tinhtrang;
    }

    public String getSochap() {
        return sochap;
    }

    public void setSochap(String sochap) {
        this.sochap = sochap;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }
}
