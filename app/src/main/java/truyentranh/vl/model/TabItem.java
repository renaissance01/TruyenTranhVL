package truyentranh.vl.model;

public class TabItem {

    private String txtTen;
    private int tvIcon;

    public TabItem(int tvIcon, String txtTen) {
        this.tvIcon = tvIcon;
        this.txtTen = txtTen;
    }

    public int getTvIcon() {
        return tvIcon;
    }

    public void setTvIcon(int tvIcon) {
        this.tvIcon = tvIcon;
    }

    public String getTxtTen() {
        return txtTen;
    }

    public void setTxtTen(String txtTen) {
        this.txtTen = txtTen;
    }
}
