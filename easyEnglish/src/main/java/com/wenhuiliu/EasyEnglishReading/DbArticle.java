package com.wenhuiliu.EasyEnglishReading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Administrator on 2015/3/3.
 * 数据库类
 */
public class DbArticle extends SQLiteOpenHelper
{
    //重写构造方法,可以改为(Context context, int version)只要这两参数
    public DbArticle(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version)
    {

        super(context, "articles.db", null, version);
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS Article");
        //创建person表
        db.execSQL("CREATE TABLE Article (url VARCHAR PRIMARY KEY,title VARCHAR," +
                "catalogy VARCHAR, body VARCHAR, level VARCHAR, difficultRatio INT, time VARCHAR)");
        Log.e("数据库","表创建成功");
    }

    //升级表（当Database的Version低于当前new里的Version，直接执行下面方法）
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        int v = newVersion - oldVersion;
        switch (v)
        {
            case 3:
                db.execSQL("ALTER TABLE person ADD salary3 VARCHAR(20)");
            case 2:
                db.execSQL("ALTER TABLE person ADD salary2 VARCHAR(20)");
            case 1:
                db.execSQL("ALTER TABLE person ADD salary1 VARCHAR(20)");
            default:
                break;
        }
    }

}