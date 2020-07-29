package me.ele.uetool;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import me.ele.uetool.base.config.ConfigCt;
import me.ele.uetool.base.db.ElementBean;
import me.ele.uetool.base.db.MMKVUtil;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.AppTaskInfo;

import java.util.ArrayList;
import java.util.List;

import me.ele.uetool.attrdialog.AttrsDialogItemViewBinder;
import me.ele.uetool.attrdialog.AttrsDialogMultiTypePool;
import me.ele.uetool.base.Element;
import me.ele.uetool.base.IAttrs;
import me.ele.uetool.base.ItemArrayList;

import me.ele.uetool.base.item.AddMinusEditItem;
import me.ele.uetool.base.item.BitmapItem;
import me.ele.uetool.base.item.BriefDescItem;
import me.ele.uetool.base.item.EditStepItem;
import me.ele.uetool.base.item.EditTextItem;
import me.ele.uetool.base.item.Item;
import me.ele.uetool.base.item.SwitchItem;
import me.ele.uetool.base.item.TextItem;
import me.ele.uetool.base.item.TitleItem;

import static me.ele.uetool.base.DimenUtil.dip2px;

public class Adapter extends RecyclerView.Adapter {

    private List<Item> items = new ItemArrayList<>();
    private List<Item> validItems = new ArrayList<>();
    private List<Item> selectedItems = new ArrayList<>();
    private AttrDialogCallback callback;

    public void setAttrDialogCallback(AttrDialogCallback callback) {
        this.callback = callback;
    }

    public AttrDialogCallback getAttrDialogCallback() {
        return this.callback;
    }

