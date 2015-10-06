package com.liu.easyenglishupdate.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.SpiderArticle;
import com.liu.easyenglishupdate.entity.Article;

import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 文章列表页面
 * 实现上拉加载更多，下拉刷新获取网络最新
 */
public class Refresh extends ActionBarActivity {

    ArrayList<HashMap<String, Object>> itemEntities = new ArrayList<HashMap<String, Object>>();
//    MyListView listview = null;
    private  int refreshIndex = 0;
    private  int loadIndex =0;
//  每个页面显示的行数
    private int perReadNum = 6;
    private int articleNum = 0;
    private String type = null;
    View footer, header;// 底顶部布局
    LayoutInflater inflater = null;

//  获取网络状态
    private IntentFilter intentFilter;
//  extends BraodcastReceiver
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
//        deleteDatabase("Articles.db");
//      获取分类
        type = this.getIntent().getAction();
        if(type != null) {
            if (type.equalsIgnoreCase("初级科技")) {
                type = "科技";
            } else if (type.equalsIgnoreCase("初级健康")) {
                type = "健康";
            } else if (type.equalsIgnoreCase("初级教育")) {
                type = "教育";
            } else if (type.equalsIgnoreCase("初级经济")) {
                type = "经济";
            } else if (type.equalsIgnoreCase("初级自然")) {
                type = "自然";
            } else if (type.equalsIgnoreCase("初级其他")) {
                type = "今日";
            }
        }

//      从数据库获取
        articleNum = getArticleNum();
//            Log.e("数据数目", Integer.toString(articleNum));
        InitView();

    }

//   记得要取消注册的广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    public void InitView(){
        loadIndex = articleNum;
        readArticle(articleNum - perReadNum, articleNum);
        //        如果数据库为空 添加默认页面
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        if (itemEntities.isEmpty()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
//            TODO 以后可以加图片
//            map.put("ItemImage", R.drawable.ic_launcher);//加入图片
            map.put("title", "当前没有数据\n下拉刷新从网络获取数据");
            map.put("date", time);
            itemEntities.add(map);
        }
        inflater = LayoutInflater.from(this);

        showListView(itemEntities);


//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//             try {
//                String title = (String) itemEntities.get((int) id).get("title");
//                Intent intent = new Intent();
//                intent.setClass(Refresh.this, Message.class);
//                intent.setAction(title);
//                startActivity(intent);
////                  Log.d("你点击的是" , listview.toString() +","+ position +","+id + ","
////                          + itemEntities.get(position - 1) + itemEntities.get((int)id));
////                  Log.d("你点击的文章题目是",title);
//             }catch (IndexOutOfBoundsException e){
//                 e.printStackTrace();
//                 Toast.makeText(Refresh.this,"点其他哦！",Toast.LENGTH_SHORT).show();
//             }
//            }
//        });
    }


    public int getArticleNum(){
        SQLiteDatabase db = null;
        Cursor c = null;
        int count = 0;
        try {
            db = Connector.getReadableDatabase();
            c = db.rawQuery("select count(*) from 'Article' where catalogy ='"+ type+"'", null);
            if (c.moveToNext()) {
                count = c.getInt(0);
            }
        }catch (Exception e){
             e.printStackTrace();
        }finally {
            if (c != null){
                c.close();
            }

            if(db != null){
            db.close();
            }

            return count;
        }

    }



    private void showListView(ArrayList<HashMap<String, Object>> itemEntities) {
//        listview = (MyListView) findViewById(R.id.mylist);
//        listview.setLoaderListener(this);
//        SimpleAdapter mSimpleAdapter = new SimpleAdapter(Refresh.this,itemEntities,//需要绑定的数据
//                R.layout.lsitviewitem,//每一行的布局//动态数组中的数据源的键对应到定义布局的View中(图片先不加）
//                new String[] {"title", "date"},
//                new int[] {R.id.title,R.id.date}
//        );
//        if(!mSimpleAdapter.isEmpty()) {
//            mSimpleAdapter.notifyDataSetChanged();
//            listview.setAdapter(mSimpleAdapter);//为ListView绑定适配器
//        }
    }

    public void onReflash() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取最新数据
                setReflashData();
                // 通知界面显示
                showListView(itemEntities);
                // 通知listview 刷新数据完毕；
