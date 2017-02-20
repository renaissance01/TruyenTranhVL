package truyentranh.vl.model;

public class LvTaiTruyen {

    private String id, avatar, tentruyen, tacgia, chap;

    public LvTaiTruyen(String id, String avatar, String tentruyen, String tacgia, String chap) {
        this.id = id;
        this.avatar = avatar;
        this.tentruyen = tentruyen;
        this.tacgia = tacgia;
        this.chap = chap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTacgia() {
        return tacgia;
    }

    public void setTacgia(String tacgia) {
        this.tacgia = tacgia;
    }

    public String getChap() {
        return chap;
    }

    public void setChap(String chap) {
        this.chap = chap;
    }
}
