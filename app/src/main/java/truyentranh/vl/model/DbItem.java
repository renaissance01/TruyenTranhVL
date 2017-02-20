package truyentranh.vl.model;

public class DbItem {

    private String id, view, like, dislike, lichsu;
    private long time;

    public DbItem(String id, String view, String like, String dislike, long time, String lichsu) {
        this.id = id;
        this.view = view;
        this.like = like;
        this.dislike = dislike;
        this.lichsu = lichsu;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDislike() {
        return dislike;
    }

    public void setDislike(String dislike) {
        this.dislike = dislike;
    }

    public String getLichsu() {
        return lichsu;
    }

    public void setLichsu(String lichsu) {
        this.lichsu = lichsu;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
