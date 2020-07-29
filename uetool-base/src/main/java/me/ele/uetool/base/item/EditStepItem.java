package me.ele.uetool.base.item;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ele.uetool.base.db.ElementBean;

import static me.ele.uetool.base.item.EditStepItem.Type.TYPE_DELAY;
import static me.ele.uetool.base.item.EditStepItem.Type.TYPE_LOOP_CLICK;
import static me.ele.uetool.base.item.EditStepItem.Type.TYPE_SCROLL_VIEW;

/**
 * @author zijian.cheng
 * @date 2020/7/27
 */
public class EditStepItem extends TitleItem {

    private @Type
    int type;
    private ElementBean elementBean;

    public EditStepItem(String name, ElementBean elementBean, @Type int type) {
        super(name);
        this.elementBean = elementBean;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public ElementBean getElementBean() {
        return elementBean;
    }

    public void setElementBean(ElementBean elementBean) {
        this.elementBean = elementBean;
    }

    @IntDef({
            TYPE_DELAY,
            TYPE_SCROLL_VIEW,
            TYPE_LOOP_CLICK,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int TYPE_DELAY = 1;
        int TYPE_SCROLL_VIEW = 2;
        int TYPE_LOOP_CLICK = 3;
    }
}
