package me.ele.uetool;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lody.virtual.helper.utils.Reflect;

import java.util.ArrayList;
import java.util.List;

import me.ele.uetool.base.Element;
import me.ele.uetool.base.db.ElementBean;
import me.ele.uetool.base.item.BriefDescItem;
import me.ele.uetool.base.item.EditStepItem;
import me.ele.uetool.base.item.Item;

import static me.ele.uetool.base.DimenUtil.dip2px;
import static me.ele.uetool.base.DimenUtil.getScreenHeight;
import static me.ele.uetool.base.DimenUtil.getScreenWidth;

public class AttrsDialog extends Dialog {

    private Adapter adapter = new Adapter();
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

    public AttrsDialog(Context context) {
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

    public void notifySelectedViewItemInserted(int positionStart, Element targetElement, ElementBean currentBean) {
        List<Item> selectedItems = new ArrayList<>();
        selectedItems.add(new EditStepItem("延时触发", currentBean,
                EditStepItem.Type.TYPE_DELAY));
        View view = targetElement.getView();
        boolean canScrollUp = ViewCompat.canScrollVertically(view, -1);
        boolean canScrollDown = ViewCompat.canScrollVertically(view, 1);
        boolean canScrollLeft = ViewCompat.canScrollHorizontally(view, -1);
        boolean canScrollRight = ViewCompat.canScrollHorizontally(view, 1);
        boolean canScrollVer = false;
        boolean canScrollHor = false;
        if (view.getClass().getName().contains("RecyclerView")) {
            int mOrientation = Reflect.on(Reflect.on(view).get("mLayout")).get("mOrientation");
            if (mOrientation == 0) {
                //HORIZONTAL = 0;
                canScrollHor = true;
            } else if (mOrientation == 1) {
                //VERTICAL = 1;
                canScrollVer = true;
            }
//            selectedItems.add(new EditStepItem("循环点击", currentBean,
//                    EditStepItem.Type.TYPE_LOOP_CLICK));
        }
        if (canScrollUp || canScrollDown || canScrollVer
                || canScrollLeft || canScrollRight || canScrollHor) {
            selectedItems.add(new EditStepItem("滑动列表", currentBean,
                    EditStepItem.Type.TYPE_SCROLL_VIEW));
        }
        adapter.notifySelectedItemInserted(positionStart, selectedItems);
    }

    public final void notifySelectedItemRangeRemoved(int positionStart) {
        adapter.notifySelectedItemRemoved(positionStart);
    }

    public void setAttrDialogCallback(AttrDialogCallback callback) {
        adapter.setAttrDialogCallback(callback);
    }
}

