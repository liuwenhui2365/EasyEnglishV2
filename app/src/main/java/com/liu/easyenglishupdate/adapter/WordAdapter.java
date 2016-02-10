package com.liu.easyenglishupdate.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.db.EasyEnglishDB;
import com.liu.easyenglishupdate.entity.Word;
import com.liu.easyenglishupdate.util.Util;

import org.apache.fontbox.encoding.Encoding;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/3/19.
 */
public class WordAdapter extends BaseAdapter implements SectionIndexer {

    private LayoutInflater inflater;
    private Activity context;
    private String[] from = null;
    private int [] to =null;
    ArrayList<Word> mWordsList = null;
    private long start;
    private long end;

    public WordAdapter(Activity context, ArrayList<Word> wordsList) {
        this.context = context;
        this.mWordsList = wordsList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setAdapterData(ArrayList<Word> wordsList){
        this.mWordsList = wordsList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mWordsList.size();
    }

    @Override
    public Word getItem(int position)
    {
        return mWordsList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.wordlistviewitem, null);
            viewHolder = new ViewHolder();
            viewHolder.mTxtWord = (TextView) convertView
                    .findViewById(R.id.word);
            viewHolder.mTxtWordInitial = (TextView) convertView
                    .findViewById(R.id.word_initial);
            viewHolder.button = (Button) convertView
                    .findViewById(R.id.TogButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Word word = getItem(position);
        final String englishword = word.getWord();
        viewHolder.mTxtWord.setText(englishword);
        viewHolder.button.setText(word.getType());
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String type = EasyEnglishDB.modifyWordType(englishword);
                viewHolder.button.setText(type);
                Util.showToast(context, "单词类型修改成功");
            }
        });

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.mTxtWordInitial.setVisibility(View.VISIBLE);
            //显示单词的首字母
            String substring = mWordsList.get(position).getWord().substring(0, 1).toUpperCase();
            viewHolder.mTxtWordInitial.setText(substring);
        }else{
            viewHolder.mTxtWordInitial.setVisibility(View.GONE);
        }

        return convertView;

    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     * 注意转为大写
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mWordsList.get(i).getWord().substring(0, 1);
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     * 注意统一转为大写
     */
    @Override
    public int getSectionForPosition(int position) {
        return mWordsList.get(position).getWord().substring(0, 1).toUpperCase().charAt(0);
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    public class ViewHolder {
        //字母导航
        TextView mTxtWordInitial;
        TextView mTxtWord;
        Button button;
    }

}
