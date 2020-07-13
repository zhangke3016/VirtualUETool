package me.ele.uetool.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zijian.cheng
 * @date 2020/7/10
 */
public class ElementBean implements Parcelable {
    private int sort;
    private String id;
    private String resName;
    private String zClass;
    private String clickable;
    private String onClickListener;
    private String text;

    public String getId() {
        return id;
    }

    public String getResName() {
        return resName;
    }

    public ElementBean(int sort, String id,
                       String resName,
                       String zClass,
                       String clickable,
                       String onClickListener,
                       String text) {
        this.sort = sort;
        this.id = id;
        this.resName = resName;
        this.zClass = zClass;
        this.clickable = clickable;
        this.onClickListener = onClickListener;
        this.text = text;
    }

    protected ElementBean(Parcel in) {
        sort = in.readInt();
        id = in.readString();
        resName = in.readString();
        zClass = in.readString();
        clickable = in.readString();
        onClickListener = in.readString();
        text = in.readString();
    }

    public int getSort() {
        return sort;
    }

    public static final Creator<ElementBean> CREATOR = new Creator<ElementBean>() {
        @Override
        public ElementBean createFromParcel(Parcel in) {
            return new ElementBean(in);
        }

        @Override
        public ElementBean[] newArray(int size) {
            return new ElementBean[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sort);
        dest.writeString(id);
        dest.writeString(resName);
        dest.writeString(zClass);
        dest.writeString(clickable);
        dest.writeString(onClickListener);
        dest.writeString(text);
    }
}
