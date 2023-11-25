package com.example.kit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ColorSplotch extends View {

    private int colorInt;
    private Paint paintColor;
    private RectF rectF;

    public ColorSplotch(Context context) {
        super(context);
        paintColor = new Paint(colorInt);
        rectF = new RectF(0, 0, getWidth(), getHeight());
    }

    public ColorSplotch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ColorSplotch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ColorSplotch, 0, 0);
        try {
            colorInt = array.getColor(R.styleable.ColorSplotch_splotchColor, 0xff000000);
        } finally {
            array.recycle();
        }

        paintColor = new Paint(colorInt);
        rectF = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rectF, R.dimen.corner_radius, R.dimen.corner_radius, paintColor);
    }
}
