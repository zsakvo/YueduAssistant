package cc.zsakvo.yueduhchelper.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;

import androidx.core.content.res.TypedArrayUtils;
import moe.shizuku.preference.PreferenceViewHolder;

public class CheckBoxPreference extends TwoStatePreference {
    private final cc.zsakvo.yueduhchelper.preference.CheckBoxPreference.Listener mListener = new cc.zsakvo.yueduhchelper.preference.CheckBoxPreference.Listener();

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked);
                return;
            }
            cc.zsakvo.yueduhchelper.preference.CheckBoxPreference.this.setChecked(isChecked);
        }
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, moe.shizuku.preference.R.style.Preference_CheckBoxPreference_Material);
    }

    @SuppressLint("RestrictedApi")
    public CheckBoxPreference(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                moe.shizuku.preference.R.styleable.CheckBoxPreference, defStyleAttr, defStyleRes);

        setSummaryOn(TypedArrayUtils.getString(a, moe.shizuku.preference.R.styleable.CheckBoxPreference_summaryOn,
                moe.shizuku.preference.R.styleable.CheckBoxPreference_android_summaryOn));

        setSummaryOff(TypedArrayUtils.getString(a, moe.shizuku.preference.R.styleable.CheckBoxPreference_summaryOff,
                moe.shizuku.preference.R.styleable.CheckBoxPreference_android_summaryOff));

        setDisableDependentsState(TypedArrayUtils.getBoolean(a,
                moe.shizuku.preference.R.styleable.CheckBoxPreference_disableDependentsState,
                moe.shizuku.preference.R.styleable.CheckBoxPreference_android_disableDependentsState, false));

        a.recycle();
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, moe.shizuku.preference.R.attr.checkBoxPreferenceStyle);
    }

    public CheckBoxPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        syncCheckboxView(holder.findViewById(moe.shizuku.preference.R.id.checkbox));

        syncSummaryView(holder);
    }


    private void syncCheckboxView(View view) {
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setOnCheckedChangeListener(null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(mChecked);
        }
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setOnCheckedChangeListener(mListener);
        }
    }
}
