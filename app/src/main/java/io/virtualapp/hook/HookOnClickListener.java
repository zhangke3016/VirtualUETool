package io.virtualapp.hook;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HookOnClickListener implements OnClickListener {

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

        Log.w("HookOnClickListener", "onclick view name: " + v.getClass().getName());
    }
}
