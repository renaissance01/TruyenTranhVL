package truyentranh.vl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import truyentranh.vl.model.DbDaXem;
import truyentranh.vl.model.DbItem;
import truyentranh.vl.model.DbTaiTruyen;
import truyentranh.vl.model.DbView;

public class Database {

    private SQLiteDatabase database;
    private String DATABASE_NAME = "db_truyen";
    private String TABLE_TRUYEN = "tb_truyen";
    private String TABLE_VIEW = "tb_dangxem";
    private String TABLE_TAI = "tb_taitruyen";
    private String TABLE_DAXEM = "tb_daxem";
    private String IDTRUYEN = "idtruyen";
    private String DATHICH = "dathich";
    private String TIME = "time";
    private String LICHSU = "lichsu";
    private String IDCHAP = "idchap";
    private String TENCHAP = "tenchap";
    private String TRANG = "trang";
    private String TENTRUYEN = "tentruyen";
    private String TACGIA = "tacgia";
    private String AVATAR = "avatar";

    public void Database() {
    }

    public Database(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME,
                context.MODE_PRIVATE, null);
        createBatch();
    }

    public void createBatch() {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRUYEN + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + IDTRUYEN + " INTEGER NOT NULL DEFAULT 0, "
                + TIME + " LONG NOT NULL DEFAULT 0, "
                + DATHICH + " INTEGER NOT NULL DEFAULT 0, "
                + LICHSU + " INTEGER NOT NULL DEFAULT 0);");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VIEW + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + IDTRUYEN + " INTEGER NOT NULL DEFAULT 0, "
                + TENTRUYEN + " TEXT NOT NULL, "
                + IDCHAP + " INTEGER NOT NULL DEFAULT 0, "
                + TENCHAP + " TEXT NOT NULL, "
                + TRANG + " INTEGER NOT NULL DEFAULT 0, "
                + TIME + " LONG NOT NULL DEFAULT 0);");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DAXEM + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + IDTRUYEN + " INTEGER NOT NULL DEFAULT 0, "
                + IDCHAP + " INTEGER NOT NULL DEFAULT 0);");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TAI + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + IDTRUYEN + " INTEGER NOT NULL DEFAULT 0, "
                + TENTRUYEN + " TEXT NOT NULL, "
                + TACGIA + " TEXT NOT NULL, "
                + AVATAR + " TEXT NOT NULL, "
                + IDCHAP + " INTEGER NOT NULL DEFAULT 0, "
                + TENCHAP + " TEXT NOT NULL, "
                + TIME + " LONG NOT NULL DEFAULT 0);");
    }

    public boolean addTruyen(DbItem item) {
        ContentValues values = new ContentValues();
        values.put(IDTRUYEN, item.getId());
        values.put(TIME, item.getTime());
        values.put(LICHSU, item.getLichsu());
        if (database.insert(TABLE_TRUYEN, null, values) == -1) {
            return false;
        }
        return true;
    }

    public boolean getId(String id) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_TRUYEN + " WHERE " + IDTRUYEN + " == '" + id + "';", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getCount(int vitri, String id) {
        Cursor c = database.rawQuery("select * from " + TABLE_TRUYEN + " WHERE " + IDTRUYEN + " == '" + id + "';", null);
        c.moveToFirst();
        int thongke = c.getInt(vitri);
        c.close();
        return thongke;
    }

    public void updateTruyen(String column, String id, int giatrimoi, long time) {
        database.execSQL("UPDATE " + TABLE_TRUYEN + " SET " + column + " == '" + giatrimoi + "', " + TIME + " == " + time + " WHERE " + IDTRUYEN + " == '" + id + "';");
    }

    public ArrayList<String> getTruyen() {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("select * from " + TABLE_TRUYEN + " ORDER BY " + TIME + " DESC", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(1);
            list.add(String.valueOf(id));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public boolean addYeuThich(DbItem item) {
        ContentValues values = new ContentValues();
        values.put(IDTRUYEN, item.getId());
        values.put(TIME, item.getTime());
        if (database.insert(TABLE_TRUYEN, null, values) == -1) {
            return false;
        }
        return true;
    }

    public void updateYeuThich(String id, long time) {
        database.execSQL("UPDATE " + TABLE_TRUYEN + " SET " + DATHICH + " == 1, " + TIME + " == " + time + " WHERE " + IDTRUYEN + " == '" + id + "';");
    }

    public void xoaYeuThich(String id) {
        database.execSQL("UPDATE " + TABLE_TRUYEN + " SET " + DATHICH + " == 0 WHERE " + IDTRUYEN + " == '" + id + "';");
    }

    public ArrayList<String> getDaThich() {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("select * from " + TABLE_TRUYEN + " WHERE " + DATHICH + " == '1' ORDER BY " + TIME + " DESC", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(1);
            list.add(String.valueOf(id));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public ArrayList<String> getLichSu() {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = database.rawQuery("select * from " + TABLE_TRUYEN + " WHERE " + LICHSU + " == '1' ORDER BY " + TIME + " DESC", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(1);
            list.add(String.valueOf(id));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public void xoaLichSu() {
        database.execSQL("UPDATE " + TABLE_TRUYEN + " SET " + LICHSU + " == 0;");
    }

    public boolean addDangXem(DbView view) {
        ContentValues values = new ContentValues();
        values.put(IDTRUYEN, view.getIdtruyen());
        values.put(TENTRUYEN, view.getTentruyen());
        values.put(IDCHAP, view.getIdchap());
        values.put(TENCHAP, view.getTenchap());
        values.put(TRANG, view.getTrang());
        values.put(TIME, view.getTime());
        if (database.insert(TABLE_VIEW, null, values) == -1) {
            return false;
        }
        return true;
    }

    public void xoaDangXem() {
        database.execSQL("DELETE FROM " + TABLE_VIEW);
    }

    public void updateDangXem(DbView view) {
        database.execSQL("UPDATE " + TABLE_VIEW + " SET " + TENTRUYEN + " == '" + view.getTentruyen() + "', " + IDCHAP + " == '" + view.getIdchap() + "', " + TENCHAP + " == '" + view.getTenchap() + "', " + TRANG + " == '" + view.getTrang() + "', " + TIME + " == '" + view.getTime() + "' where " + IDTRUYEN + " == '" + view.getIdtruyen() + "'");
    }

    public boolean checkDangXem(String id) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " WHERE " + IDTRUYEN + " == '" + id + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDangXemZIP(String tentruyen) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " WHERE " + TENTRUYEN + " == '" + tentruyen + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<DbView> getDangXem() {
        ArrayList<DbView> list = new ArrayList<DbView>();
        Cursor c = database.rawQuery("select * from " + TABLE_VIEW + " order by " + TIME + " DESC limit 1", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String idtruyen = c.getString(1);
            String tentruyen = c.getString(2);
            String idchap = c.getString(3);
            String tenchap = c.getString(4);
            String trang = c.getString(5);
            long time = c.getLong(6);
            list.add(new DbView(idtruyen, tentruyen, idchap, tenchap, trang, time));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public ArrayList<DbView> getDangXemChap(String id) {
        ArrayList<DbView> list = new ArrayList<DbView>();
        Cursor c = database.rawQuery("select * from " + TABLE_VIEW + " WHERE " + IDTRUYEN + " == '" + id + "' order by " + TIME + " DESC limit 1", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String idtruyen = c.getString(1);
            String tentruyen = c.getString(2);
            String idchap = c.getString(3);
            String tenchap = c.getString(4);
            String trang = c.getString(5);
            long time = c.getLong(6);
            list.add(new DbView(idtruyen, tentruyen, idchap, tenchap, trang, time));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public boolean checkTrang() {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + ";", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTrangDangXem(String tenchap) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " WHERE " + TENCHAP + " == '" + tenchap + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTrangDangXem2(String idtruyen, String idchap) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " WHERE " + IDTRUYEN + " == '" + idtruyen + "' and " + IDCHAP + " == '" + idchap + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTrangDangXem3(String idtruyen) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " order by " + TIME + " DESC limit 1", null);
        c.moveToFirst();
        long time = c.getLong(6);
        c.close();

        Cursor c2 = database.rawQuery("SELECT * FROM " + TABLE_VIEW + " WHERE " + IDTRUYEN + " == '" + idtruyen + "' and " + TIME + " == '" + time + "'", null);
        if (c2.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    //Xử lý truyện và chap đã xem
    public boolean addDaXem(DbDaXem daxem) {
        ContentValues values = new ContentValues();
        values.put(IDTRUYEN, daxem.getIdtruyen());
        values.put(IDCHAP, daxem.getIdchap());
        if (database.insert(TABLE_DAXEM, null, values) == -1) {
            return false;
        }
        return true;
    }

    public ArrayList<DbDaXem> getDaXem(String idchap) {
        ArrayList<DbDaXem> list = new ArrayList<DbDaXem>();
        Cursor c = database.rawQuery("select * from " + TABLE_DAXEM + " WHERE " + IDCHAP + " == '" + idchap + "'", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String idtruyen = c.getString(1);
            String idchap2 = c.getString(2);
            list.add(new DbDaXem(idtruyen, idchap2));
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public boolean checkIdChapDaXem(String idchap) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_DAXEM + " WHERE " + IDCHAP + " == '" + idchap + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    //Xử lý trang đã tải
    public boolean addTaiTruyen(DbTaiTruyen view) {
        ContentValues values = new ContentValues();
        values.put(IDTRUYEN, view.getIdtruyen());
        values.put(TENTRUYEN, view.getTentruyen());
        values.put(TACGIA, view.getTacgia());
        values.put(AVATAR, view.getAvatar());
        values.put(IDCHAP, view.getIdchap());
        values.put(TENCHAP, view.getTenchap());
        values.put(TIME, view.getTime());
        if (database.insert(TABLE_TAI, null, values) == -1) {
            return false;
        }
        return true;
    }

    public boolean getIdTaiTruyen(String idtruyen, String idchap) {
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_TAI + " WHERE " + IDTRUYEN + " == '" + idtruyen + "' AND " + IDCHAP + " == '" + idchap + "'", null);
        if (c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void xoaTaiTruyen(String idtruyen, String idchap) {
        database.execSQL("DELETE FROM " + TABLE_TAI + " WHERE " + IDTRUYEN + " == '" + idtruyen + "' AND " + IDCHAP + " == '" + idchap + "'");
    }

    public ArrayList<DbTaiTruyen> getTaiTruyen() {
        ArrayList<DbTaiTruyen> list = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_TAI + " ORDER BY " + TIME + " DESC", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int idtruyen = c.getInt(1);
            String tentruyen = c.getString(2);
            String tacgia = c.getString(3);
            String avatar = c.getString(4);
            int idchap = c.getInt(5);
            String tenchap = c.getString(6);
            long time = c.getLong(7);
            list.add(new DbTaiTruyen(String.valueOf(idtruyen), tentruyen, tacgia, avatar, String.valueOf(idchap), tenchap, time));
            c.moveToNext();
        }
        c.close();
        return list;
    }

}
