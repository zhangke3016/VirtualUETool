package me.ele.uetool;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ele.uetool.base.DimenUtil;

import static android.view.Gravity.BOTTOM;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static me.ele.uetool.MeasureToolHelper.Type.TYPE_EDIT_ATTR;
import static me.ele.uetool.MeasureToolHelper.Type.TYPE_RELATIVE_POSITION;
import static me.ele.uetool.MeasureToolHelper.Type.TYPE_SHOW_GRIDDING;
import static me.ele.uetool.MeasureToolHelper.Type.TYPE_UNKNOWN;

public class MeasureToolHelper {

    public static final String EXTRA_TYPE = "extra_type";

    public static void onCreate(Activity activity,Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(activity);
        ViewGroup vContainer = frameLayout;
        final BoardTextView board = new BoardTextView(activity);
        int type = savedInstanceState.getInt(EXTRA_TYPE, TYPE_UNKNOWN);

        switch (type) {
            case TYPE_EDIT_ATTR:
                EditAttrLayout editAttrLayout = new EditAttrLayout(activity);
                editAttrLayout.setOnDragListener(new EditAttrLayout.OnDragListener() {
                    @Override
                    public void showOffset(String offsetContent) {
                        board.updateInfo(offsetContent);
                    }
                });
                vContainer.addView(editAttrLayout);
                break;
            case TYPE_RELATIVE_POSITION:
                vContainer.addView(new RelativePositionLayout(activity));
                break;
            case TYPE_SHOW_GRIDDING:
                vContainer.addView(new GriddingLayout(activity));
                board.updateInfo("LINE_INTERVAL: " + DimenUtil.px2dip(GriddingLayout.LINE_INTERVAL, true));
                break;
            default:
                Toast.makeText(activity, "fail ---", Toast.LENGTH_SHORT).show();
                break;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = BOTTOM;
        vContainer.addView(board, params);

        View view = Util.getCurrentView(activity);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup){
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup != null){
            View viewWithTag = viewGroup.findViewWithTag(EXTRA_TYPE);
            if (viewWithTag != null){
                viewGroup.removeView(viewWithTag);
            }
            vContainer.setTag(EXTRA_TYPE);
            vContainer.setFocusable(false);
            vContainer.setFocusableInTouchMode(false);
            viewGroup.addView(vContainer,new ViewGroup.LayoutParams(viewGroup.getWidth(),viewGroup.getHeight()));
        }
    }

    public static boolean onDestroy(Activity activity) {
        View view = Util.getCurrentView(activity);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup){
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup != null){
            View viewWithTag = viewGroup.findViewWithTag(EXTRA_TYPE);
            if (viewWithTag != null){
                viewGroup.removeView(viewWithTag);
                return true;
            }
        }
        return false;
//        UETool.getInstance().release();
    }

    @IntDef({
            TYPE_UNKNOWN,
            TYPE_EDIT_ATTR,
            TYPE_SHOW_GRIDDING,
            TYPE_RELATIVE_POSITION,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int TYPE_UNKNOWN = -1;
        int TYPE_EDIT_ATTR = 1;
        int TYPE_SHOW_GRIDDING = 2;
        int TYPE_RELATIVE_POSITION = 3;
    }
}
