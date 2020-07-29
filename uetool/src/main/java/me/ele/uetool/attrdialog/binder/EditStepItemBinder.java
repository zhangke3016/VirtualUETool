package me.ele.uetool.attrdialog.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.ele.uetool.Adapter;
import me.ele.uetool.attrdialog.AttrsDialogItemViewBinder;
import me.ele.uetool.base.item.EditStepItem;
import me.ele.uetool.base.item.TextItem;

/**
 * @author: weishenhong <a href="mailto:weishenhong@bytedance.com">contact me.</a>
 * @date: 2019-07-08 23:46
 */
public class EditStepItemBinder extends AttrsDialogItemViewBinder<EditStepItem, Adapter.EditStepViewHolder<EditStepItem>> {

    @NonNull
    @Override
    public Adapter.EditStepViewHolder<EditStepItem> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, RecyclerView.Adapter adapter) {
        return Adapter.EditStepViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.EditStepViewHolder<EditStepItem> holder, @NonNull EditStepItem item) {
        holder.bindView(item);
    }
}
