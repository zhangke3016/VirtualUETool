package me.ele.uetool;

import me.ele.uetool.base.Element;
import me.ele.uetool.base.db.ElementBean;

public interface AttrDialogCallback {
    void enableMove();

    void showSelectedViews(int position, boolean isChecked, ElementBean currentElement);

    void showValidViews(int position, boolean isChecked);

    void selectView(Element element);
}