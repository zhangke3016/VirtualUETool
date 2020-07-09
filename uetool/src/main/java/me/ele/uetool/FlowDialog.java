package me.ele.uetool;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cmprocess.ipc.client.core.VirtualCore;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.ele.uetool.attrdialog.AttrsDialogMultiTypePool;
import me.ele.uetool.base.Element;
import me.ele.uetool.base.ItemViewBinder;
import me.ele.uetool.base.MMKVUtil;
import me.ele.uetool.base.item.BriefDescItem;
import me.ele.uetool.base.item.Item;

import static me.ele.uetool.base.DimenUtil.dip2px;
import static me.ele.uetool.base.DimenUtil.getScreenHeight;
import static me.ele.uetool.base.DimenUtil.getScreenWidth;

public class FlowDialog extends Dialog {

    private Adapter adapter = new Adapter();
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    private Switch switchBtn;
    private TextView stepTextView;

    public FlowDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setBackgroundColor(Color.WHITE);

        {
            LinearLayout topLayout = new LinearLayout(getContext());
            topLayout.setOrientation(LinearLayout.HORIZONTAL);
            stepTextView = new TextView(getContext());
            stepTextView.setTextColor(0xffff0000);
            stepTextView.setTextSize(16);
            stepTextView.setSingleLine();

            switchBtn = new Switch(getContext());
            topLayout.addView(stepTextView);
            topLayout.addView(switchBtn);
            linearLayout.addView(topLayout);
        }


        RecyclerView recyclerView = new RecyclerView(getContext());
        linearLayout.addView(recyclerView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(linearLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void show(Element element) {
        show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = element.getRect().left;
        lp.y = element.getRect().bottom;
        lp.width = getScreenWidth() - dip2px(30);
        lp.height = getScreenHeight() / 2;
        dialogWindow.setAttributes(lp);
        adapter.notifyDataSetChanged(element);
        layoutManager.scrollToPosition(0);

        resetStepText();
        switchBtn.setOnCheckedChangeListener(null);
        switchBtn.setChecked(false);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("setOnChecked", "" + isChecked);
                Item item = adapter.getItem(0);
                AttrsDialogMultiTypePool pool = UETool.getInstance().getAttrsDialogMultiTypePool();
                Class aClass = pool.getItemClass(item);
                Log.e("setOnChecked", "" + aClass.getSimpleName());
                if (isChecked) {

                } else {

                }
                resetStepText();
            }
        });
    }

    private void resetStepText() {
        Set set = MMKVUtil.getInstance().get("flowList", Set.class);
        int size = 0;
        if (set != null) {
            size = set.size();
        }
        stepTextView.setText("第" + size + "步");
    }

    public void notifyValidViewItemInserted(int positionStart, List<Element> validElements, Element targetElement) {
        List<Item> validItems = new ArrayList<>();
        for (int i = 0, N = validElements.size(); i < N; i++) {
            Element element = validElements.get(i);
            validItems.add(new BriefDescItem(element, targetElement.equals(element)));
        }
        adapter.notifyValidViewItemInserted(positionStart, validItems);
    }

    public final void notifyItemRangeRemoved(int positionStart) {
        adapter.notifyValidViewItemRemoved(positionStart);
    }

    public void setAttrDialogCallback(AttrDialogCallback callback) {
        adapter.setAttrDialogCallback(callback);
    }

}

