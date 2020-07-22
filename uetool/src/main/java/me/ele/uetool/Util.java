package me.ele.uetool;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.automate.core.model.ElementBean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.ele.uetool.base.Application;
import me.ele.uetool.base.Element;
import me.ele.uetool.base.ReflectionP;
import me.ele.uetool.base.ReflectionP.Func;

import static android.view.View.NO_ID;

public class Util {

    public static final String TAG = "Util";

    public static void enableFullscreen(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            addSystemUiFlag(window, 1280);
        }
    }

    private static void addSystemUiFlag(Window window, int flag) {
        View view = window.getDecorView();
        if (view != null) {
            view.setSystemUiVisibility(view.getSystemUiVisibility() | flag);
        }
    }

    public static void setStatusBarColor(@NonNull Window window, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(color);
        }
    }

    public static String getSwitchText(List<ElementBean> beforeElement, Element element) {
        if (beforeElement != null) {
            int size = beforeElement.size();
            String clickListener = getViewClickListener(element.getView());
            ElementBean bean;
            for (int i = 0; i < size; i++) {
                bean = beforeElement.get(i);
                if (element.getRect().contains(bean.getRect())) {
                    if ((TextUtils.isEmpty(bean.getViewClickClass())
                            && TextUtils.isEmpty(clickListener))
                            || bean.getViewClickClass().equals(clickListener)) {
                        return (i + 1) + "/" + size;
                    }
                }
            }
        }
        return "未设置";
    }

    public static int getSwitchState(List<ElementBean> beforeElement, Element element) {
        int id = element.getView().getId();
        String clickListener = getViewClickListener(element.getView());
        if (id > 0) {
            String resId = getResId(id);
            if (!TextUtils.isEmpty(resId)) {
                if (beforeElement != null) {
                    for (ElementBean bean : beforeElement) {
                        if (bean.getResId().equals(resId) && element.getRect().contains(bean.getRect())) {
                            if ((TextUtils.isEmpty(bean.getViewClickClass())
                                    && TextUtils.isEmpty(clickListener))
                                    || bean.getViewClickClass().equals(clickListener)) {
                                return 1;
                            }
                        }
                    }
                }
            }
            return 0;
        } else {
            if (beforeElement != null) {
                for (ElementBean bean : beforeElement) {
                    if (element.getRect().contains(bean.getRect())) {
                        View view = element.getView();
                        if (view instanceof TextView) {
                            if (((TextView) view).getText().toString().equals(bean.getText())) {
                                return 1;
                            }
                        } else if ((TextUtils.isEmpty(bean.getViewClickClass())
                                && TextUtils.isEmpty(clickListener))
                                || bean.getViewClickClass().equals(clickListener)) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
    }

    public static String getResourceName(int id) {
        Resources resources = Application.getApplicationContext().getResources();
        try {
            if (id == NO_ID || id == 0) {
                return "";
            } else {
                return resources.getResourceEntryName(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getViewTag(final View view) {
        final Object tag = view.getTag();
        return tag == null ? "" : tag.toString();
    }

    public static String getResId(int id) {
        try {
            if (id == NO_ID) {
                return "";
            } else {
                return "0x" + Integer.toHexString(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String intToHexColor(int color) {
        return "#" + Integer.toHexString(color).toUpperCase();
    }

    public static Object getBackground(View view) {
        Drawable drawable = view.getBackground();
        if (drawable instanceof ColorDrawable) {
            return intToHexColor(((ColorDrawable) drawable).getColor());
        } else if (drawable instanceof GradientDrawable) {
            try {
                Field mFillPaintField = GradientDrawable.class.getDeclaredField("mFillPaint");
                mFillPaintField.setAccessible(true);
                Paint mFillPaint = (Paint) mFillPaintField.get(drawable);
                Shader shader = mFillPaint.getShader();
                if (shader instanceof LinearGradient) {
                    Field mColorsField = LinearGradient.class.getDeclaredField("mColors");
                    mColorsField.setAccessible(true);
                    int[] mColors = (int[]) mColorsField.get(shader);
                    StringBuilder sb = new StringBuilder();
                    if (mColors != null) {
                        for (int i = 0, N = mColors.length; i < N; i++) {
                            sb.append(intToHexColor(mColors[i]));
                            if (i < N - 1) {
                                sb.append(" -> ");
                            }
                        }
                    }
                    return sb.toString();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            return getDrawableBitmap(drawable);
        }
        return null;
    }

    public static List<Pair<String, Bitmap>> getTextViewBitmap(TextView textView) {
        List<Pair<String, Bitmap>> bitmaps = new ArrayList<>();
        bitmaps.addAll(getTextViewDrawableBitmap(textView));
        bitmaps.addAll(getTextViewImageSpanBitmap(textView));
        return bitmaps;
    }

    private static List<Pair<String, Bitmap>> getTextViewDrawableBitmap(TextView textView) {
        List<Pair<String, Bitmap>> bitmaps = new ArrayList<>();
        try {
            Drawable[] drawables = textView.getCompoundDrawables();
            bitmaps.add(new Pair<>("DrawableLeft", getDrawableBitmap(drawables[0])));
            bitmaps.add(new Pair<>("DrawableTop", getDrawableBitmap(drawables[1])));
            bitmaps.add(new Pair<>("DrawableRight", getDrawableBitmap(drawables[2])));
            bitmaps.add(new Pair<>("DrawableBottom", getDrawableBitmap(drawables[3])));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmaps;
    }

    private static List<Pair<String, Bitmap>> getTextViewImageSpanBitmap(TextView textView) {
        List<Pair<String, Bitmap>> bitmaps = new ArrayList<>();
        try {
            CharSequence text = textView.getText();
            if (text instanceof SpannedString) {
                Field mSpansField = Class.forName("android.text.SpannableStringInternal").getDeclaredField("mSpans");
                mSpansField.setAccessible(true);
                Object[] spans = (Object[]) mSpansField.get(text);
                for (Object span : spans) {
                    if (span instanceof ImageSpan) {
                        bitmaps.add(new Pair<>("SpanBitmap", getDrawableBitmap(((ImageSpan) span).getDrawable())));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmaps;
    }

    public static Bitmap getImageViewBitmap(ImageView imageView) {
        return getDrawableBitmap(imageView.getDrawable());
    }

    private static Bitmap getDrawableBitmap(Drawable drawable) {
        try {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof NinePatchDrawable) {
                NinePatch ninePatch = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Field mNinePatchStateFiled = NinePatchDrawable.class.getDeclaredField("mNinePatchState");
                    mNinePatchStateFiled.setAccessible(true);
                    Object mNinePatchState = mNinePatchStateFiled.get(drawable);
                    Field mNinePatchFiled = mNinePatchState.getClass().getDeclaredField("mNinePatch");
                    mNinePatchFiled.setAccessible(true);
                    ninePatch = (NinePatch) mNinePatchFiled.get(mNinePatchState);
                    return ninePatch.getBitmap();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Field mNinePatchFiled = NinePatchDrawable.class.getDeclaredField("mNinePatch");
                    mNinePatchFiled.setAccessible(true);
                    ninePatch = (NinePatch) mNinePatchFiled.get(drawable);
                    return ninePatch.getBitmap();
                }
            } else if (drawable instanceof ClipDrawable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return ((BitmapDrawable) ((ClipDrawable) drawable).getDrawable()).getBitmap();
                }
            } else if (drawable instanceof StateListDrawable) {
                return ((BitmapDrawable) drawable.getCurrent()).getBitmap();
            } else if (drawable instanceof VectorDrawableCompat) {
                Field mVectorStateField = VectorDrawableCompat.class.getDeclaredField("mVectorState");
                mVectorStateField.setAccessible(true);
                Field mCachedBitmapField = Class.forName("android.support.graphics.drawable.VectorDrawableCompat$VectorDrawableCompatState").getDeclaredField("mCachedBitmap");
                mCachedBitmapField.setAccessible(true);
                return (Bitmap) mCachedBitmapField.get(mVectorStateField.get(drawable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageViewScaleType(ImageView imageView) {
        return imageView.getScaleType().name();
    }

    public static void clipText(String clipText) {
        Context context = Application.getApplicationContext();
        ClipData clipData = ClipData.newPlainText("", clipText);
        ((ClipboardManager) (context.getSystemService(Context.CLIPBOARD_SERVICE))).setPrimaryClip(clipData);
        Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show();
    }

    public static View getCurrentView(Activity activity) {
        try {
            Activity targetActivity = UETool.getInstance().getTargetActivity();
            WindowManager windowManager = targetActivity.getWindowManager();
            Field mGlobalField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("mGlobal");
            mGlobalField.setAccessible(true);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Field mViewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = (List<View>) mViewsField.get(mGlobalField.get(windowManager));
                for (int i = views.size() - 1; i >= 0; i--) {
                    View targetView = getTargetDecorView(targetActivity, views.get(i));
                    if (targetView != null) {
                        return targetView;
                    }
                }
            } else {
                Field mRootsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mRoots");
                mRootsField.setAccessible(true);
                List viewRootImpls;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    viewRootImpls = (List) mRootsField.get(mGlobalField.get(windowManager));
                } else {
                    viewRootImpls = Arrays.asList((Object[]) mRootsField.get(mGlobalField.get(windowManager)));
                }
                for (int i = viewRootImpls.size() - 1; i >= 0; i--) {
                    Class clazz = Class.forName("android.view.ViewRootImpl");
                    Object object = viewRootImpls.get(i);
                    Field mWindowAttributesField = clazz.getDeclaredField("mWindowAttributes");
                    mWindowAttributesField.setAccessible(true);
                    Field mViewField = clazz.getDeclaredField("mView");
                    mViewField.setAccessible(true);
                    View decorView = (View) mViewField.get(object);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mWindowAttributesField.get(object);
                    if (layoutParams.getTitle().toString().contains(targetActivity.getClass().getName())
                            || getTargetDecorView(targetActivity, decorView) != null) {
                        return decorView;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static View getTargetDecorView(Activity targetActivity, View decorView) {
        View targetView = null;
        Context context = decorView.getContext();
        if (context == targetActivity) {
            targetView = decorView;
        } else {
            while (context instanceof ContextThemeWrapper) {
                Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
                if (baseContext == targetActivity) {
                    targetView = decorView;
                    break;
                }
                context = baseContext;
            }
        }
        return targetView;
    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            Field mActivitiesField = activityThreadClass.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Map activities = (Map) mActivitiesField.get(currentActivityThread);
            for (Object record : activities.values()) {
                Class recordClass = record.getClass();
                Field pausedField = recordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!(boolean) pausedField.get(record)) {
                    Field activityField = recordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //  获取当前 view 所在的最上层 fragment
    @Nullable
    public static Fragment getCurrentFragment(View targetView) {

        Activity activity = UETool.getInstance().getTargetActivity();
        if (activity instanceof FragmentActivity) {
            List<Fragment> fragments = collectVisibleFragment(((FragmentActivity) activity).getSupportFragmentManager());
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (findTargetView(fragment.getView(), targetView)) {
                    return fragment;
                }
            }
        }

        return null;
    }

    //  收集所有可见 fragment
    private static List<Fragment> collectVisibleFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = new ArrayList<>();

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isVisible()) {
                fragments.add(fragment);
                fragments.addAll(collectVisibleFragment(fragment.getChildFragmentManager()));
            }
        }

        return fragments;
    }

    //  获取当前 fragment 类名
    @Nullable
    public static String getCurrentFragmentName(View targetView) {

        Fragment fragment = getCurrentFragment(targetView);

        if (fragment != null) {
            return fragment.getClass().getName();
        }

        return null;
    }

    //  获取当前 view 的 view holder 类名
    public static String getViewHolderName(View targetView) {
        View currentView = targetView;
        while (currentView != null) {
            ViewParent parent = currentView.getParent();
            if (parent instanceof RecyclerView) {
                return ((RecyclerView) parent).getChildViewHolder(currentView).getClass().getName();
            }
            currentView = parent instanceof View ? (View) parent : null;
        }
        return null;
    }

    //  遍历目标 view 是否在指定 view 内
    private static boolean findTargetView(View view, View targetView) {
        if (view == targetView) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (findTargetView(parent.getChildAt(i), targetView)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getViewClickListener(final View view) {
        return ReflectionP.breakAndroidP(new Func<String>() {
            @Override
            public String call() {
                try {
                    final Field mListenerInfoField = View.class.getDeclaredField("mListenerInfo");
                    mListenerInfoField.setAccessible(true);
                    final Field mClickListenerField = Class.forName("android.view.View$ListenerInfo").getDeclaredField("mOnClickListener");
                    mClickListenerField.setAccessible(true);
                    OnClickListener listener = (OnClickListener) mClickListenerField.get(mListenerInfoField.get(view));
                    return listener.getClass().getName();
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }
}