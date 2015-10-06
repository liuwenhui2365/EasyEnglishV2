package com.liu.easyenglishupdate.ui;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.PdfParse;
import com.liu.easyenglishupdate.util.Util;

import org.litepal.crud.DataSupport;

import java.io.InputStream;
import java.util.List;

/**
 * pdf解析为字符串
 */
public class PdfParseActivity extends ActionBarActivity {
    private final String TAG = PdfParseActivity.class.getSimpleName();
    private ProgressBar mProgressPdfParse;
    private TextView mTxtPdfContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_parse);
        setTitle(R.string.title_activity_pdf_parse);

        mProgressPdfParse = (ProgressBar)findViewById(R.id.progress_pdfParse);
        mTxtPdfContent = (TextView)findViewById(R.id.pdf_content);
        openPdfFile();
        List<Article> articles = DataSupport.where("title = ?","pdfTest").find(Article.class);
        if (articles.size() == 0) {
            //测试用时
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Util.d(TAG + "开始解析文档", "开始计时");
                    long start = System.currentTimeMillis();
                    String message = PdfParse.readFdf(getResources().openRawResource(R.raw.english));
                    long end = System.currentTimeMillis();
                    Util.d(TAG + "结束文档解析", "耗时" + (end - start) / 1000 + "秒");
                    Message msg = handler.obtainMessage();
                    msg.obj = message;
                    handler.sendMessage(msg);
                }
            }).start();
        }else {
            Util.showToast(PdfParseActivity.this,R.string.pdf_parse_already);
        }

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String)msg.obj;
            Article pdfTest = new Article();
            pdfTest.setTitle("pdfTest");
            pdfTest.setDifficultRatio(5);
            pdfTest.setCatalogy("pdf");
            pdfTest.setTime("2015-10-03");
            pdfTest.setBody(result);
            pdfTest.save();
            if (pdfTest.isSaved()){
                Util.d(TAG+"pdf文章","存储成功！");
                Util.showToast(PdfParseActivity.this,"文章存储成功！");
            }
            int num = DataSupport.count(Article.class);
            mTxtPdfContent.setText(result);
        }
    };

    /**
     * 查看手机中pdf文件并获取数据
     */
    private void openPdfFile() {
        Intent toFindPdf = new Intent(Intent.ACTION_GET_CONTENT);
        toFindPdf.addCategory(Intent.CATEGORY_OPENABLE);
        toFindPdf.setType("application/pdf");
        try {
            startActivityForResult(toFindPdf, 5);
        } catch (android.content.ActivityNotFoundException e){
            Util.showToast(this,R.string.file_manager_not_exist);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5){

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdf_parse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
