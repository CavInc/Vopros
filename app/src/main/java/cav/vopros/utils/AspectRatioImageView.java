package cav.vopros.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import cav.vopros.R;


public class AspectRatioImageView extends ImageView {
    private static final float DEFAULT_ASPECT_RATIO = 1.73f;
    private final float mAspectRaio;

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        mAspectRaio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, DEFAULT_ASPECT_RATIO);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth;
        int newHeigth;

        newWidth = getMeasuredWidth();
        newHeigth = (int) (newWidth/mAspectRaio);

        setMeasuredDimension(newWidth,newHeigth);
    }
}
