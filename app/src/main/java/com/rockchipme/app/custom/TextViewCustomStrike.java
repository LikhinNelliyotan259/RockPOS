package com.rockchipme.app.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.rockchipme.app.R;

/**
 * Created by sibin on 16/6/17.
 */

public class TextViewCustomStrike extends AppCompatTextView {
    private static final String TAG = "TextViewCustom";

    public TextViewCustomStrike(Context context) {
        super(context);
    }

    public TextViewCustomStrike(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewCustomStrike(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewCustom);
        String customFont = a.getString(R.styleable.TextViewCustom_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
//            Log.e(TAG, "Unable to load typeface: "+e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#da3b38"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrikeThruText(true);
        paint.setStrokeWidth(4);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        super.onDraw(canvas);
        float width = getWidth();
        float heigh = getHeight();
        canvas.drawLine(width/10, heigh/10, (width-width/10),(heigh-heigh/10), paint);

    }
}