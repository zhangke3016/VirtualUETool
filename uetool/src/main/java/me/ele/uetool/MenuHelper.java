package me.ele.uetool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ele.uetool.base.DimenUtil;

import static android.view.Gravity.BOTTOM;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static me.ele.uetool.MenuHelper.Type.TYPE_EDIT_ATTR;
import static me.ele.uetool.MenuHelper.Type.TYPE_LAYOUT_LEVEL;
import static me.ele.uetool.MenuHelper.Type.TYPE_RELATIVE_POSITION;
import static me.ele.uetool.MenuHelper.Type.TYPE_SHOW_EDIT;
import static me.ele.uetool.MenuHelper.Type.TYPE_SHOW_GRIDDING;
import static me.ele.uetool.MenuHelper.Type.TYPE_UNKNOWN;

public class MenuHelper {

    public static final String TAG = "MenuHelper";

    public static final String EXTRA_TYPE = "x_uetool_extra_type";

    private static final String EXTRA_TYPE_LEVEL = "x_uetool_extra_type_level";

    public static void show(Activity activity, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        Log.d(TAG, " show ");
        FrameLayout frameLayout = new FrameLayout(activity);
        ViewGroup vContainer = frameLayout;
        final BoardTextView board = new BoardTextView(activity);
        int type = bundle.getInt(EXTRA_TYPE, TYPE_UNKNOWN);

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
            case TYPE_SHOW_EDIT:
                EditTouchLayout editTouchLayout = new EditTouchLayout(activity);
                vContainer.addView(editTouchLayout);
                break;
            case TYPE_RELATIVE_POSITION:
                vContainer.addView(new RelativePositionLayout(activity));
                break;
            case TYPE_SHOW_GRIDDING:
                vContainer.addView(new GriddingLayout(activity));
                board.updateInfo("LINE_INTERVAL: " + DimenUtil.px2dip(GriddingLayout.LINE_INTERVAL, true));
                break;
            case TYPE_LAYOUT_LEVEL:
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
        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup != null && viewGroup.getChildCount() > 0) {
            if (type == TYPE_LAYOUT_LEVEL) {
                ScalpelFrameLayout scalpelFrameLayout = new ScalpelFrameLayout(activity);
                View v = viewGroup.getChildAt(0);
                viewGroup.removeView(v);

                scalpelFrameLayout.addView(v);
                scalpelFrameLayout.setTag(EXTRA_TYPE_LEVEL);
                vContainer.addView(scalpelFrameLayout);
                scalpelFrameLayout.setLayerInteractionEnabled(true);
                scalpelFrameLayout.setDrawViews(true);
                scalpelFrameLayout.setDrawIds(true);
            }
            View viewWithTag = viewGroup.findViewWithTag(EXTRA_TYPE);
            View viewWithTagLevel = viewGroup.findViewWithTag(EXTRA_TYPE_LEVEL);
            if (viewWithTag != null) {
                viewGroup.removeView(viewWithTag);
            }
            if (viewWithTagLevel != null) {
                viewGroup.removeView(viewWithTagLevel);
            }
            vContainer.setTag(EXTRA_TYPE);
            vContainer.setFocusable(false);
            vContainer.setFocusableInTouchMode(false);
            Log.d(TAG, " addView ");
            viewGroup.addView(vContainer, new ViewGroup.LayoutParams(viewGroup.getWidth(), viewGroup.getHeight()));
            viewGroup.postInvalidate();
        }
    }

    public static boolean dismiss(Activity activity) {
        View view = Util.getCurrentView(activity);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup != null) {
            View viewWithTag = viewGroup.findViewWithTag(EXTRA_TYPE);
            ViewGroup viewWithTagLevel = viewGroup.findViewWithTag(EXTRA_TYPE_LEVEL);
            if (viewWithTagLevel != null) {
                View child = viewWithTagLevel.getChildAt(0);
                if (child != null) {
                    viewWithTagLevel.removeView(child);
                    ViewGroup vg = (ViewGroup) Util.getCurrentView(activity);
                    vg.addView(child, 0);
                }
            }
            if (viewWithTag != null) {
                Log.d(TAG, " removeView ");
                viewGroup.removeView(viewWithTag);
                return true;
            }
        }
        return false;
    }

    @IntDef({
            TYPE_UNKNOWN,
            TYPE_EDIT_ATTR,
            TYPE_SHOW_EDIT,
            TYPE_SHOW_GRIDDING,
            TYPE_RELATIVE_POSITION,
            TYPE_LAYOUT_LEVEL,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int TYPE_UNKNOWN = -1;
        int TYPE_EDIT_ATTR = 1;
        int TYPE_SHOW_EDIT = 2;
        int TYPE_SHOW_GRIDDING = 3;
        int TYPE_RELATIVE_POSITION = 4;
        int TYPE_LAYOUT_LEVEL = 5;
    }
}
