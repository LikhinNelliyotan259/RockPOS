package com.rockchipme.app.helpers;

/**
 * Created by Android on 2/13/2017.
 */
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class ShadowBuilder extends View.DragShadowBuilder {

    private Point mScaleFactor;
    public ShadowBuilder(View v) {
        super(v);
    }
    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        int width;
        int height;
        width = getView().getWidth();
        height = getView().getHeight();
        size.set(width, height);
        mScaleFactor = size;
        touch.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(mScaleFactor.x/(float)getView().getWidth(), mScaleFactor.y/(float)getView().getHeight());
        getView().draw(canvas);
    }

}