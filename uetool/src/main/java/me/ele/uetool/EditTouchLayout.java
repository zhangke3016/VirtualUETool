package me.ele.uetool;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author zijian.cheng
 * @date 2020/7/13
 */
public class EditTouchLayout extends FrameLayout {

    public EditTouchLayout(@NonNull Context context) {
        this(context, null);
    }

    public EditTouchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTouchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0xff123123);
    }
}