    public void notifyDataSetChanged(Element element) {
        items.clear();
        validItems.clear();
        selectedItems.clear();
        for (String attrsProvider : UETool.getInstance().getAttrsProvider()) {
            try {
                IAttrs attrs = (IAttrs) Class.forName(attrsProvider).newInstance();
                items.addAll(attrs.getAttrs(element));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void notifyValidViewItemInserted(int positionStart, List<Item> validItems) {
        this.validItems.addAll(validItems);
        items.addAll(positionStart, validItems);
        notifyItemRangeInserted(positionStart, validItems.size());
    }

    public void notifyValidViewItemRemoved(int positionStart) {
        items.removeAll(validItems);
        notifyItemRangeRemoved(positionStart, validItems.size());
    }

    public void notifySelectedItemInserted(int positionStart, List<Item> selectedItems) {
        this.selectedItems.addAll(selectedItems);
        items.addAll(positionStart, selectedItems);
        notifyItemRangeInserted(positionStart, selectedItems.size());
    }

    public void notifySelectedItemRemoved(int positionStart) {
        items.removeAll(selectedItems);
        notifyItemRangeRemoved(positionStart, selectedItems.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AttrsDialogMultiTypePool pool = UETool.getInstance().getAttrsDialogMultiTypePool();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return pool.getItemViewBinder(viewType).onCreateViewHolder(inflater, parent, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AttrsDialogMultiTypePool pool = UETool.getInstance().getAttrsDialogMultiTypePool();
        ((AttrsDialogItemViewBinder) pool.getItemViewBinder(holder.getItemViewType())).onBindViewHolder(holder, getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItem(position);
        AttrsDialogMultiTypePool pool = UETool.getInstance().getAttrsDialogMultiTypePool();
        return pool.getItemType(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends Item> T getItem(int adapterPosition) {
        if (adapterPosition < 0 || adapterPosition >= items.size()) {
            return null;
        }
        return (T) items.get(adapterPosition);
    }

    public static abstract class BaseViewHolder<T extends Item> extends RecyclerView.ViewHolder {

        protected T item;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(T t) {
            item = t;
        }
    }

    public static class TitleViewHolder extends BaseViewHolder<TitleItem> {

        private TextView vTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            vTitle = (TextView) itemView;
        }

        public static TitleViewHolder newInstance(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(14);
            textView.setPadding(dip2px(10), dip2px(10), dip2px(10), dip2px(10));

            return new TitleViewHolder(textView);
        }

        @Override
        public void bindView(TitleItem titleItem) {
            super.bindView(titleItem);
            vTitle.setText(titleItem.getName());
        }
    }

    public static class TextViewHolder extends BaseViewHolder<TextItem> {

        private TextView vName;
        private TextView vDetail;

        public TextViewHolder(View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.name);
            vDetail = itemView.findViewById(R.id.detail);
        }

        public static TextViewHolder newInstance(ViewGroup parent) {

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));


            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);
            textView.setMaxLines(1);

            linearLayout.addView(textView);

            TextView textView2 = new TextView(parent.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(10);
            textView2.setLayoutParams(layoutParams);
            textView2.setTextColor(Color.BLACK);
            textView2.setTextSize(12);
            textView2.setId(R.id.detail);
            textView2.setGravity(Gravity.RIGHT);
            linearLayout.addView(textView2);

            return new TextViewHolder(linearLayout);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_text, parent, false)
        }

        @Override
        public void bindView(final TextItem textItem) {
            super.bindView(textItem);
            vName.setText(textItem.getName());
            vDetail.setText(textItem.getDetail());
            vDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textItem.isEnableCopy()) {
                        Util.clipText(textItem.getDetail());
                    }
                }
            });
        }
    }

    public static class EditTextViewHolder<T extends EditTextItem>
            extends BaseViewHolder<T> {

        protected TextView vName;
        protected EditText vDetail;
        @Nullable
        private View vColor;

        protected TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (item.getType() == EditTextItem.Type.TYPE_TEXT) {
                        TextView textView = ((TextView) (item.getElement().getView()));
                        if (!TextUtils.equals(textView.getText().toString(), s.toString())) {
                            textView.setText(s.toString());
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_TEXT_SIZE) {
                        TextView textView = ((TextView) (item.getElement().getView()));
                        float textSize = Float.valueOf(s.toString());
                        if (textView.getTextSize() != textSize) {
                            textView.setTextSize(textSize);
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_TEXT_COLOR) {
                        TextView textView = ((TextView) (item.getElement().getView()));
                        int color = Color.parseColor(vDetail.getText().toString());
                        if (color != textView.getCurrentTextColor()) {
                            vColor.setBackgroundColor(color);
                            textView.setTextColor(color);
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_WIDTH) {
                        View view = item.getElement().getView();
                        int width = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(width - view.getWidth()) >= dip2px(1)) {
                            view.getLayoutParams().width = width;
                            view.requestLayout();
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_HEIGHT) {
                        View view = item.getElement().getView();
                        int height = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(height - view.getHeight()) >= dip2px(1)) {
                            view.getLayoutParams().height = height;
                            view.requestLayout();
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_LEFT) {
                        View view = item.getElement().getView();
                        int paddingLeft = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(paddingLeft - view.getPaddingLeft()) >= dip2px(1)) {
                            view.setPadding(paddingLeft, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_RIGHT) {
                        View view = item.getElement().getView();
                        int paddingRight = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(paddingRight - view.getPaddingRight()) >= dip2px(1)) {
                            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), paddingRight, view.getPaddingBottom());
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_TOP) {
                        View view = item.getElement().getView();
                        int paddingTop = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(paddingTop - view.getPaddingTop()) >= dip2px(1)) {
                            view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(), view.getPaddingBottom());
                        }
                    } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_BOTTOM) {
                        View view = item.getElement().getView();
                        int paddingBottom = dip2px(Integer.valueOf(s.toString()));
                        if (Math.abs(paddingBottom - view.getPaddingBottom()) >= dip2px(1)) {
                            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), paddingBottom);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        public EditTextViewHolder(View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.name);
            vDetail = itemView.findViewById(R.id.detail);
            vColor = itemView.findViewById(R.id.color);
            vDetail.addTextChangedListener(textWatcher);
        }

        public static EditTextViewHolder newInstance(ViewGroup parent) {

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));

            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);

            linearLayout.addView(textView);

            LinearLayout linearLayout2 = new LinearLayout(parent.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(10);
            linearLayout2.setLayoutParams(layoutParams);
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout2.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            View view = new View(parent.getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dip2px(25), dip2px(25));
            layoutParams1.rightMargin = dip2px(5);
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.color);

            linearLayout2.addView(view);

            EditText editText = new EditText(parent.getContext());
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            editText.setLayoutParams(layoutParams2);
            editText.setBackgroundDrawable(null);
            editText.setTextSize(12);
            editText.setTextColor(Color.DKGRAY);
            editText.setId(R.id.detail);

            linearLayout2.addView(editText);

            linearLayout.addView(linearLayout2);

            return new EditTextViewHolder(linearLayout);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_edit_text, parent, false)
        }

        @Override
        public void bindView(final T editTextItem) {
            super.bindView(editTextItem);
            vName.setText(editTextItem.getName());
            vDetail.setText(editTextItem.getDetail());
            if (vColor != null) {
                try {
                    vColor.setBackgroundColor(Color.parseColor(editTextItem.getDetail()));
                    vColor.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    vColor.setVisibility(View.GONE);
                }
            }
        }
    }

    public static class AddMinusEditViewHolder extends EditTextViewHolder<AddMinusEditItem> {

        private View vAdd;
        private View vMinus;

        public AddMinusEditViewHolder(View itemView) {
            super(itemView);
            vAdd = itemView.findViewById(R.id.add);
            vMinus = itemView.findViewById(R.id.minus);
            vAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int textSize = Integer.valueOf(vDetail.getText().toString());
                        vDetail.setText(String.valueOf(++textSize));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            vMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int textSize = Integer.valueOf(vDetail.getText().toString());
                        if (textSize > 0) {
                            vDetail.setText(String.valueOf(--textSize));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public static AddMinusEditViewHolder newInstance(ViewGroup parent) {

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));

            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);

            linearLayout.addView(textView);

            LinearLayout linearLayout2 = new LinearLayout(parent.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout2.setLayoutParams(layoutParams);
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout2.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);


            TextView view = new TextView(parent.getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout2.addView(view);

            EditText editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(30));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail);

            linearLayout2.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout2.addView(view);

            linearLayout.addView(linearLayout2);
            return new AddMinusEditViewHolder(linearLayout);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_add_minus_edit, parent, false)
        }

        @Override
        public void bindView(AddMinusEditItem editTextItem) {
            super.bindView(editTextItem);
        }
    }

    public static class SwitchViewHolder extends BaseViewHolder<SwitchItem> {

        private AttrDialogCallback callback;
        private TextView vName;
        private Switch vSwitch;

        public SwitchViewHolder(View itemView, final AttrDialogCallback callback) {
            super(itemView);
            this.callback = callback;
            vName = itemView.findViewById(R.id.name);
            vSwitch = itemView.findViewById(R.id.switch_view);
            vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        //防止初始化的时候发出监听
                        if (!buttonView.isPressed()) {
                            if (item.getType() == SwitchItem.Type.TYPE_SELECT_STEP && isChecked) {
                                String currentWindow = "";
                                AppTaskInfo appTaskInfo = VirtualCore.get().getForegroundTask(ConfigCt.AppName);
                                if (appTaskInfo != null) {
                                    currentWindow = appTaskInfo.topActivity.getClassName();
                                }
                                ElementBean currentElement = item.getElementBean();
                                if (currentElement == null) {
                                    currentElement = ElementBean.createElementBean(item.getElement());
                                }
                                currentElement.setCurrentPage(currentWindow);
                                if (callback != null) {
                                    callback.showSelectedViews(getAdapterPosition(), isChecked, currentElement);
                                }
                            }
                            return;
                        }
                        if (item.getType() == SwitchItem.Type.TYPE_SELECT_STEP) {
                            try {
                                String currentWindow = "";
                                AppTaskInfo appTaskInfo = VirtualCore.get().getForegroundTask(ConfigCt.AppName);
                                if (appTaskInfo != null) {
                                    currentWindow = appTaskInfo.topActivity.getClassName();
                                }
                                ElementBean currentElement = item.getElementBean();
                                if (currentElement == null) {
                                    currentElement = ElementBean.createElementBean(item.getElement());
                                }
                                currentElement.setCurrentPage(currentWindow);

                                List<ElementBean> beforeElement = MMKVUtil.getInstance().getElements();
                                if (beforeElement == null) {
                                    beforeElement = new ArrayList<>();
                                }
                                String nameText = "未设置";
                                if (isChecked) {
                                    beforeElement.add(currentElement);
                                    MMKVUtil.getInstance().setElement(beforeElement);
                                    nameText = beforeElement.size() + "/" + beforeElement.size();
                                } else {
                                    //取消选择
                                    int index = beforeElement.size();
                                    if (index > 0) {
                                        for (ElementBean elementBean : beforeElement) {
                                            if (elementBean.getCurrentPage().equals(currentWindow)) {
                                                if (elementBean.getUniqueId().equals(currentElement.getUniqueId())) {
                                                    beforeElement.remove(elementBean);
                                                    break;
                                                }
                                            }
                                        }
                                        MMKVUtil.getInstance().setElement(beforeElement);
                                    }
                                }
                                vName.setText(nameText);
                                item.setChecked(isChecked);
                                if (callback != null) {
                                    callback.showSelectedViews(getAdapterPosition(), isChecked, currentElement);
                                }
                            } catch (Exception e) {
                                item.setChecked(false);
                                e.printStackTrace();
                            }
                            return;
                        } else if (item.getType() == SwitchItem.Type.TYPE_MOVE) {
                            if (callback != null && isChecked) {
                                callback.enableMove();
                            }
                            return;
                        } else if (item.getType() == SwitchItem.Type.TYPE_SHOW_VALID_VIEWS) {
                            item.setChecked(isChecked);
                            if (callback != null) {
                                callback.showValidViews(getAdapterPosition(), isChecked);
                            }
                            return;
                        }
                        if (item.getElement().getView() instanceof TextView) {
                            TextView textView = ((TextView) (item.getElement().getView()));
                            if (item.getType() == SwitchItem.Type.TYPE_IS_BOLD) {
                                textView.setTypeface(null, isChecked ? Typeface.BOLD : Typeface.NORMAL);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public static SwitchViewHolder newInstance(ViewGroup parent, AttrDialogCallback callback) {

            RelativeLayout relativeLayout = new RelativeLayout(parent.getContext());
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            relativeLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));

            TextView textView = new TextView(parent.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);
            textView.setMaxLines(1);

            relativeLayout.addView(textView);


            Switch switchCompat = new Switch(parent.getContext());
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            switchCompat.setLayoutParams(layoutParams2);
            switchCompat.setId(R.id.switch_view);

            relativeLayout.addView(switchCompat);

            return new SwitchViewHolder(relativeLayout, callback);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_switch, parent, false)
        }

        @Override
        public void bindView(SwitchItem switchItem) {
            super.bindView(switchItem);
            vName.setText(switchItem.getName());
            //先设为默认
            vSwitch.setChecked(false);
            vSwitch.setChecked(switchItem.isChecked());
            vSwitch.setEnabled(!switchItem.isDisEnable());
        }
    }

    public static class BitmapInfoViewHolder extends BaseViewHolder<BitmapItem> {

        private final int imageHeight = dip2px(58);

        private TextView vName;
        private ImageView vImage;
        private TextView vInfo;

        public BitmapInfoViewHolder(View itemView) {
            super(itemView);

            vName = itemView.findViewById(R.id.name);
            vImage = itemView.findViewById(R.id.image);
            vInfo = itemView.findViewById(R.id.info);
        }

        public static BitmapInfoViewHolder newInstance(ViewGroup parent) {

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));

            TextView textView = new TextView(parent.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);
            textView.setMaxLines(1);

            linearLayout.addView(textView);

            //todo
            LinearLayout linearLayout2 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(10);
            linearLayout2.setLayoutParams(layoutParams);
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            linearLayout2.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            ImageView imageView = new ImageView(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setId(R.id.image);

            linearLayout2.addView(imageView);

            textView = new TextView(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = dip2px(2);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(12);
            textView.setId(R.id.info);
            linearLayout2.addView(textView);

            linearLayout.addView(linearLayout2);

            return new BitmapInfoViewHolder(linearLayout);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_bitmap_info, parent, false)
        }

        @Override
        public void bindView(BitmapItem bitmapItem) {
            super.bindView(bitmapItem);

            vName.setText(bitmapItem.getName());
            Bitmap bitmap = bitmapItem.getBitmap();

            int height = Math.min(bitmap.getHeight(), imageHeight);
            int width = (int) ((float) height / bitmap.getHeight() * bitmap.getWidth());

            ViewGroup.LayoutParams layoutParams = vImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            vImage.setImageBitmap(bitmap);
            vInfo.setText(bitmap.getWidth() + "px*" + bitmap.getHeight() + "px");
        }
    }

    public static class BriefDescViewHolder extends BaseViewHolder<BriefDescItem> {

        private TextView vDesc;

        public BriefDescViewHolder(View itemView, final AttrDialogCallback callback) {
            super(itemView);
            vDesc = (TextView) itemView;
            vDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.selectView(item.getElement());
                    }
                }
            });
        }

        public static BriefDescViewHolder newInstance(ViewGroup parent, AttrDialogCallback callback) {
            TextView textView = new TextView(parent.getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setBackgroundColor(Color.LTGRAY);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(12);
            textView.setId(R.id.info);
            textView.setPadding(dip2px(10), dip2px(2), dip2px(10), dip2px(2));

            return new BriefDescViewHolder(textView, callback);//LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_brief_view_desc, parent, false)
        }

        @Override
        public void bindView(BriefDescItem briefDescItem) {
            super.bindView(briefDescItem);
            View view = briefDescItem.getElement().getView();
            StringBuilder sb = new StringBuilder();
            sb.append(view.getClass().getName());
            String resName = Util.getResourceName(view.getId());
            if (!TextUtils.isEmpty(resName)) {
                sb.append("@").append(resName);
            }
            vDesc.setText(sb.toString());

            vDesc.setSelected(briefDescItem.isSelected());
        }
    }

    public static class EditStepViewHolder<T extends EditStepItem> extends BaseViewHolder<EditStepItem> {

        private View vLayoutFrom;
        private View vLayoutTo;
        private View vLayoutDuration;
        private View vLayoutLoop;

        private TextView vName;

        private View vAddFx;
        private EditText vDetailFx;
        private View vMinusFx;

        private View vAddFy;
        private EditText vDetailFy;
        private View vMinusFy;

        private View vAddTx;
        private EditText vDetailTx;
        private View vMinusTx;

        private View vAddTy;
        private EditText vDetailTy;
        private View vMinusTy;

        private View vAddDelay;
        private EditText vDetailDelay;
        private View vMinusDelay;

        private View vAddStart;
        private EditText vDetailStart;
        private View vMinusStart;

        private View vAddEnd;
        private EditText vDetailEnd;
        private View vMinusEnd;

        private View vAddLoopCount;
        private EditText vDetailLoopCount;
        private View vMinusLoopCount;

        class DetailTextWatcher implements TextWatcher {
            private int currentId;

            public DetailTextWatcher(int currentId) {
                this.currentId = currentId;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    ElementBean elementBean = item.getElementBean();
                    long currentValue = Long.parseLong(s.toString());
                    if (currentId == vDetailDelay.getId()) {
                        if (item.getType() == EditStepItem.Type.TYPE_DELAY) {
                            //点击延迟
                            if (elementBean.getStepDelay() == currentValue) {
                                return;
                            }
                            elementBean.setStepDelay(currentValue);
                        } else if (item.getType() == EditStepItem.Type.TYPE_SCROLL_VIEW) {
                            //滑动时长
                            if (elementBean.getScrollDuration() == currentValue) {
                                return;
                            }
                            elementBean.setScrollDuration(currentValue);
                        }
                    } else if (currentId == vDetailFx.getId()) {
                        //滑动开始x坐标
                        if (elementBean.getFromPoint().x == currentValue) {
                            return;
                        }
                        elementBean.getFromPoint().x = currentValue;
                    } else if (currentId == vDetailFy.getId()) {
                        //滑动开始y坐标
                        if (elementBean.getFromPoint().y == currentValue) {
                            return;
                        }
                        elementBean.getFromPoint().y = currentValue;
                    } else if (currentId == vDetailTx.getId()) {
                        //滑动结束x坐标
                        if (elementBean.getToPoint().x == currentValue) {
                            return;
                        }
                        elementBean.getToPoint().x = currentValue;
                    } else if (currentId == vDetailTy.getId()) {
                        //滑动结束y坐标
                        if (elementBean.getToPoint().y == currentValue) {
                            return;
                        }
                        elementBean.getToPoint().y = currentValue;
                    } else if (currentId == vDetailStart.getId()) {
                        //点击list开始的位置
                        if (elementBean.getListStartItem() == currentValue) {
                            return;
                        }
                        elementBean.setListStartItem(currentValue);
                    } else if (currentId == vDetailEnd.getId()) {
                        //点击list结束的位置
                        if (elementBean.getListEndItem() == currentValue) {
                            return;
                        }
                        elementBean.setListEndItem(currentValue);
                    } else if (currentId == vDetailLoopCount.getId()) {
                        //点击list循环次数
                        if (elementBean.getListLoopCount() == currentValue) {
                            return;
                        }
                        elementBean.setListLoopCount(currentValue);
                    }

                    item.setElementBean(elementBean);
                    List<ElementBean> beforeElement = MMKVUtil.getInstance().getElements();
                    if (beforeElement == null) {
                        beforeElement = new ArrayList<>();
                    }
                    ElementBean tempBean;
                    for (int i = 0; i < beforeElement.size(); i++) {
                        tempBean = beforeElement.get(i);
                        if (tempBean.getUniqueId().equals(elementBean.getUniqueId())) {
                            beforeElement.set(i, elementBean);
                            break;
                        }
                    }
                    MMKVUtil.getInstance().setElement(beforeElement);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }

        public EditStepViewHolder(View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.name);

            vLayoutFrom = itemView.findViewById(R.id.id_ly_from);
            vLayoutTo = itemView.findViewById(R.id.id_ly_to);
            vLayoutDuration = itemView.findViewById(R.id.id_ly_duration);
            vLayoutLoop = itemView.findViewById(R.id.id_ly_loop);

            vAddFx = itemView.findViewById(R.id.add_fx);
            vDetailFx = itemView.findViewById(R.id.detail_fx);
            vMinusFx = itemView.findViewById(R.id.minus_fx);

            vAddFy = itemView.findViewById(R.id.add_fy);
            vDetailFy = itemView.findViewById(R.id.detail_fy);
            vMinusFy = itemView.findViewById(R.id.minus_fy);

            vAddTx = itemView.findViewById(R.id.add_tx);
            vDetailTx = itemView.findViewById(R.id.detail_tx);
            vMinusTx = itemView.findViewById(R.id.minus_tx);

            vAddTy = itemView.findViewById(R.id.add_ty);
            vDetailTy = itemView.findViewById(R.id.detail_ty);
            vMinusTy = itemView.findViewById(R.id.minus_ty);

            vAddDelay = itemView.findViewById(R.id.add_delay);
            vDetailDelay = itemView.findViewById(R.id.detail_delay);
            vMinusDelay = itemView.findViewById(R.id.minus_delay);

            vAddStart = itemView.findViewById(R.id.add_s);
            vDetailStart = itemView.findViewById(R.id.detail_s);
            vMinusStart = itemView.findViewById(R.id.minus_s);

            vAddEnd = itemView.findViewById(R.id.add_e);
            vDetailEnd = itemView.findViewById(R.id.detail_e);
            vMinusEnd = itemView.findViewById(R.id.minus_e);

            vAddLoopCount = itemView.findViewById(R.id.add_lc);
            vDetailLoopCount = itemView.findViewById(R.id.detail_lc);
            vMinusLoopCount = itemView.findViewById(R.id.minus_lc);

            vDetailFx.addTextChangedListener(new DetailTextWatcher(vDetailFx.getId()));
            vDetailFy.addTextChangedListener(new DetailTextWatcher(vDetailFy.getId()));
            vDetailTx.addTextChangedListener(new DetailTextWatcher(vDetailTx.getId()));
            vDetailTy.addTextChangedListener(new DetailTextWatcher(vDetailTy.getId()));
            vDetailDelay.addTextChangedListener(new DetailTextWatcher(vDetailDelay.getId()));
            vDetailStart.addTextChangedListener(new DetailTextWatcher(vDetailStart.getId()));
            vDetailEnd.addTextChangedListener(new DetailTextWatcher(vDetailEnd.getId()));
            vDetailLoopCount.addTextChangedListener(new DetailTextWatcher(vDetailLoopCount.getId()));

        }

        @Override
        public void bindView(EditStepItem editStepItem) {
            super.bindView(editStepItem);
            vLayoutFrom.setVisibility(View.GONE);
            vLayoutTo.setVisibility(View.GONE);
            vLayoutDuration.setVisibility(View.GONE);
            vLayoutLoop.setVisibility(View.GONE);

            vName.setText(editStepItem.getName());
            int type = editStepItem.getType();
            ElementBean elementBean = editStepItem.getElementBean();
            if (type == EditStepItem.Type.TYPE_DELAY) {
                vLayoutDuration.setVisibility(View.VISIBLE);
                vDetailDelay.setText(String.valueOf(elementBean.getStepDelay()));
                setViewOnclick(vAddDelay, vDetailDelay, true, 100);
                setViewOnclick(vMinusDelay, vDetailDelay, false, 100);
            } else if (type == EditStepItem.Type.TYPE_SCROLL_VIEW) {
                vLayoutFrom.setVisibility(View.VISIBLE);
                vLayoutTo.setVisibility(View.VISIBLE);
                vLayoutDuration.setVisibility(View.VISIBLE);

                PointF fromPoint = elementBean.getFromPoint();
                vDetailFx.setText(String.valueOf(fromPoint == null ? 0 : (int) fromPoint.x));
                setViewOnclick(vAddFx, vDetailFx, true, 100);
                setViewOnclick(vMinusFx, vDetailFx, false, 100);

                vDetailFy.setText(String.valueOf(fromPoint == null ? 0 : (int) fromPoint.y));
                setViewOnclick(vAddFy, vDetailFy, true, 100);
                setViewOnclick(vMinusFy, vDetailFy, false, 100);

                PointF toPoint = elementBean.getToPoint();
                vDetailTx.setText(String.valueOf(toPoint == null ? 0 : (int) toPoint.x));
                setViewOnclick(vAddTx, vDetailTx, true, 100);
                setViewOnclick(vMinusTx, vDetailTx, false, 100);

                vDetailTy.setText(String.valueOf(toPoint == null ? 0 : (int) toPoint.y));
                setViewOnclick(vAddTy, vDetailTy, true, 100);
                setViewOnclick(vMinusTy, vDetailTy, false, 100);

                vDetailDelay.setText(String.valueOf(elementBean.getScrollDuration()));
                setViewOnclick(vAddDelay, vDetailDelay, true, 500);
                setViewOnclick(vMinusDelay, vDetailDelay, false, 500);
            } else if (type == EditStepItem.Type.TYPE_LOOP_CLICK) {
                vLayoutLoop.setVisibility(View.VISIBLE);

                vDetailStart.setText(String.valueOf(elementBean.getListStartItem()));
                setViewOnclick(vAddStart, vDetailStart, true, 1);
                setViewOnclick(vMinusStart, vDetailStart, false, 1);

                vDetailEnd.setText(String.valueOf(elementBean.getListEndItem()));
                setViewOnclick(vAddEnd, vDetailEnd, true, 1);
                setViewOnclick(vMinusEnd, vDetailEnd, false, 1);

                vDetailLoopCount.setText(String.valueOf(elementBean.getListLoopCount()));
                setViewOnclick(vAddLoopCount, vDetailLoopCount, true, 1);
                setViewOnclick(vMinusLoopCount, vDetailLoopCount, false, 1);
            }
        }

        private void setViewOnclick(View view, final EditText detail, final boolean up, final int step) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float textSize = 0;
                    try {
                        textSize = Float.parseFloat(detail.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    detail.setText(String.valueOf(up ? (int) (textSize + step) : (int) (textSize - step)));
                }
            });
        }

        public static EditStepViewHolder<EditStepItem> newInstance(ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setPadding(dip2px(10), dip2px(5), dip2px(10), dip2px(5));

            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(12);
            textView.setId(R.id.name);
            linearLayout.addView(textView);

            LinearLayout childLinearLayout = new LinearLayout(parent.getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            childLinearLayout.setLayoutParams(layoutParams);
            childLinearLayout.setOrientation(LinearLayout.VERTICAL);
            childLinearLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            ////////////////////////////////////////////////
            LinearLayout linearLayout2 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout2.setId(R.id.id_ly_from);
            linearLayout2.setLayoutParams(layoutParams);
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout2.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            /////////////////
            TextView view = new TextView(parent.getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("fromX");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout2.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_fx);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout2.addView(view);

            EditText editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_fx);

            linearLayout2.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_fx);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout2.addView(view);

            ////////////////

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setGravity(Gravity.CENTER);
            view.setText("fromY");
            view.setTextSize(12);

            linearLayout2.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_fy);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout2.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_fy);

            linearLayout2.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_fy);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout2.addView(view);
            //////////////////////

            childLinearLayout.addView(linearLayout2);

            ///////////////////////////////////////////////

            LinearLayout linearLayout3 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout3.setId(R.id.id_ly_to);
            linearLayout3.setLayoutParams(layoutParams);
            linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout3.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            ////////////////

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("toX");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout3.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_tx);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout3.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_tx);

            linearLayout3.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_tx);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout3.addView(view);

            ///////////////

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("toY");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout3.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_ty);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout3.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_ty);

            linearLayout3.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_ty);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout3.addView(view);
            ///////////////

            childLinearLayout.addView(linearLayout3);
            ///////////////////////////////////////////////

            LinearLayout linearLayout4 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout4.setId(R.id.id_ly_duration);
            linearLayout4.setLayoutParams(layoutParams);
            linearLayout4.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout4.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(60), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("duration");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout4.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_delay);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout4.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_delay);

            linearLayout4.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_delay);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout4.addView(view);

            childLinearLayout.addView(linearLayout4);
            ///////////////////////////////////////////////

            LinearLayout linearLayout5 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout5.setId(R.id.id_ly_loop);
            linearLayout5.setLayoutParams(layoutParams);
            linearLayout5.setOrientation(LinearLayout.VERTICAL);
            linearLayout5.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            ////////////////////

            LinearLayout linearLayout51 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout51.setLayoutParams(layoutParams);
            linearLayout51.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout51.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("start");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout51.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_s);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout51.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_s);

            linearLayout51.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_s);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout51.addView(view);
            ////////////////////

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(40), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("end");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout51.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_e);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout51.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_e);

            linearLayout51.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_e);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout51.addView(view);

            linearLayout5.addView(linearLayout51);
            ////////////////////

            LinearLayout linearLayout52 = new LinearLayout(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout52.setLayoutParams(layoutParams);
            linearLayout52.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout52.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(70), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setTextColor(Color.BLACK);
            view.setText("LoopCount");
            view.setGravity(Gravity.CENTER);
            view.setTextSize(12);

            linearLayout52.addView(view);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.minus_lc);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("-");
            view.setTextSize(12);

            linearLayout52.addView(view);

            editText = new EditText(parent.getContext());
            layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = dip2px(5);
            layoutParams.rightMargin = dip2px(5);
            editText.setLayoutParams(layoutParams);
            editText.setBackgroundDrawable(null);
            editText.setGravity(Gravity.CENTER);
            editText.setMinWidth(dip2px(24));
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(12);
            editText.setId(R.id.detail_lc);

            linearLayout52.addView(editText);

            view = new TextView(parent.getContext());
            layoutParams1 = new LinearLayout.LayoutParams(dip2px(20), dip2px(20));
            view.setLayoutParams(layoutParams1);
            view.setId(R.id.add_lc);
            view.setClickable(true);
            view.setTextColor(Color.BLACK);
            view.setText("+");

            linearLayout52.addView(view);

            linearLayout5.addView(linearLayout52);
            ////////////////////

            childLinearLayout.addView(linearLayout5);
            ///////////////////////////////////////////////

            linearLayout.addView(childLinearLayout);
            return new EditStepViewHolder<EditStepItem>(linearLayout);
        }
    }
}