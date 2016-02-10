package com.liu.easyenglishupdate.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liu.easyreadenglishupdate.R;
import com.liu.easyenglishupdate.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChangeInfoActivity extends ActionBarActivity implements View.OnClickListener {
    /**
     * 标题
     */
    private TextView mTxtTitle;
    /**
     * 照相机
     */
    private Button mBtnCamera;
    /**
     * 相册
     */
    private Button mBtnPhoto;
    /**
     * 修改昵称
     */
    private Button mBtnChangeName;
    /**
     * 新昵称
     */
    private EditText mEdtName;
    /**
     * 新昵称
     */
    private LinearLayout mLytChangeName;

    private Uri mImageUri;
    private static final int TAKE_PHOTO = 1;
    private static final int CROP_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_info);
        mTxtTitle = (TextView) findViewById(R.id.tv_change_title);
        String title = getIntent().getStringExtra(Util.SHIFT_FLAG);
        mTxtTitle.setText(title);

        mBtnCamera = (Button) findViewById(R.id.btn_camera);
        mBtnPhoto = (Button) findViewById(R.id.btn_photo);
        mEdtName = (EditText) findViewById(R.id.edt_change_name);
        mBtnChangeName = (Button) findViewById(R.id.btn_change_name);
        mLytChangeName = (LinearLayout)findViewById(R.id.lyt_change_name);

        mBtnCamera.setOnClickListener(this);
        mBtnPhoto.setOnClickListener(this);
        mBtnChangeName.setOnClickListener(this);

        if(getString(R.string.change_icon).equals(title)){
            mBtnCamera.setVisibility(View.VISIBLE);
            mBtnPhoto.setVisibility(View.VISIBLE);
            mLytChangeName.setVisibility(View.GONE);
        }else{
            mBtnPhoto.setVisibility(View.GONE);
            mBtnCamera.setVisibility(View.GONE);
            mLytChangeName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera: //照相机
                File outImage = new File(getCacheDir(), "camera.jpg");
                try {
                    if (outImage.exists()) {
                        outImage.delete();
                    }
                    outImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mImageUri = Uri.fromFile(outImage);
                Intent toCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                toCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(toCamera, TAKE_PHOTO);
                break;
            case R.id.btn_photo: //相册
                outImage = new File(getCacheDir(), "camera.jpg");
                try {
                    if (outImage.exists()) {
                        outImage.delete();
                    }
                    outImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mImageUri = Uri.fromFile(outImage);
                toCamera = new Intent("android.intent.action.GET_CONTENT");
                toCamera.setType("image/**");
                toCamera.putExtra("crop", true);
                toCamera.putExtra("scale", true);
                toCamera.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
                startActivityForResult(toCamera, CROP_PHOTO);
                break;
            case R.id.btn_change_name: //修改昵称
                String nickName = mEdtName.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName)) {
                    SharedPreferences preferences = getSharedPreferences(Util.USER_INFO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Util.USER_NICK, nickName);
                } else {
                    Util.showToast(this, R.string.nick_remind);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent cropPhoto = new Intent("com.android.camera.action,CROP");
                    cropPhoto.setDataAndType(mImageUri, "image/**");
//                    cropPhoto.putExtra("scale", true);
                    cropPhoto.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(cropPhoto, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                        //显示图片

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            default:
                break;
        }
    }
}
