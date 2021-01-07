package com.rockchipme.app.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockchipme.app.R;

/**
 * Created by Alisons on 4/28/2018.
 */

public class QuantityPicker extends LinearLayout {


    // default values
    private final int DEFAULT_MIN = 0;
    private final int DEFAULT_MAX = 99999;
    private final int DEFAULT_VALUE = 1;
    private final int DEFAULT_UNIT = 1;
    private final int DEFAULT_LAYOUT = R.layout.quantity_picker_new;
    private final boolean DEFAULT_FOCUSABLE = false;

    private Handler handler;
    private Runnable runnable;

    // required variables
    private int minValue;
    private int maxValue;
    private int unit;
    private int currentValue;
    private int layout;
    private boolean focusable;

    // ui components
    private Context mContext;
    private FrameLayout flDecrement;
    private FrameLayout flIncrement;
    private LinearLayout llDecrementAndValue;
    private TextView tvDisplay, tvAdd;

    // listeners
    private ValueChangedListener valueChangedListener;


    public QuantityPicker(Context context) {
        super(context);
    }

    public QuantityPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public QuantityPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void initialize(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.QuantityPicker, 0, 0);

        // set required variables with values of xml layout attributes or default ones
        this.minValue = attributes.getInteger(R.styleable.QuantityPicker_minQuantity, this.DEFAULT_MIN);
        this.maxValue = attributes.getInteger(R.styleable.QuantityPicker_maxQuantity, this.DEFAULT_MAX);
        this.currentValue = attributes.getInteger(R.styleable.QuantityPicker_valueQuantity, this.DEFAULT_VALUE);
        this.unit = attributes.getInteger(R.styleable.QuantityPicker_unitQuantity, this.DEFAULT_UNIT);
        this.layout = attributes.getResourceId(R.styleable.QuantityPicker_custom_layout, this.DEFAULT_LAYOUT);
        this.focusable = attributes.getBoolean(R.styleable.QuantityPicker_focusable, this.DEFAULT_FOCUSABLE);
        this.mContext = context;

        // if current value is greater than the max. value, decrement it to the max. value
        this.currentValue = this.currentValue > this.maxValue ? maxValue : currentValue;

        // if current value is less than the min. value, decrement it to the min. value
        this.currentValue = this.currentValue < this.minValue ? minValue : currentValue;

        // set layout view
        LayoutInflater.from(this.mContext).inflate(layout, this, true);

        // init ui components
        this.flDecrement = (FrameLayout) findViewById(R.id.flDecrement);
        this.flIncrement = (FrameLayout) findViewById(R.id.flIncrement);
        this.tvDisplay = (TextView) findViewById(R.id.tvDisplay);
        this.tvAdd = (TextView) findViewById(R.id.tvAdd);
        this.llDecrementAndValue = findViewById(R.id.llDecrementAndValue);

        // register button click and action listeners
        this.flIncrement.setOnClickListener(new ActionListener(this, this.tvDisplay, ActionEnum.INCREMENT));
        if (tvAdd != null) {
            tvAdd.setOnClickListener(new ActionListener(this, this.tvDisplay, ActionEnum.INCREMENT));
        }
        this.flDecrement.setOnClickListener(new ActionListener(this, this.tvDisplay, ActionEnum.DECREMENT));

//        // init listener for exceeding upper and lower limits
//        this.setLimitExceededListener(new DefaultLimitExceededListener());
        // init listener for increment&decrement
        this.setValueChangedListener(new DefaultValueChangedListener());
//        // init listener for focus change
//        this.setOnFocusChangeListener(new DefaultOnFocusChangeListener(this));
//        // init listener for done action in keyboard
//        this.setOnEditorActionListener(new DefaultOnEditorActionListener(this));

        // set default display mode
        this.setDisplayFocusable(this.focusable);

