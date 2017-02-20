package truyentranh.vl.model;

public class DbView {

    private String idtruyen, tentruyen, idchap, tenchap, trang;
    private long time;

    public DbView(String idtruyen, String tentruyen, String idchap, String tenchap, String trang, long time) {
        this.idtruyen = idtruyen;
        this.tentruyen = tentruyen;
        this.idchap = idchap;
        this.tenchap = tenchap;
        this.trang = trang;
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

    public String getTrang() {
        return trang;
    }

    public void setTrang(String trang) {
        this.trang = trang;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
