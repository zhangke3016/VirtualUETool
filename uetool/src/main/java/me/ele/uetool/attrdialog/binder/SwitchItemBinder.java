package me.ele.uetool.attrdialog.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.ele.uetool.Adapter;
import me.ele.uetool.AttrsDialog;
import me.ele.uetool.attrdialog.AttrsDialogItemViewBinder;
import me.ele.uetool.base.item.SwitchItem;

/**
 * @author: weishenhong <a href="mailto:weishenhong@bytedance.com">contact me.</a>
 * @date: 2019-07-08 23:46
 */
public class SwitchItemBinder extends AttrsDialogItemViewBinder<SwitchItem, Adapter.SwitchViewHolder> {
    @NonNull
    @Override
    public Adapter.SwitchViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, RecyclerView.Adapter adapter) {
        return Adapter.SwitchViewHolder.newInstance(parent, getAttrDialogCallback(adapter));
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.SwitchViewHolder holder, @NonNull SwitchItem item) {
        holder.bindView(item);
    }
}
