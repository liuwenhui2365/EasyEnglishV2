package com.example.liu.autotanslate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wenhuiliu.EasyEnglishReading.DbArticle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/19.
 */
public class WordAdapter extends SimpleAdapter {

    private LayoutInflater inflater;
    private Context context;
    private int resId = 0;
    private String[] from = null;
    private int [] to =null;
    ArrayList<HashMap<String,String>> contentList = null;

    public WordAdapter(Context context, int resId, ArrayList<HashMap<String, String>> artistList, String[] from, int[] to) {
        super(context,artistList,resId,from,to);
        this.resId = resId;
        this.context = context;
        this.contentList = artistList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return contentList.size();
    }

    @Override
    public HashMap<String,String> getItem(int position)
    {
        return contentList.get(position);
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
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.word);
            viewHolder.button = (Button) convertView
                    .findViewById(R.id.TogButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String word = getItem(position).get("word");
        viewHolder.text.setText(word);
        viewHolder.button.setText(getWordCatalogy(word));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Log.d("Click事件","驱动了");
                String type = ButtonClick(word);
                viewHolder.button.setText(type);
                Toast.makeText(context,"单词类型为"+type,Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;

    }

    public String getWordCatalogy(String word){
        DbArticle dbArticle;
        SQLiteDatabase db = null;
        String type = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(context, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("SELECT type FROM words where word = ?", new String[]{word});
                    while (c.moveToNext()) {
                        type = c.getString(c.getColumnIndex("type"));
//                        Log.d("从数据库中获取type", "type=>" + type);
                    }
                }
            }else{

                Log.w("警告","没有获取到单词类型");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

            return type;
        }
    }

    private String ButtonClick(String word) {
//        Log.d("按钮获取到的单词",word);
        DbArticle dbArticle;
        SQLiteDatabase db = null;
        String type = null;
        Cursor c = null;
        try {
            dbArticle = new DbArticle(context, "Articles.db", null, 1);
            db = dbArticle.getReadableDatabase();
            c = db.rawQuery("select count(*) as c from sqlite_master  where type ='table' and name ='words'", null);
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
//                 如果表存在
                    c = db.rawQuery("SELECT type FROM words where word = ?", new String[]{word});
                    while (c.moveToNext()) {
                        type = c.getString(c.getColumnIndex("type"));
//                        Log.d("从数据库中读取type", "type=>" + type);
                    }
                }

                if (type != null) {
                    if (type.equalsIgnoreCase("unknow")) {
                        type = "know";
                        db.execSQL("UPDATE words SET type = ? where word = ?", new String[]{type, word});
//                       Toast没反应！！！
                    } else {
                        type = "unknow";
                        db.execSQL("UPDATE words SET type = ? where word = ?", new String[]{type, word});
                    }
                } else {
                    Log.e("警告", "没有获取到类型");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

            return type;
        }

    }



    public class ViewHolder {
        TextView text;
        Button button;
    }

}
