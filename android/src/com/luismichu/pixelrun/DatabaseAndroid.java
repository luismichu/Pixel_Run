package com.luismichu.pixelrun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.badlogic.gdx.utils.Array;


import java.sql.PreparedStatement;

public class DatabaseAndroid extends Database{
    SQLiteOpenHelper connection;
    SQLiteDatabase db;

    public DatabaseAndroid(Context context) {
        connection = new AndroidDB(context, database_name, null, version);
        db = connection.getWritableDatabase();
    }

    @Override
    void drop(){
        try {
            String sql = "DROP TABLE IF EXISTS scores";
            db.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS scores (id integer primary key autoincrement, name text, score int)";
            db.execSQL(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    void insertar(Score score) {
        String sql = "CREATE TABLE IF NOT EXISTS scores (id integer primary key autoincrement, name text, score int)";
        db.execSQL(sql);

        ContentValues v = new ContentValues();
        v.put("name", score.name);
        v.put("score", score.score);
        db.insertOrThrow("scores", null, v);
    }

    @Override
    Array<Score> leer() {
        String[] SELECT = {"name", "score"};
        Cursor cursor = db.query("scores", SELECT, null, null, null, null,
                "score", "5");

        Array<Score> scores = new Array<>();
        Score score = new Score();
        while (cursor.moveToNext()) {
            score.name = cursor.getString(0);
            score.score = cursor.getInt(1);
            scores.add(score);
        }
        cursor.close();
        return scores;
    }

    class AndroidDB extends SQLiteOpenHelper {
        public AndroidDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            DatabaseAndroid.this.db = db;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
