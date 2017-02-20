package truyentranh.vl.model;

public class DbTaiTruyen {

    private String idtruyen, tentruyen, tacgia, avatar, idchap, tenchap;
    private long time;

    public DbTaiTruyen(String idtruyen, String tentruyen, String tacgia, String avatar, String idchap, String tenchap, long time) {
        this.idtruyen = idtruyen;
        this.tentruyen = tentruyen;
        this.tacgia = tacgia;
        this.avatar = avatar;
        this.idchap = idchap;
        this.tenchap = tenchap;
        this.time = time;
    }

    public String getIdtruyen() {
        return idtruyen;
    }

    public void setIdtruyen(String idtruyen) {
        this.idtruyen = idtruyen;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdchap() {
        return idchap;
    }

    public void setIdchap(String idchap) {
        this.idchap = idchap;
    }

    public String getTenchap() {
        return tenchap;
    }

    public void setTenchap(String tenchap) {
        this.tenchap = tenchap;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