//                listview.reflashComplete();
            }
        }, 2000);
    }

    private void setReflashData() {
        header = inflater.inflate(R.layout.headerview, null);
//      footer = inflater.inflate(R.layout.footview, null);

        TextView tip = (TextView) header.findViewById(R.id.refresh_tips);

        refreshIndex = articleNum + 1;
        new Thread(){

            public void run(){
                  writeArticle();
            }
        }.start();
        articleNum = getArticleNum();
//        Log.d("下拉刷新索引开始" + refreshIndex, "结束" + articleNum);
        if (refreshIndex < articleNum){
            readArticle(refreshIndex, articleNum);
        }else {
            Toast.makeText(this,"已经是最新的啦！",Toast.LENGTH_SHORT).show();
        }
    }


    public void onLoad() {
         Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run () {
                 // 获取更多数据
                 loadData();
                 // 更新listview显示；
                 showListView(itemEntities);
                 // 通知listview加载完毕
//                 listview.loadComplete();
             }
        }, 1000);
    }


    private void loadData() {
//        header = inflater.inflate(R.layout.top, null);
        footer = inflater.inflate(R.layout.footview, null);
        TextView lt = (TextView) findViewById(R.id.loader);

        loadIndex = loadIndex - perReadNum;
        int fromIndex = loadIndex - perReadNum +1;
        if(fromIndex < 0)
        {
            fromIndex = 0;
        }
        if (loadIndex < 0){
            loadIndex = 0;
        }
        if (loadIndex != 0) {
            readArticle(fromIndex, perReadNum);
        }else{
            Toast.makeText(this,"已读完了哦！",Toast.LENGTH_SHORT).show();
        }
//        Log.d("上拉刷新 begin" + fromIndex, "end" + loadIndex);

    }


    public void readArticle(int firstIndex, int perReadNum){
        ArrayList<Article> dbal = new ArrayList<Article>();
        SQLiteDatabase db = null;
        Article article = null;
        Cursor c = null;
        String title = null;
        String time = null;
        try {
            db = Connector.getReadableDatabase();
            if (articleNum > 0) {
                c = db.rawQuery("SELECT * FROM Article  WHERE catalogy ='"+type+"' limit " +firstIndex +"," + perReadNum, null);
                while (c.moveToNext()) {
                    String url = c.getString(c.getColumnIndex("url"));
                    title = c.getString(c.getColumnIndex("title"));
                    String catalogy = c.getString(c.getColumnIndex("catalogy"));
                    String body = c.getString(c.getColumnIndex("body"));
                    String level = c.getString(c.getColumnIndex("level"));
                    int difficultRatio = c.getInt(c.getColumnIndex("difficultRatio"));
                    time = c.getString(c.getColumnIndex("time"));
//                    Log.d("从数据库中读取db", "title=>" + title + ",time" + time + ",catalogy" + catalogy);
                    String bodys = body;
                    article = new Article(title, bodys, catalogy);
                    article.setDifficultRatio(100);
                    article.setTime(time);
                    dbal.add(article);
                }
            }else {
                Toast.makeText(this,"数据读取完成",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }
            if(!dbal.isEmpty()) {
                for (int i = dbal.size() - 1; i>= 0; i-- ) {
                    article = dbal.get(i);
                    title = article.getTitle();
                    time = article.getTime();
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemImage", R.drawable.ic_drawer);//加入图片
                    map.put("title", title);
                    map.put("date", time);
                    itemEntities.add(map);
                    //Log.d("从dbal中读取db", "title=>" + title + ",time" + time);
                }
                dbal.clear();
            }else{
                Toast.makeText(this,"当前数据库为空！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void writeArticle(){
//        关于数据库的操作
        ArrayList<String>  urls = null;
        Article article = null;
        SQLiteDatabase db = null;
        Cursor myCursor = null;

        SpiderArticle spiderArticle = new SpiderArticle();

        //插入数据(逆序写入保证读取到最新的）
//        这样在多次插入数据再读取的时候会出现顺序不匹配问题
        try {
             urls = spiderArticle.getUrlList(type);
//             Log.d("网址",urls.toString());

            db = Connector.getWritableDatabase();
//            db.execSQL("DROP TABLE IF EXISTS Article");
//            Log.i("提示","表删除成功");
//          判断表是否存在
            myCursor = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='Article'", null);
            if (myCursor.moveToNext()) {
                int count = myCursor.getInt(0);
                if (count > 0) {
//                    Log.i("网络获取文章数目", Integer.toString(urls.size()));
//                    TODO 调试完成之后修改参数
                    for (int i = urls.size()-1; i > urls.size() -51; i--) {
                        String url = urls.get(i);
                        try {
                            article = spiderArticle.getPassage(url);
                            String title = article.getTitle();
                            String catalogy = article.getCatalogy();
                            int difficultRatio = article.getDifficultRatio();
                            String body = article.getBody();
                            String time = article.getTime();
//                            Log.d("从网络获取文章时间", time);
                            db.execSQL("INSERT INTO Article VALUES (?,?,?,?,?,?)", new Object[]{url, title, catalogy, body
                                    , difficultRatio, time});
                        } catch (IllegalArgumentException e) {
                            Log.w("警告", "article arguments is illegal!");
                            continue;
                        } catch (SQLiteConstraintException e1) {
                            Log.w("警告", "表中已经存在！");
                            continue;
                        }
                    }
                } else {
                    db.execSQL("CREATE TABLE Article (url VARCHAR PRIMARY KEY,title VARCHAR," +
                            "catalogy VARCHAR, body VARCHAR, level VARCHAR, difficultRatio INT, time VARCHAR)");
                    Log.e("数据库", "表创建成功");
//                    TODO 调试完成之后修改参数
//                    Log.i("网络获取文章数目", Integer.toString(urls.size()));
                    for (int i = urls.size()-1; i > urls.size()-51; i--) {
                        String url = urls.get(i);
                        try {
                            article = spiderArticle.getPassage(url);
                            String title = article.getTitle();
                            String catalogy = article.getCatalogy();
                            int difficultRatio = article.getDifficultRatio();
                            String body = article.getBody();
                            String time = article.getTime();
//                            Log.d("从网络获取文章时间", time);
                            db.execSQL("INSERT INTO Article VALUES (?,?,?,?,?,?)", new Object[]{url, title, catalogy, body
                                    , difficultRatio, time});
                        } catch (IllegalArgumentException e) {
                            Log.w("警告", "article arguments is illegal!");
                            continue;
                        } catch (SQLiteConstraintException e1) {
                            Log.w("警告", "表中数据已经存在！");
                            continue;
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("捕获","异常");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement 多个菜单选项时使用
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    get net state
    class NetworkChangeReceiver extends BroadcastReceiver{
//  必须要在主配置文件中设置否则会挂掉
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            try {
                NetworkInfo networkInfo =  networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    Toast.makeText(context, "连接失败，请检查网络!", Toast.LENGTH_LONG).show();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
//                Log.d("获取网络状态","失败");
            }
        }
    }
}
