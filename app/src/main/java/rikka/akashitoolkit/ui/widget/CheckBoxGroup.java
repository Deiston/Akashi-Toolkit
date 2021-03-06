package rikka.akashitoolkit.ui.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import rikka.akashitoolkit.R;
import rikka.akashitoolkit.utils.Utils;

/**
 * Created by Rikka on 2016/2/16.
 */
public class CheckBoxGroup extends LinearLayout {
    private int mItemLayoutResId;
    private int mTitleLayoutResId;
    private int mChecked;
    private Map<CheckBox, Integer> mMap;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, int checked);
    }

    public CheckBoxGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);

        mMap = new ArrayMap<>();
        mItemLayoutResId = R.layout.drawer_item_check_box;
        mTitleLayoutResId = R.layout.drawer_item_title;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void addTitle(String title) {
        View view = LayoutInflater.from(getContext())
                .inflate(mTitleLayoutResId, this, false);

        ((TextView) view.findViewById(R.id.textView)).setText(title);
        addView(view);
    }

    public void addItem(@StringRes int resId) {
        addItem(getContext().getString(resId));
    }

    public void addItem(String title) {
        addItem(title, mMap.size());
    }

    public void addItem(String title, int index) {
        View view = LayoutInflater.from(getContext())
                .inflate(mItemLayoutResId, this, false);

        ((TextView) view.findViewById(R.id.textView)).setText(title);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setChecked(false);
        checkBox.setId(NO_ID);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.performClick();
                calcChecked();

                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(v, mChecked);
                }
            }
        });


        addView(view);
        mMap.put(checkBox, index);
    }

    public void addDivider() {
        View view;
        view = new View(getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(4)));
        addView(view);

        view = new View(getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
        view.setBackgroundResource(R.drawable.preference_list_divider_material);
        addView(view);

        view = new View(getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(4)));
        addView(view);
    }

    private void calcChecked() {
        mChecked = 0;
        for (Map.Entry<CheckBox, Integer> entry : mMap.entrySet()) {
            if (entry.getKey().isChecked()) {
                mChecked |= (1 << entry.getValue());
            }
        }
    }

    private void check(int checked) {
        mChecked = checked;

        for (Map.Entry<CheckBox, Integer> entry : mMap.entrySet()) {
            entry.getKey().setChecked((checked & 1 << entry.getValue()) > 0);
        }
    }

    public void setChecked(int checked) {
        check(checked);

        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.index = mChecked;

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.index != -1) {
            setChecked(ss.index);
        }
    }

    public static class SavedState extends BaseSavedState {
        int index;

        public SavedState(Parcel source) {
            super(source);
            index = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(index);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
