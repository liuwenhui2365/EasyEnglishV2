package com.liu.easyenglishupdate.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.entity.Article;
import com.liu.easyenglishupdate.util.MyURLSpan;
import com.liu.easyenglishupdate.util.PdfParse;
import com.liu.easyenglishupdate.util.Translate;
import com.liu.easyenglishupdate.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PdfMessageFragment extends Fragment {
    private final String TAG = "PdfMessageFragment";
    private ProgressBar mProgressPdfParse;
    private TextView mTxtPdfContent;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_pdf_parse, null);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressPdfParse = (ProgressBar)view.findViewById(R.id.progress_pdfParse);
        mTxtPdfContent = (TextView)view.findViewById(R.id.pdf_content);

        openPdfFile();
        List<Article> articles = DataSupport.where("title = ?","pdfTest").find(Article.class);
        if (articles.size() == 0) {
            //测试用时
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Util.d(TAG + "开始解析文档", "开始计时");
                    long start = System.currentTimeMillis();
                    final String message = PdfParse.readFdf(getResources().openRawResource(R.raw.english));
                    long end = System.currentTimeMillis();
                    Util.d(TAG + "结束文档解析", "耗时" + (end - start) / 1000 + "秒");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTxtPdfContent.setText(message);
                        }
                    });
                    //网络标记词性必须在子线程不能在主线程
                    String messageTag = Util.post(message);
                    Message msg = handler.obtainMessage();
                    msg.obj = messageTag;
                    handler.sendMessage(msg);
                }
            }).start();
        }else {
            Util.showToast(getActivity(),R.string.pdf_parse_already);
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
            if(!TextUtils.isEmpty(result)) {
                pdfTest.setBody(result);
                pdfTest.save();
                if (pdfTest.isSaved()) {
                    Util.d(TAG + "pdf文章", "存储成功！");
                    Util.showToast(getActivity(), "文章存储成功！");
                }
            }else{
                Util.showToast(getActivity(),R.string.press_back_again);
            }
            int num = DataSupport.count(Article.class);
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
            Util.showToast(getActivity(),R.string.file_manager_not_exist);
        }

    }
}
