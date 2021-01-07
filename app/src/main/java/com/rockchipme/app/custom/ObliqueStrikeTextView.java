package com.rockchipme.app.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Alisons on 12/19/2016.
 */
public class ObliqueStrikeTextView extends TextView {
    private int dividerColor;
    private Paint paint;

    public ObliqueStrikeTextView(Context context) {
        super(context);
        init(context);
    }

    public ObliqueStrikeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ObliqueStrikeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        //replace with your color
       /* dividerColor = resources.getColor(R.color.black);

        paint = new Paint();
        paint.setColor(dividerColor);
        //replace with your desired width
        paint.setStrokeWidth(resources.getDimension(R.dimen.vertical_divider_width));*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#da3b38"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrikeThruText(true);
        paint.setStrokeWidth(2);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        super.onDraw(canvas);
        float width = getWidth();
        float heigh = getHeight();
        //canvas.drawLine(width/10, heigh/10, (width-width/10),(heigh-heigh/10), paint);
        canvas.drawLine(-5, heigh/2, width+5,heigh/2, paint);
    }
}
