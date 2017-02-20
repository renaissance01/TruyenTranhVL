package truyentranh.vl.model;

public class DbDaXem {
    private String idtruyen, idchap;

    public DbDaXem(String idtruyen, String idchap) {
        this.idtruyen = idtruyen;
        this.idchap = idchap;
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
}
