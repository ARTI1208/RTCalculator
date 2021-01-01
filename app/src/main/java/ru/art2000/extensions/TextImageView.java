package ru.art2000.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import ru.art2000.calculator.R;
import ru.art2000.helpers.AndroidHelper;

public class TextImageView extends LinearLayoutCompat {

    private AppCompatImageView imageView;
    private TextView textView;

    public TextImageView(Context context) {
        super(context);
        init();
    }

    public TextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        Context context = getContext();
        int padding = AndroidHelper.dip2px(context, 10);
        setPadding(padding, padding, padding, padding);
        setBackground(AndroidHelper.getDrawableAttribute(context, R.attr.selectableItemBackground));
        imageView = new AppCompatImageView(context);
        textView = new TextView(context);
        int imageSize = AndroidHelper.dip2px(context, 60);
        LinearLayoutCompat.LayoutParams imageLayoutParams =
                new LayoutParams(imageSize, imageSize);
        imageLayoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageLayoutParams);
        LinearLayoutCompat.LayoutParams textLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLayoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textLayoutParams);
        addView(imageView);
        addView(textView);
    }

    public AppCompatImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }
}
