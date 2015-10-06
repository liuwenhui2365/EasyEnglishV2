package com.liu.easyenglishupdate.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
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
    private String clickStr;
    private static StringLink mStringLink;

    public MyURLSpan(Context ctx,String clickStr){
        this.ctx=ctx;
        this.clickStr=clickStr;
    }

    /**
     * 选中某个单词的点击事件
     * @param widget
     */
    @Override
    public void onClick(View widget) {
//        Log.d("点击获取到",clickStr);
        try {
//            Toast.makeText(ctx,"获取到的内容"+clickStr,Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);

//          防止单词首字母大写获取不到类型转换为小写
            final String type = getWordType(clickStr.toLowerCase());
            String flag = null;
            if (type != null) {
                if (type.equalsIgnoreCase("unknow")) {
                    flag = "认识";
                } else {
                    flag = "不认识";
                }
                dialog.setTitle("修改单词类型");
                dialog.setMessage("确定要将" + clickStr+"转为"+flag+"的单词吗？");
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String typeMod = EasyEnglishDB.modifyWordType(clickStr.toLowerCase());
                        if (typeMod.equalsIgnoreCase("know")){
                            typeMod = "认识";
                            Toast.makeText(ctx,"已经成功将该词归类到"+typeMod+"的单词库中！",Toast.LENGTH_SHORT).show();
                        }else if (typeMod.equalsIgnoreCase("unknow")){
                            typeMod = "不认识";
                            Toast.makeText(ctx,"已经成功将该词归类到"+typeMod+"的单词库中！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }else {
                Toast.makeText(ctx,"非常抱歉，该词还没有收录本词库，以后会更新哦！",Toast.LENGTH_SHORT).show();
            }


        } catch (NullPointerException w) {
            w.printStackTrace();
        }
    }

    /**
     * 英文字母在字符串中的位置，将每一个字符的位置存储到list
     */
    public static List<Integer> getENPositionList(String str){
        List<Integer> list=new ArrayList<Integer>();
//        Log.d("报告标记后的文章大小",str.length()+"");
//        如果长度太大截取即可，防止长时间没有响应
        if (str.length() > 4000){
            str = str.substring(0,4000);
        }
        for(int i=0;i<str.length();i++){
            char mchar=str.charAt(i);
            //('a' <= mchar && mchar <= 'z')||('A' <= mchar && mchar <='Z')
            if(Pattern.matches("[A-Za-z]", mchar + "")){
                list.add(i);
//              System.out.println(i+"位置为英文字符："+mchar);
            }
        }
        return list;
    }

    /**
     * 处理传过来的字符串
     * @param str 文章内容
     */
    public static void handlerStr(String str){
        List<Integer> enStrList= getENPositionList(str);
        Util.d(MyURLSpan.class.getSimpleName() + "报告", "添加获取单词后的文章大小" + enStrList.size());
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
        }
    }

    public static void setStringLink(StringLink stringLink){
        mStringLink = stringLink;
    }

    public interface StringLink {
        public void setLink(int start,int end,String clickStr);
    }

    /**
     * 获取单词的类型（认识或不认识）
     * @param word
     * @return
     */
    private String getWordType(String word) {
        List<Word> wordlist = DataSupport.where("word = ?", word).find(Word.class);
        //防止查询不到该单词
        if(wordlist.size() > 0) {
            Word wordTemp = wordlist.get(0);
            return wordTemp.getType();
        }else{
            return null;
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ctx.getResources().getColor(R.color.black));
        ds.setUnderlineText(false); //去掉下划线
    }
}
