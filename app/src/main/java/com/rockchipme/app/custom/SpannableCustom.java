package com.rockchipme.app.custom;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class SpannableCustom extends ClickableSpan {

    private boolean isUnderline = true;

    public SpannableCustom(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.RED);
        ds.setUnderlineText(isUnderline);

    }

    @Override
    public void onClick(View widget) {

    }
}