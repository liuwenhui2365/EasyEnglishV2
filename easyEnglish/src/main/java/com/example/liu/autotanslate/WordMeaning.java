package com.example.liu.autotanslate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.wenhuiliu.EasyEnglishReading.DbArticle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WordMeaning extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        TextView textView = null;
        String word = this.getIntent().getAction();
        textView = (TextView)findViewById(R.id.dis_word);
        textView.setText(word);
        textView = (TextView)findViewById(R.id.meaning);
        textView.setText(FindWordMeaning(word));


    }

    private String FindWordMeaning(String word) {
        HashMap<String, String> wordMeaning = new HashMap<String, String>();
        String [] words = new String[]{};
        BufferedReader reader = null;
        InputStream input = null;
        String line = null;
        String meaning = null;

        DbArticle dbArticle = null;
        SQLiteDatabase db = null;
        Cursor c = null;
//      表已经创建
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
    //                Log.d("查询表的个数",count+"");
                if (count > 0) {
                    c = db.rawQuery("select meaning from words where word = ?", new String[]{word});
                    if (c.moveToNext()) {
                        meaning = c.getString(c.getColumnIndex("meaning"));
    //                        Log.d("从数据库中读取wordMeaning", "meaning=>" + meaning);
                    } else {
                        //TODO 添加弹框提示数据库读取完毕
                        Log.e("错误", "数据库没有该单词");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }

            return meaning;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_meaning, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
