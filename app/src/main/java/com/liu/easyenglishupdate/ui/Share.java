package com.liu.easyenglishupdate.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.liu.easyreadenglishupdate.R;

public class Share extends ActionBarActivity {

    private Button button=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        button = (Button) findViewById(R.id.mybutton);
        button.setText("Next");
        button.setOnClickListener(new MyButtonListener());
//      使用按键实现图片文字分享
        Button share=(Button)findViewById(R.id.share);
        share.setText("图片分享");
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_STREAM,"./res/drawable/ab.png");
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));


            }
        });

        share=(Button)findViewById(R.id.sharetext);
        share.setText("文字分享");
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              设置发送动作否则发不出去内容
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"hello, I learn the text sharing");
                String title = (String)getResources().getText(R.string.state);
                Intent chooser = Intent.createChooser(intent, title);
                startActivity(chooser);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            Intent intent = new Intent();
//            intent.setClass(Share.this,ActionBar.Tab.class);
//            Share.this.startActivity(intent);
            finish();
        }
    }
}

