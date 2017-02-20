package truyentranh.vl.model;

public class LvChapItem {

    private String idtruyen, idchap, tenchap, ngay, zipchap;

    public LvChapItem(String idtruyen, String idchap, String tenchap, String ngay, String zipchap) {
        this.idtruyen = idtruyen;
        this.idchap = idchap;
        this.tenchap = tenchap;
        this.ngay = ngay;
        this.zipchap = zipchap;
    }

    public String getIdtruyen() {
        return idtruyen;
    }

    public void setIdtruyen(String idtruyen) {
        this.idtruyen = idtruyen;
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

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getZipchap() {
        return zipchap;
    }

    public void setZipchap(String zipchap) {
        this.zipchap = zipchap;
    }
}
