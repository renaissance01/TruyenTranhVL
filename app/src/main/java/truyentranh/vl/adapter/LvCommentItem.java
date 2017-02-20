package truyentranh.vl.adapter;

/**
 * Created by RONGLUFFY on 17/02/2017.
 */

public class LvCommentItem {

    private String stt, hoten, thoigian, noidung;

    public LvCommentItem(String stt, String hoten, String thoigian, String noidung) {
        this.stt = stt;
        this.hoten = hoten;
        this.thoigian = thoigian;
        this.noidung = noidung;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getThoigian() {
        return thoigian;
    }

    public void setThoigian(String thoigian) {
        this.thoigian = thoigian;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }
}
