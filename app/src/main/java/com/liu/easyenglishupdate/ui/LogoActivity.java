package com.liu.easyenglishupdate.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.db.EasyEnglishDB;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.entity.WordTag;
import com.liu.easyenglishupdate.util.Util;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.io.InputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LogoActivity extends Activity {
    public static final String TAG = LogoActivity.class.getSimpleName();
    /**
     *  the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 4000;
    /**
     * 发送成功标志位
     */
    private static final int FINISH_TASK = 0;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo);

        final ImageView controlsView = (ImageView)findViewById(R.id.img_applicstion_icon);
        //后两个参数设置旋转点
        //相对于自身
        RotateAnimation rotateSelf= new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        controlsView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.animation));
        controlsView.startAnimation(rotateSelf);

        initData();
        mHideHandler.sendEmptyMessageDelayed(FINISH_TASK,AUTO_HIDE_DELAY_MILLIS);
    }

    /**
     * 初始化数据库
     * 创建单词表、文章表、词性表
     *
     */
    private void initData() {
        //初始化数据库及创建表
        SQLiteDatabase sqlDb = Connector.getDatabase();
        if (DataSupport.count(Word.class) == 0){
            initWordData();
        }

        if (DataSupport.count(WordTag.class) == 0){
            initWordTagData();
        }

//        DataSupport.deleteAll(Article.class);
//        int num = DataSupport.count(Article.class);
    }


    /**
     * 初始化单词数据
     */
    private void initWordData(){
        final InputStream inputStream = getResources().openRawResource(R.raw.sortwordlist);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = EasyEnglishDB.saveAllWords(inputStream);
                    if (!result){
                        Util.d(TAG,"单词表初始化数据失败！");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化单词词性表数据
     */
    private void initWordTagData(){
        final InputStream inputStream = getResources().openRawResource(R.raw.wordtag);
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                boolean result = EasyEnglishDB.saveAllWordsTag(inputStream);
                if (!result){
                    Util.d(TAG,"单词表初始化数据失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }).start();
    }

    Handler mHideHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent toMain = new Intent(LogoActivity.this,MainActivity.class);
            startActivity(toMain);
            finish();
            super.handleMessage(msg);
        }
    };

}
