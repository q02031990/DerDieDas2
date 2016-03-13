package tiengduc123.com.derdiedas;

/**
 * Created by qadmin on 21.12.15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by qadmin on 19.12.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Derdiedas.db";
    public static final String TABLE_NAME = "tbl_woerter";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "ARTIKEL";
    public static final String COL_3 = "WOERTER";
    public static final String COL_4 = "WOERTER_SUCHEN";
    public static final String COL_5 = "DEFINITION";

    public boolean checkDBExit(){
        File file = new File("/data/data/tiengduc123.com.derdiedas/databases/Derdiedas.db");
        long fileExists = getFolderSize(file);
        if(fileExists < 500) {
            return false;
        }
        return true;

    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return (size/1024);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ARTIKEL TEXT, WOERTER TEXT, WOERTER_SUCHEN TEXT, DEFINITION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean updateData(String id, String ARTIKEL,String WOERTER,String WOERTER_SUCHEN,String DEFINITION){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,ARTIKEL);
        contentValues.put(COL_3,WOERTER);
        contentValues.put(COL_4,WOERTER_SUCHEN);
        contentValues.put(COL_5, DEFINITION);

        String where = "ID = '" + id + "'";

        long resualt = db.update(TABLE_NAME, contentValues, where, null);
        //db.delete(TABLE_NAME,where,null);
        if(resualt == -1)
            return false;
        else
            return true;

    }

    public boolean DeleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "ID = '" + id + "'";
        long resualt = db.delete(TABLE_NAME,where,null);
        if(resualt == -1)
            return false;
        else
            return true;
    }

    public boolean insertData(String ARTIKEL,String WOERTER,String WOERTER_SUCHEN,String DEFINITION){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_1,"1800");
        contentValues.put(COL_2,ARTIKEL);
        contentValues.put(COL_3,WOERTER);
        contentValues.put(COL_4,WOERTER_SUCHEN);
        contentValues.put(COL_5, DEFINITION);
        long resualt = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if(resualt == -1)
            return false;
        else
            return true;
    }

    public woeter searchWordByID(String id)
    {
        woeter a = new woeter("","","","");
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String qr = "SELECT  * FROM " + TABLE_NAME + " where id = " + id ;
        Cursor res = db.rawQuery(qr, null);

        boolean insertOnceRowIntoDatabase = false;

        if(res.getCount() >0) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                String Atikel = res.getString(res.getColumnIndex(COL_2)).toString().trim();

                String word = res.getString(res.getColumnIndex(COL_3));
                String definition = res.getString(res.getColumnIndex(COL_5));

                //if(res.getString(4).toString().trim() == "") {
                Random rn = new Random();
                String _id = res.getString(res.getColumnIndex(COL_1)).toString().trim();

                int n = rn.nextInt();
                int m = rn.nextInt();


                // insert tu dong vao db
                if(definition == null) {
                    String str1 = "http://tiengduc123.com/app/Translate.php?m=" + m + "&n=" + n + "&word=" + word;

                    definition =readFileFromInternet(str1);

                    boolean resualt = updateData(id, Atikel, word, word, definition);
                    if (resualt) {
                            //Toast.makeText(getApplicationContext(), "You need to connect to the Internet", Toast.LENGTH_LONG).show();
                    }
                }
                //}

                if (Atikel.equalsIgnoreCase("1") || Atikel.equalsIgnoreCase("3")) {
                    Atikel = "der";
                } else if (Atikel.equalsIgnoreCase("2")) {
                    Atikel = "die";
                } else if (Atikel.equalsIgnoreCase("4")) {
                    Atikel = "das";
                }

                a = new woeter(Atikel,word, definition, _id);
                res.moveToNext();
            }
        }
        return a;
    }


    public ArrayList<woeter> searchWord(String str)
    {
        ArrayList<woeter> array_list = new ArrayList<woeter>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String qr = "SELECT  * FROM " + TABLE_NAME + " where WOERTER_SUCHEN like '" + str + "%' limit 55" ;
        Cursor res = db.rawQuery(qr, null);

        boolean insertOnceRowIntoDatabase = false;

        if(res.getCount() >1) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                String Atikel = res.getString(res.getColumnIndex(COL_2)).toString().trim();

                String word = res.getString(res.getColumnIndex(COL_3));
                String definition = res.getString(res.getColumnIndex(COL_5));
                if(definition == null){
                    definition = "";
                }else if(definition == ""){

                }else {
                    definition = " : " + definition;
                }

                //if(res.getString(4).toString().trim() == "") {
                Random rn = new Random();
                String _id = res.getString(res.getColumnIndex(COL_1)).toString().trim();

                int n = rn.nextInt();
                int m = rn.nextInt();

                if (Atikel.equalsIgnoreCase("1") || Atikel.equalsIgnoreCase("3")) {
                    Atikel = "der";
                } else if (Atikel.equalsIgnoreCase("2")) {
                    Atikel = "die";
                } else if (Atikel.equalsIgnoreCase("4")) {
                    Atikel = "das";
                }

                woeter a = new woeter(Atikel,word,definition, _id);
                array_list.add(a);
                res.moveToNext();
            }
        }
        return array_list;
    }


    public String readFileFromInternet(final String url){
        URLConnection feedUrl;
        String str = "";
        try {
            feedUrl = new URL(url).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();
            str = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}