package truyentranh.vl.model;

public class LvTheLoai {

    private int id;
    private String theloai;
    private boolean isChecked;
    private boolean check;

    public LvTheLoai(int id, String theloai) {
        this.id = id;
        this.theloai = theloai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTheloai() {
        return theloai;
    }

    public void setTheloai(String theloai) {
        this.theloai = theloai;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}