package com.example.jcman.marqueetextviewdemo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jcman on 16-3-9.
 */
public class MarqueeTextView extends TextView{


    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MarqueeTextView(Context context) {
        this(context,null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        this.setSingleLine(true);
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setMarqueeRepeatLimit(-1);

    }

    @Override
    public boolean isFocused(){
        return true;
    }
}
