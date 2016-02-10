package com.liu.easyenglishupdate.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.db.EasyEnglishDB;
import com.liu.easyenglishupdate.entity.Word;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用来识别字符串中每一个单词及设置相应的点击事件
 */
public class MyURLSpan extends ClickableSpan {
    private Context ctx;
    /**
     * 选中的字符串
     */
    private String clickStr;
    private static StringLink mStringLink;

    public MyURLSpan(Context ctx, String clickStr) {
        this.ctx = ctx;
        this.clickStr = clickStr;
    }

    /**
     * 选中某个单词的点击事件
     *
     * @param widget 显示字符串的控件
     */
    @Override
    public void onClick(View widget) {
        try {
//            Toast.makeText(ctx,"获取到的内容"+clickStr,Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);

//          防止单词首字母大写获取不到类型转换为小写
            final String type = getWordType(clickStr.toLowerCase());
            //表示单词的类型相反的值，用于提醒用户将要转为的类型
            String flag = null;
            if (type != null) {
                if (type.equalsIgnoreCase("unknow")) {
                    flag = ctx.getString(R.string.know);
                } else {
                    flag = ctx.getString(R.string.unknow);
                }
                dialog.setTitle(ctx.getString(R.string.modify_word_type_title));
                dialog.setMessage(ctx.getString(R.string.modify_word_type_remind_pre)
                        + clickStr + ctx.getString(R.string.modify_word_type_remind_middle) + flag
                        + ctx.getString(R.string.modify_word_type_remind_after));
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String typeMod = EasyEnglishDB.modifyWordType(clickStr.toLowerCase());
                        if (typeMod.equalsIgnoreCase("know")) {
                            typeMod = ctx.getString(R.string.know);
                            Toast.makeText(ctx, ctx.getString(R.string.modify_word_type_pre) + typeMod
                                    + ctx.getString(R.string.modify_word_type_after), Toast.LENGTH_SHORT).show();
                        } else if (typeMod.equalsIgnoreCase("unknow")) {
                            typeMod = ctx.getString(R.string.unknow);
                            Toast.makeText(ctx, ctx.getString(R.string.modify_word_type_pre) + typeMod + ctx.getString(R.string.modify_word_type_after), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            } else {
                Toast.makeText(ctx, ctx.getString(R.string.database_no_word), Toast.LENGTH_SHORT).show();
            }


        } catch (NullPointerException w) {
            w.printStackTrace();
        }
    }

    /**
     * 英文字母在字符串中的位置，将每一个字符的位置存储到list
     *
     * @param str 标记字符串的位置
     */
    public static List<Integer> getENPositionList(String str) {
        List<Integer> list = new ArrayList<Integer>();
        Util.d(Util.getClassName() + "报告", "开始获取字符串的位置");
//        如果长度太大截取即可，防止长时间没有响应
        if (str.length() > 10000) {
            str = str.substring(0, 10000);
        }
        for (int i = 0; i < str.length(); i++) {
            char mchar = str.charAt(i);
            if (Pattern.matches("[A-Za-z]", String.valueOf(mchar))) {
                list.add(i);
//                Util.d("获取" + i, "位置的英文字符：" + mchar);
            }
        }
        return list;
    }

    /**
     * 处理传过来的字符设置每个单词的点击事件
     *
     * @param str 文章内容
     */
    public static void handlerStr(String str) {
        List<Integer> enStrList = getENPositionList(str);
        Util.d(MyURLSpan.class.getSimpleName() + "报告", "获取字符位置后的文章大小" + enStrList.size());
        long start = System.currentTimeMillis();
//      如果大小为0则不执行
        if (enStrList.size() > 0) {
            String tempStr = String.valueOf(str.charAt(enStrList.get(0)));
            for (int i = 0; i < enStrList.size() - 1; i++) {
                if (enStrList.get(i + 1) - enStrList.get(i) == 1) {
                    tempStr = tempStr + str.charAt(enStrList.get(i + 1));
                } else {
                    mStringLink.setLink(enStrList.get(i) - tempStr.length() + 1, enStrList.get(i) + 1, tempStr);//因为此时i在循环中已经自加了
                    tempStr = str.charAt(enStrList.get(i + 1)) + "";
                }
            }
            mStringLink.setLink(enStrList.get(enStrList.size() - 1) - tempStr.length() + 1, enStrList.get(enStrList.size() - 1) + 1, tempStr);
            long end = System.currentTimeMillis();
            Util.d(Util.getClassName() + "报告", "处理文章内容链接耗费" + (end - start) / 1000 + "秒");
        }
    }

    public static void setStringLink(StringLink stringLink) {
        mStringLink = stringLink;
    }

    public interface StringLink {
        public void setLink(int start, int end, String clickStr);
    }

    /**
     * 获取单词的类型（认识或不认识）
     *
     * @param word
     * @return
     */
    private String getWordType(String word) {
        List<Word> wordlist = DataSupport.where("word = ?", word).find(Word.class);
        //防止查询不到该单词
        if (wordlist.size() > 0) {
            Word wordTemp = wordlist.get(0);
            return wordTemp.getType();
        } else {
            return null;
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ctx.getResources().getColor(R.color.light_black));
        ds.setUnderlineText(false); //去掉下划线
    }
}
