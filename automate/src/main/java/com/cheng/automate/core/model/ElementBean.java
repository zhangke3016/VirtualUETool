package com.cheng.automate.core.model;

import android.graphics.Rect;

import java.io.Serializable;

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

    public String getText() {
        return text;
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