        // update ui view
        this.refresh();
    }

    public void refresh() {
        if (isWantToWaitChangeValue) {
            if (this.currentValue < 1) {
                llDecrementAndValue.setVisibility(GONE);
                tvAdd.setVisibility(VISIBLE);
            } else {
                tvAdd.setVisibility(GONE);
                llDecrementAndValue.setVisibility(VISIBLE);
            }
            this.tvDisplay.setText(this.currentValue + "");
        } else {
            llDecrementAndValue.setVisibility(GONE);
            tvAdd.setVisibility(VISIBLE);
        }

    }

    public void clearFocus() {
        this.tvDisplay.clearFocus();
    }

    public boolean valueIsAllowed(int value) {
        return (value >= this.minValue && value <= this.maxValue);
    }

    public void setMin(int value) {
        this.minValue = value;
    }

    public void setMax(int value) {
        this.maxValue = value;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getUnit() {
        return this.unit;
    }

    public int getMin() {
        return this.minValue;
    }

    public int getMax() {
        return this.maxValue;
    }

    public void setValue(int value) {
        if (!this.valueIsAllowed(value)) {
//            this.limitExceededListener.limitExceeded(value < this.minValue ? this.minValue : this.maxValue, value);
            return;
        }

        this.currentValue = value;
        this.refresh();
    }

    public int getValue() {
        return this.currentValue;
    }


    public void setValueChangedListener(ValueChangedListener valueChangedListener) {
        this.valueChangedListener = valueChangedListener;
    }

    public ValueChangedListener getValueChangedListener() {
        return this.valueChangedListener;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        this.tvDisplay.setOnEditorActionListener(onEditorActionListener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.tvDisplay.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void setActionEnabled(ActionEnum action, boolean enabled) {
        if (action == ActionEnum.INCREMENT) {
            this.flIncrement.setEnabled(enabled);
        } else if (action == ActionEnum.DECREMENT) {
            this.flDecrement.setEnabled(enabled);
        }
    }

    public void setDisplayFocusable(boolean focusable) {
        this.tvDisplay.setFocusable(focusable);

        // required for making EditText focusable
        if (focusable) {
            this.tvDisplay.setFocusableInTouchMode(true);
        }
    }

    public void increment() {
        this.changeValueBy(this.unit);
    }

    public void increment(int unit) {
        this.changeValueBy(unit);
    }

    public void decrement() {
        this.changeValueBy(-this.unit);
    }

    public void decrement(int unit) {
        this.changeValueBy(-unit);
    }

    private void changeValueBy(final int unit) {
        int oldValue = this.getValue();

        this.setValue(this.currentValue + unit);

        if (oldValue != this.getValue()) {

            if (handler == null) {
                handler = new Handler();
            }
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }

            if (isWantToWaitChangeValue) {
                runnable = this::valueChanged;
                handler.postDelayed(runnable, 500);
            } else {
                valueChanged();
            }
        }
    }

    public boolean isWantToWaitChangeValue = true;

    private void valueChanged(){
        QuantityPicker.this.valueChangedListener.valueChanged(QuantityPicker.this.getValue(), unit > 0 ? ActionEnum.INCREMENT : ActionEnum.DECREMENT);
    }

    public void disableClickEvent() {
        if (flDecrement != null) {
            flDecrement.setClickable(false);
        }
        if (flIncrement != null) {
            flIncrement.setClickable(false);
        }
    }


    class ActionListener implements OnClickListener, OnLongClickListener, OnTouchListener {

        QuantityPicker layout;
        ActionEnum action;
        TextView display;

        public ActionListener(QuantityPicker layout, TextView display, ActionEnum action) {
            this.layout = layout;
            this.action = action;
            this.display = display;
        }

        @Override
        public void onClick(View v) {
            try {
                int newValue = Integer.parseInt(this.display.getText().toString());

                if (!this.layout.valueIsAllowed(newValue)) {
                    return;
                }

                this.layout.setValue(newValue);
            } catch (NumberFormatException e) {
                this.layout.refresh();
            }

            switch (this.action) {
                case INCREMENT:
                    this.layout.increment();
                    break;
                case DECREMENT:
                    this.layout.decrement();
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }


    public class DefaultValueChangedListener implements ValueChangedListener {
        public void valueChanged(int value, ActionEnum action) {
            String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ? "incremented" : "decremented");
            String message = String.format("NumberPicker is %s to %d", actionText, value);
            Log.v(this.getClass().getSimpleName(), message);
        }
    }

    public interface ValueChangedListener {
        void valueChanged(int value, ActionEnum action);
    }

    public enum ActionEnum {
        INCREMENT, DECREMENT, MANUAL
    }

}
