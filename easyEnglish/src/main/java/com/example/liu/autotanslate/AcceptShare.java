package com.example.liu.autotanslate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.Article;
import com.wenhuiliu.EasyEnglishReading.DbArticle;
import com.wenhuiliu.EasyEnglishReading.MSpiderChinaDaily;
import com.wenhuiliu.EasyEnglishReading.SpiderEconomicArticle;
import com.wenhuiliu.EasyEnglishReading.Translate;

import java.io.IOException;


public class AcceptShare extends ActionBarActivity {
    public static final int SHOW_DATA = 0;
    private final String CHINADAILY_URL = "http://m.chinadaily.com.cn";
    private final String ECONOMIST_URL = "http://www.economist.com/";
    DbArticle dbArticle;
    SQLiteDatabase db = null;

    String title = null;
    String time = null;
    StringBuilder content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //获得Intent的Action
        String action = intent.getAction();
        //获得Intent的MIME type
        String type = intent.getType();

//      接受到网址才能触发
        if(Intent.ACTION_SEND.equals(action) && type != null){
            //我们这里处理所有的文本类型
            if(type.startsWith("text/")){
                //处理获取到的文本，这里我们用TextView显示
                handleSendText(intent);
//          handleSendText();
            }
        }else {
            Toast.makeText(this,"还没有接受到网址哦！",Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_message);
    }

    /**
     * 用TextView显示文本
     * 可以打开一般的文本文件
     * @param intent
     */
    private void handleSendText(final Intent intent){
          new Thread(new Runnable() {
               @Override
               public void run() {
                   String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    //                String sharedText = "http://m.chinadaily.com.cn/en/trending/2015-04/04/content_20000872.htm";
                   if(sharedText != null) {
                       if (sharedText.contains(CHINADAILY_URL)
                               || sharedText.contains(ECONOMIST_URL)) {
                           try {
                               writeShareArticle(sharedText);
                           } catch (Exception e) {
                               Log.e("写入分享文章出现异常", "查看异常");
                           }
                       } else {
                           Toast.makeText(AcceptShare.this, "暂时不支持哦！", Toast.LENGTH_SHORT).show();
                       }
                   }else {
                       Toast.makeText(AcceptShare.this,"没有获取到网址，请重试！",Toast.LENGTH_SHORT).show();
                   }
                }
            }).start();
    }

//        接受子线程发过来的对象

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Log.d("handler","接受到子线程传过来的对象了");
            switch (msg.what){
                case SHOW_DATA:
                    Article article = (Article)msg.obj;
                    TextView textView = (TextView)findViewById(R.id.title_message);
                    article.getTitle();
                    textView.setText( article.getTitle());
                    textView = (TextView)findViewById(R.id.date_message);
                    textView.setText( article.getTime());
                    textView = (TextView)findViewById(R.id.content);
                    textView.setText( article.getBody());
            }
        }
    };



    public void writeShareArticle(String url){
//        关于数据库的操作
        Article article = null;
        Cursor myCursor = null;
        Translate translate = new Translate(this);
        MSpiderChinaDaily mSpiderChinaDaily = new MSpiderChinaDaily();
        SpiderEconomicArticle spiderEconomicArticle = new SpiderEconomicArticle();

        Log.d("获取到的网址网址",url);

        //插入数据(逆序写入保证读取到最新的）
//        这样在多次插入数据再读取的时候会出现顺序不匹配问题
        try {
            dbArticle = new DbArticle(this, "Articles.db", null, 1);
            db = dbArticle.getWritableDatabase();
//            db.execSQL("DROP TABLE IF EXISTS Article");
//            Log.i("提示","表删除成功");
            myCursor = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='ShareArticle'", null);
            if (myCursor.moveToNext()) {
                int count = myCursor.getInt(0);
                if (count > 0) {
                    try {
                        if (url.contains(CHINADAILY_URL)) {
                            article = mSpiderChinaDaily.getPassage(url);
                        }else if (url.contains(ECONOMIST_URL)){
                            article = spiderEconomicArticle.getPassage(url);
                        }

                        if (article != null) {
                            String title = article.getTitle();
                            Log.d("标题", title);
                            String catalogy = article.getCatalogy();
                            String level = article.getLevel();
                            String time = article.getTime();
                            //                        Log.d("从网络获取文章时间", time);
                            int difficultRatio = article.getDifficultRatio();
                            StringBuilder body = article.getBody();
                            //                        Log.d("网络获取文章内容",body+"");
                            db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title,
                                    catalogy, body, level, difficultRatio, time});
                            article = translate.translate(article, dbArticle);
                            Message message = new Message();
                            message.what = SHOW_DATA;
                            message.obj = article;
                            //                      注意handler是全局对象
                            handler.sendMessage(message);
                        }else {
                            Log.e("警告","获取文章为空");
                            Toast.makeText(this,"获取内容为空，请重试！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        Log.e("警告", "Share表中已经存在！");
                        Toast.makeText(this,"数据已经分享过了哦，去已分享看看吧。",Toast.LENGTH_SHORT).show();
                    } catch (IOException e){
                        Toast.makeText(this,"获取失败，重新分享试试",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    db.execSQL("CREATE TABLE ShareArticle (url VARCHAR PRIMARY KEY,title VARCHAR," +
                            "catalogy VARCHAR, body VARCHAR, level VARCHAR, difficultRatio INT, time VARCHAR)");
                    Log.e("数据库", "share表创建成功");
                    //                    Log.i("网络获取文章数目", Integer.toString(urls.size()));
                    try {
                        article = mSpiderChinaDaily.getPassage(url);
                        String title = article.getTitle();
                        Log.d("获取到的标题",title);
                        String catalogy = article.getCatalogy();
                        String level = article.getLevel();
                        int difficultRatio = article.getDifficultRatio();
                        StringBuilder body = article.getBody();
                        String time = article.getTime();
                        db.execSQL("INSERT INTO ShareArticle VALUES (?,?,?,?,?,?,?)", new Object[]{url, title, catalogy, body
                                , level, difficultRatio, time});
//                        Log.d("开始翻译","ooppoo");
                        article = translate.translate(article,dbArticle);
//                        Log.d("向主线程发送对象","ggg");
                        Log.d("翻译好的文章",article.getBody()+"");
                        Message message = new Message();
                        message.what = SHOW_DATA;
                        message.obj = article;
                        Handler handler = new Handler();
                        handler.sendMessage(message);
                    } catch (IllegalArgumentException e) {
                        Log.e("警告", "article arguments is illegal!");
                    } catch (SQLiteConstraintException e1) {
                        Toast.makeText(this,"数据已经分享过了哦，去已分享看看吧。",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("捕获","异常");
//            Toast.makeText(Refresh.this,"获取数据异常",Toast.LENGTH_SHORT).show();
        }finally {
            if(myCursor!=null){
                myCursor.close();
            }

            if (db!=null){
                db.close();
            }
        }

    }
}
