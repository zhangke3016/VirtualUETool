package me.ele.uetool;

import me.ele.uetool.base.Element;

public interface AttrDialogCallback {
    void enableMove();

    void showValidViews(int position, boolean isChecked);

    void selectView(Element element);
}