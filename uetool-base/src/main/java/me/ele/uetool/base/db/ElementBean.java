package me.ele.uetool.base.db;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import com.lody.virtual.helper.utils.MD5Utils;
import com.lody.virtual.helper.utils.Reflect;

import java.io.Serializable;

import me.ele.uetool.base.Element;

/**
 * @author zijian.cheng
 * @date 2020/7/20
 */
public class ElementBean implements Serializable {

    private String currentPage;
    private int sort;
    private String resId;
    private String text;
    private String resName;
    private String className;
    private String clickable;
    private String viewClickClass;
    private Rect rect;

    private long stepDelay = 800;

    private PointF fromPoint = new PointF();
    private PointF toPoint = new PointF();
    private long scrollDuration;

    private long listStartItem;
    private long listEndItem;
    private long listLoopCount;

    public static ElementBean createElementBean(Element element) {
        View view = element.getView();
        ElementBean currentElement = new ElementBean();
        String resId = Reflect.on("me.ele.uetool.Util").call("getResId", view.getId()).get();
        currentElement.setResId(resId);
        String resourceName = Reflect.on("me.ele.uetool.Util").call("getResourceName", view.getId()).get();
        currentElement.setResName(resourceName);
        currentElement.setClassName(view.getClass().getName());
        currentElement.setClickable(Boolean.toString(view.isClickable()).toUpperCase());
        String clickListener = Reflect.on("me.ele.uetool.Util").call("getViewClickListener", view).get();
        currentElement.setViewClickListener(clickListener);
        currentElement.setRect(element.getRect());
        if (view instanceof TextView) {
            currentElement.setText(((TextView) view).getText().toString());
        }
        return currentElement;
    }

    public String getUniqueId() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(resId);
        stringBuffer.append(text);
        stringBuffer.append(resName);
        stringBuffer.append(className);
        stringBuffer.append(clickable);
        stringBuffer.append(viewClickClass);
        if (rect != null) {
            stringBuffer.append(rect.toShortString());
        }
        return MD5Utils.get32MD5String(stringBuffer.toString());
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getClickable() {
        return clickable;
    }

    public void setViewClickClass(String viewClickClass) {
        this.viewClickClass = viewClickClass;
    }

    public void setFromPoint(PointF fromPoint) {
        this.fromPoint = fromPoint;
    }

    public void setToPoint(PointF toPoint) {
        this.toPoint = toPoint;
    }

    public PointF getFromPoint() {
        return fromPoint;
    }

    public PointF getToPoint() {
        return toPoint;
    }

    public long getScrollDuration() {
        return scrollDuration;
    }

    public void setScrollDuration(long scrollDuration) {
        this.scrollDuration = scrollDuration;
    }

    public void setListEndItem(long listEndItem) {
        this.listEndItem = listEndItem;
    }

    public void setListStartItem(long listStartItem) {
        this.listStartItem = listStartItem;
    }

    public long getListEndItem() {
        return listEndItem;
    }

    public long getListStartItem() {
        return listStartItem;
    }

    public void setListLoopCount(long listLoopCount) {
        this.listLoopCount = listLoopCount;
    }

    public long getListLoopCount() {
        return listLoopCount;
    }

    public long getStepDelay() {
        return stepDelay;
    }

    public void setStepDelay(long stepDelay) {
        this.stepDelay = stepDelay;
    }

    public String getText() {
        return text;
    }

    public ElementBean() {
    }

    public ElementBean(String currentPage) {
        this.currentPage = currentPage;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public String getViewClickClass() {
        return viewClickClass;
    }

    public String getResId() {
        return resId;
    }

    public Rect getRect() {
        return rect;
    }

    public String getResName() {
        return resName;
    }

    public String getClassName() {
        return className;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public void setClickable(String clickable) {
        this.clickable = clickable;
    }

    public void setViewClickListener(String viewClickClass) {
        this.viewClickClass = viewClickClass;
    }

    public void setText(String text) {
        this.text = text;
    }
}
