package truyentranh.vl.model;

public class LvMangaItem {

    private String id, avatar, tentruyen, tacgia, chuong, luotxem;

    public LvMangaItem(String id, String avatar, String tentruyen, String tacgia, String chuong, String luotxem) {
        this.id = id;
        this.avatar = avatar;
        this.tentruyen = tentruyen;
        this.tacgia = tacgia;
        this.chuong = chuong;
        this.luotxem = luotxem;
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

    public String getChuong() {
        return chuong;
    }

    public void setChuong(String chuong) {
        this.chuong = chuong;
    }

    public String getLuotxem() {
        return luotxem;
    }

    public void setLuotxem(String luotxem) {
        this.luotxem = luotxem;
    }

    @Override
    public String toString() {
        return  id + " " + avatar + " " + tentruyen + " " + tacgia + " " + chuong + " " + luotxem;
    }
}