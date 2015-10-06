package com.liu.easyenglishupdate.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.Translate;
import com.liu.easyenglishupdate.entity.Article;

import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/4/1.
 * 用来显示已分享到的文章内容
 */
public class ShareMessage extends ActionBarActivity {
    TextView textView;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置没有标题
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_message);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date curDate = new Date();
        String time = simpleDateFormat.format(curDate);

        final String title = this.getIntent().getAction();
        textView = (TextView)findViewById(R.id.sharemessage_title);
        textView.setText(title);
        textView = (TextView)findViewById(R.id.sharemessage_time);
        textView.setText(time);
//      单词分类优化到翻译类中
        readArticle(title);

    }


    public void readArticle(String title){

        Article article = null;
        Cursor c = null;
        try {
            db = Connector.getReadableDatabase();
            Translate translate = new Translate(this);
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='ShareArticle'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                    Log.d("读取数据库文章开始","gogo");
//                    Log.d("known"+knownWords.size(),"unknown"+unknownWords.size());
                    c = db.rawQuery("SELECT * FROM ShareArticle  WHERE title = ?", new String[]{title});
                    while (c.moveToNext()) {
                        title = c.getString(c.getColumnIndex("title"));
                        String body = c.getString(c.getColumnIndex("body"));
                        String time = c.getString(c.getColumnIndex("time"));
                        String catalogy = c.getString(c.getColumnIndex("catalogy"));
                        String sBody = body;
                        article = new Article(title,sBody,catalogy);

                        article = translate.translate(article);

                        textView = (TextView)findViewById(R.id.sharemessage_title);
                        textView.setText(title);
                        textView = (TextView) findViewById(R.id.sharemessage_time);
                        textView.setText(time);
                        textView = (TextView) findViewById(R.id.sharemessage_content);
                        textView.setText(article.getBody());
                        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                    }
                }else {
                    Toast.makeText(this,"还没有分享到的文章哦！",Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("数据库异常", "ERROR");
        }finally {
            if(c != null){
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}
