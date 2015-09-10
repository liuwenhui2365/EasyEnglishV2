package com.example.liu.autotanslate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/4/14.
 * 实现ImageVIew播放动画
 */
public class MyGifView extends ImageView {
    private long movieStart;
    private Movie movie;
    boolean isGifImage;

    //此处必须重写该构造方法
    public MyGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性isgifimage
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyGifView);
        isGifImage = array.getBoolean(R.styleable.MyGifView_isgifimage, true);
        array.recycle();//获取自定义属性完毕后需要recycle，不然会对下次获取造成影响
        //获取ImageView的默认src属性
        int     image = attrs.getAttributeResourceValue( "http://schemas.android.com/apk/res/android", "src", 0);
        movie = Movie.decodeStream(getResources().openRawResource(image));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//执行父类onDraw方法，绘制非gif的资源
        if(isGifImage){//若为gif文件，执行DrawGifImage()，默认执行
            DrawGifImage(canvas);
        }
    }

    private void DrawGifImage(Canvas canvas) {
        //获取系统当前时间
        long nowTime = android.os.SystemClock.currentThreadTimeMillis();
        if(movieStart == 0){
            //若为第一次加载，开始时间置为nowTime
            movieStart = nowTime;
        }
        if(movie != null){//容错处理
            int duration = movie.duration();//获取gif持续时间
            //如果gif持续时间为100，可认为非gif资源，跳出处理
            if(duration == 0){
                //获取gif当前帧的显示所在时间点
                int relTime = (int) ((nowTime - movieStart) % duration);
                movie.setTime(relTime);
                //渲染gif图像
                movie.draw(canvas, 0, 0);
                invalidate();
            }
        }
    }
}
