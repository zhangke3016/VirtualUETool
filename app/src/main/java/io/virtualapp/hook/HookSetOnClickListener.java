package io.virtualapp.hook;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HookSetOnClickListener {

    private static final String TAG = "HookSetOnClickListener";

    public static void hook(View view, OnClickListener clickListener) {
        Log.w(TAG, "hook setOnclick name: " + view.getClass().getName());
        backup(view, new HookOnClickListener(clickListener));
    }

    public static void backup(View view, OnClickListener clickListener) {
        Log.w(TAG, "hook setOnclick should not be here");
    }


    static class HookOnClickListener implements OnClickListener {

        OnClickListener clickListener;

        public HookOnClickListener(OnClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v);
            }
            if (v instanceof TextView) {
                ((TextView) v).setText("Hook !!!");
            }

            Log.w(TAG, "onclick view name: " + v.getClass().getName());
        }
    }
}
