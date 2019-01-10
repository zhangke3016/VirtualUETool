package me.ele.uetool;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import me.ele.uetool.base.Element;
import me.ele.uetool.base.IAttrs;
import me.ele.uetool.base.ItemArrayList;
import me.ele.uetool.base.item.AddMinusEditItem;
import me.ele.uetool.base.item.BitmapItem;
import me.ele.uetool.base.item.BriefDescItem;
import me.ele.uetool.base.item.EditTextItem;
import me.ele.uetool.base.item.Item;
import me.ele.uetool.base.item.SwitchItem;
import me.ele.uetool.base.item.TextItem;
import me.ele.uetool.base.item.TitleItem;

import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_ADD_MINUS_EDIT;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_BITMAP;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_BRIEF_DESC;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_EDIT_TEXT;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_SWITCH;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_TEXT;
import static me.ele.uetool.AttrsDialog.Adapter.ViewType.TYPE_TITLE;
import static me.ele.uetool.base.DimenUtil.dip2px;
import static me.ele.uetool.base.DimenUtil.getScreenHeight;
import static me.ele.uetool.base.DimenUtil.getScreenWidth;
import static me.ele.uetool.base.DimenUtil.sp2px;

public class AttrsDialog extends Dialog {

    private RecyclerView vList;
    private Adapter adapter = new Adapter();
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

    public AttrsDialog(Context context) {
//        R.style.uet_Theme_Holo_Dialog_background_Translucent
        super(context);//, R.style.uet_Theme_Holo_Dialog_background_Translucent
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
        linearLayout.addView(recyclerView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(linearLayout);
        vList = recyclerView;//findViewById(R.id.list);uet_dialog_attrs
        vList.setAdapter(adapter);
        vList.setLayoutManager(layoutManager);
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

    public void setAttrDialogCallback(AttrDialogCallback callback) {
        adapter.setAttrDialogCallback(callback);
    }

    public interface AttrDialogCallback {
        void enableMove();

        void showValidViews(int position, boolean isChecked);

        void selectView(Element element);
    }

    public static class Adapter extends RecyclerView.Adapter {

        private List<Item> items = new ItemArrayList<>();
        private List<Item> validItems = new ArrayList<>();
        private AttrDialogCallback callback;

        public void setAttrDialogCallback(AttrDialogCallback callback) {
            this.callback = callback;
        }

        public void notifyDataSetChanged(Element element) {
            items.clear();
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

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_TITLE:
                    return TitleViewHolder.newInstance(parent);
                case TYPE_TEXT:
                    return TextViewHolder.newInstance(parent);
                case TYPE_EDIT_TEXT:
                    return EditTextViewHolder.newInstance(parent);
                case TYPE_SWITCH:
                    return SwitchViewHolder.newInstance(parent, callback);
                case TYPE_ADD_MINUS_EDIT:
                    return AddMinusEditViewHolder.newInstance(parent);
                case TYPE_BITMAP:
                    return BitmapInfoViewHolder.newInstance(parent);
                case TYPE_BRIEF_DESC:
                    return BriefDescViewHolder.newInstance(parent, callback);
            }
            throw new RuntimeException(viewType + " is an unknown view type!");
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getClass() == TitleViewHolder.class) {
                ((TitleViewHolder) holder).bindView((TitleItem) getItem(position));
            } else if (holder.getClass() == TextViewHolder.class) {
                ((TextViewHolder) holder).bindView((TextItem) getItem(position));
            } else if (holder.getClass() == EditTextViewHolder.class) {
                ((EditTextViewHolder) holder).bindView((EditTextItem) getItem(position));
            } else if (holder.getClass() == SwitchViewHolder.class) {
                ((SwitchViewHolder) holder).bindView((SwitchItem) getItem(position));
            } else if (holder.getClass() == AddMinusEditViewHolder.class) {
                ((AddMinusEditViewHolder) holder).bindView((AddMinusEditItem) getItem(position));
            } else if (holder.getClass() == BitmapInfoViewHolder.class) {
                ((BitmapInfoViewHolder) holder).bindView((BitmapItem) getItem(position));
            } else if (holder.getClass() == BriefDescViewHolder.class) {
                ((BriefDescViewHolder) holder).bindView((BriefDescItem) getItem(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            Item item = getItem(position);
            if (item.getClass() == TitleItem.class) {
                return TYPE_TITLE;
            } else if (item.getClass() == TextItem.class) {
                return TYPE_TEXT;
            } else if (item.getClass() == EditTextItem.class) {
                return TYPE_EDIT_TEXT;
            } else if (item.getClass() == SwitchItem.class) {
                return TYPE_SWITCH;
            } else if (item.getClass() == AddMinusEditItem.class) {
                return TYPE_ADD_MINUS_EDIT;
            } else if (item.getClass() == BitmapItem.class) {
                return TYPE_BITMAP;
            } else if (item.getClass() == BriefDescItem.class) {
                return TYPE_BRIEF_DESC;
            }
            throw new RuntimeException("Unknown item type.");
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

        @IntDef({
                TYPE_TITLE,
                TYPE_TEXT,
                TYPE_EDIT_TEXT,
                TYPE_SWITCH,
                TYPE_ADD_MINUS_EDIT,
                TYPE_BITMAP,
                TYPE_BRIEF_DESC,
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface ViewType {
            int TYPE_TITLE = 1;
            int TYPE_TEXT = 2;
            int TYPE_EDIT_TEXT = 3;
            int TYPE_SWITCH = 4;
            int TYPE_ADD_MINUS_EDIT = 5;
            int TYPE_BITMAP = 6;
            int TYPE_BRIEF_DESC = 7;
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

                return new TitleViewHolder(textView);//new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.uet_cell_title, parent, false));
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
                linearLayout2.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);

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
                linearLayout2.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);


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

            private TextView vName;
            private Switch vSwitch;

            public SwitchViewHolder(View itemView, final AttrDialogCallback callback) {
                super(itemView);

                vName = itemView.findViewById(R.id.name);
                vSwitch = itemView.findViewById(R.id.switch_view);
                vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            if (item.getType() == SwitchItem.Type.TYPE_MOVE) {
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
                vSwitch.setChecked(switchItem.isChecked());
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
                linearLayout2.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);

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
    }
}

