package com.dup.tdup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class DrawView extends View
{
    static Bitmap outfitImg;
    static String outfitCategory;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private ArrayList<PointF> mDrawPoint = new ArrayList<PointF>();
    private int mWidth = 0;
    private int mHeight = 0;
    private float mRatioX = 0;
    private float mRatioY = 0;
    private int mImgWidth = 0;
    private int mImgHeight = 0;
    private Paint mPaint = new Paint();
    private String TAG = "C-DRAWVIEW: ";
    //private Context context;

    //Constructors
    public DrawView(Context context){super(context);}
    public DrawView(Context context, AttributeSet attrs){super(context,attrs);}
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr)
    {super(context,attrs,defStyleAttr);}

    public void setImgSize(int width, int height)

    {
        mImgWidth = width;
        mImgHeight = height;
        requestLayout();
    }//End setImgSize


    public void setDrawPoint(float[][] point, float ratio)
    {
        mDrawPoint.clear();

        float tempX;
        float tempY;

        for(int index = 0; index < 14; index++)
        {
            tempX = (float) point[0][index]/ratio/mRatioX;
            tempY = (float) point[1][index]/ratio/mRatioY;
            mDrawPoint.add(new PointF(tempX, tempY));
        }
    }//End setDrawPoint



    public void setAspectRatio(int width, int height)
    {
        if(width < 0 || height < 0)
        {throw new IllegalArgumentException("Size can not be negative");}

        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }//End setAspectRatio


    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(mDrawPoint.isEmpty())
        {Log.d(TAG, " mDrawPoint is NULL !!");Toast.makeText(super.getContext(), "인물이 인식되도록 똑바로 서주세요", Toast.LENGTH_SHORT).show();return;}

        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(dipToFloat(2));

        //Coordinates to fit "TOP" outfit
        int top_left = (int) (mDrawPoint.get(2).x-60); //The X coordinate of the left side of the rectangle
        int top_top  = (int) (mDrawPoint.get(1).y-10); //The Y coordinate of the top of the rectangle
        int top_right = (int) (mDrawPoint.get(5).x+60); //The X coordinate of the right side of the rectangle
        int top_bottom = (int) (mDrawPoint.get(8).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_top = new Rect(top_left,top_top,top_right,top_bottom);
        //Coordinates to fit "LONG WEAR" outfit
        int long_left = (int) (mDrawPoint.get(2).x-60); //The X coordinate of the left side of the rectangle
        int long_top  = (int) (mDrawPoint.get(1).y-10); //The Y coordinate of the top of the rectangle
        int long_right = (int) (mDrawPoint.get(5).x+60); //The X coordinate of the right side of the rectangle
        int long_bottom = (int) (mDrawPoint.get(9).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_long = new Rect(long_left,long_top,long_right,long_bottom);
        //Coordinates to fit "TROUSERS" outfit
        int trousers_left = (int) (mDrawPoint.get(8).x-60); //The X coordinate of the left side of the rectangle
        int trousers_top  = (int) (mDrawPoint.get(8).y-10); //The Y coordinate of the top of the rectangle
        int trousers_right = (int) (mDrawPoint.get(11).x+60); //The X coordinate of the right side of the rectangle
        int trousers_bottom = (int) (mDrawPoint.get(10).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_trousers = new Rect(trousers_left,trousers_top,trousers_right,trousers_bottom);
        //Coordinates to fit "SHORTS N SKIRTS" outfit
        int short_left = (int) (mDrawPoint.get(8).x-60); //The X coordinate of the left side of the rectangle
        int short_top  = (int) (mDrawPoint.get(8).y-10); //The Y coordinate of the top of the rectangle
        int short_right = (int) (mDrawPoint.get(11).x+60); //The X coordinate of the right side of the rectangle
        int short_bottom = (int) (mDrawPoint.get(9).y+10); //The Y coordinate of the bottom of the rectangle
        Rect rect_short = new Rect(short_left,short_top,short_right,short_bottom);

        // 추가
        //Coordinates to fit "Hats" outfit
        int hats_left = (int) (mDrawPoint.get(0).x-60); //The X coordinate of the left side of the rectangle
        int hats_top  = (int) (mDrawPoint.get(0).y-90); //The Y coordinate of the top of the rectangle
        int hats_right = (int) (mDrawPoint.get(1).x+60); //The X coordinate of the right side of the rectangle
        int hats_bottom = (int) (mDrawPoint.get(1).y-85); //The Y coordinate of the bottom of the rectangle
        Rect rect_hats = new Rect(hats_left,hats_top,hats_right,hats_bottom);

        //Coordinates to fit "Scarves" outfit
        int scarves_left = (int) (mDrawPoint.get(2).x-30); //The X coordinate of the left side of the rectangle
        int scarves_top  = (int) (mDrawPoint.get(1).y-10); //The Y coordinate of the top of the rectangle
        int scarves_right = (int) (mDrawPoint.get(5).x+30); //The X coordinate of the right side of the rectangle
        int scarves_bottom = (int) (mDrawPoint.get(8).y); //The Y coordinate of the bottom of the rectangle
        Rect rect_scarves = new Rect(scarves_left,scarves_top,scarves_right,scarves_bottom);

        //Coordinates to fit "Shoes" outfit
        int shoes_left = (int) (mDrawPoint.get(5).x-30); //The X coordinate of the left side of the rectangle
        int shoes_top  = (int) (mDrawPoint.get(10).y+10); //The Y coordinate of the top of the rectangle
        int shoes_right = (int) (mDrawPoint.get(5).x+30); //The X coordinate of the right side of the rectangle
        int shoes_bottom = (int) (mDrawPoint.get(10).y+100); //The Y coordinate of the bottom of the rectangle
        Rect rect_shoes = new Rect(shoes_left,shoes_top,shoes_right,shoes_bottom);


        Rect dst_rect = rect_top;
        if(outfitCategory.equals("top")){dst_rect = rect_top;}
        if(outfitCategory.equals("long_wears")){dst_rect = rect_long;}
        if(outfitCategory.equals("trousers")){dst_rect = rect_trousers;}
        if(outfitCategory.equals("shorts_n_skirts")){dst_rect = rect_short;}

        // 추가
        if(outfitCategory.equals("hats")){dst_rect = rect_hats;}
        if(outfitCategory.equals("scarves")){dst_rect = rect_scarves;}
        if(outfitCategory.equals("shoes")){dst_rect = rect_shoes;}

        canvas.drawBitmap(outfitImg, null, dst_rect, null);

        Log.d(TAG, " points has been drawed");
    }//End onDraw


    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(mRatioWidth == 0 || mRatioHeight == 0){setMeasuredDimension(width, height);}
        else
        {
            if (width < height * mRatioWidth / mRatioHeight)
            {
                mWidth = width;
                mHeight = width * mRatioHeight / mRatioWidth;
            }else
            {
                mWidth = height * mRatioWidth / mRatioHeight;
                mHeight = height;
            }
        }

        setMeasuredDimension(mWidth, mHeight);

        try {
            mRatioX = (float)mImgWidth / mWidth;
            mRatioY = (float)mImgHeight / mHeight;
        }
        catch(ArithmeticException e)
        {
            Log.d(TAG, " mRatioX|mRatioY Arithmetic Exception !!");
            mRatioX =1;
            mRatioY=1;
        }
    }//End onMeasure

    //Convert a dip value into a float
    private float dipToFloat(float val)
    {
        float dip_val =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
                getContext().getResources().getDisplayMetrics());
        return dip_val;
    }//end dipToFloat

}//End class