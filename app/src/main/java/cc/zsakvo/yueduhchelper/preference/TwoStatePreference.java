package cc.zsakvo.yueduhchelper.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RestrictTo;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceViewHolder;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

public class TwoStatePreference extends Preference {

    private CharSequence mSummaryOn;
    private CharSequence mSummaryOff;
    protected boolean mChecked;
    private boolean mCheckedSet;
    private boolean mDisableDependentsState;

    public TwoStatePreference(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TwoStatePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TwoStatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoStatePreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onClick() {
        super.onClick();

        final boolean newValue = !isChecked();
        if (callChangeListener(newValue)) {
            setChecked(newValue);
        }
    }

    /**
     * Sets the checked state and saves it to the {@link android.content.SharedPreferences}.
     *
     * @param checked The checked state.
     */
    public void setChecked(boolean checked) {
        // Always persist/notify the first time; don't assume the field's default of false.
        final boolean changed = mChecked != checked;
        if (changed || !mCheckedSet) {
            mChecked = checked;
            mCheckedSet = true;
//            persistBoolean(checked);
            if (changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    /**
     * Returns the checked state.
     *
     * @return The checked state.
     */
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisable = mDisableDependentsState == mChecked;
        return shouldDisable || super.shouldDisableDependents();
    }

    /**
     * Sets the summary to be shown when checked.
     *
     * @param summary The summary to be shown when checked.
     */
    public void setSummaryOn(CharSequence summary) {
        mSummaryOn = summary;
        if (isChecked()) {
            notifyChanged();
        }
    }

    /**
     * @param summaryResId The summary as a resource.
     * @see #setSummaryOn(CharSequence)
     */
    public void setSummaryOn(int summaryResId) {
        setSummaryOn(getContext().getString(summaryResId));
    }

    /**
     * Returns the summary to be shown when checked.
     *
     * @return The summary.
     */
    public CharSequence getSummaryOn() {
        return mSummaryOn;
    }

    /**
     * Sets the summary to be shown when unchecked.
     *
     * @param summary The summary to be shown when unchecked.
     */
    public void setSummaryOff(CharSequence summary) {
        mSummaryOff = summary;
        if (!isChecked()) {
            notifyChanged();
        }
    }

    /**
     * @param summaryResId The summary as a resource.
     * @see #setSummaryOff(CharSequence)
     */
    public void setSummaryOff(int summaryResId) {
        setSummaryOff(getContext().getString(summaryResId));
    }

    /**
     * Returns the summary to be shown when unchecked.
     *
     * @return The summary.
     */
    public CharSequence getSummaryOff() {
        return mSummaryOff;
    }

    /**
     * Returns whether dependents are disabled when this preference is on ({@code true})
     * or when this preference is off ({@code false}).
     *
     * @return Whether dependents are disabled when this preference is on ({@code true})
     * or when this preference is off ({@code false}).
     */
    public boolean getDisableDependentsState() {
        return mDisableDependentsState;
    }

    /**
     * Sets whether dependents are disabled when this preference is on ({@code true})
     * or when this preference is off ({@code false}).
     *
     * @param disableDependentsState The preference state that should disable dependents.
     */
    public void setDisableDependentsState(boolean disableDependentsState) {
        mDisableDependentsState = disableDependentsState;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(restoreValue ? getPersistedBoolean(mChecked)
                : (Boolean) defaultValue);
    }

    /**
     * Sync a summary holder contained within holder's subhierarchy with the correct summary text.
     *
     * @param holder PreferenceViewHolder which holds a reference to the summary view
     */
    protected void syncSummaryView(PreferenceViewHolder holder) {
        // Sync the summary holder
        View view = holder.findViewById(android.R.id.summary);
        syncSummaryView(view);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    protected void syncSummaryView(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        TextView summaryView = (TextView) view;
        boolean useDefaultSummary = true;
        if (mChecked && !TextUtils.isEmpty(mSummaryOn)) {
            summaryView.setText(mSummaryOn);
            useDefaultSummary = false;
        } else if (!mChecked && !TextUtils.isEmpty(mSummaryOff)) {
            summaryView.setText(mSummaryOff);
            useDefaultSummary = false;
        }
        if (useDefaultSummary) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                useDefaultSummary = false;
            }
        }
        int newVisibility = View.GONE;
        if (!useDefaultSummary) {
            // Someone has written to it
            newVisibility = View.VISIBLE;
        }
        if (newVisibility != summaryView.getVisibility()) {
            summaryView.setVisibility(newVisibility);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState myState = new cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState(superState);
        myState.checked = isChecked();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState myState = (cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setChecked(myState.checked);
    }

    static class SavedState extends BaseSavedState {
        boolean checked;

        public SavedState(Parcel source) {
            super(source);
            checked = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(checked ? 1 : 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState> CREATOR =
                new Parcelable.Creator<cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState>() {
                    @Override
                    public cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState createFromParcel(Parcel in) {
                        return new cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState(in);
                    }

                    @Override
                    public cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState[] newArray(int size) {
                        return new cc.zsakvo.yueduhchelper.preference.TwoStatePreference.SavedState[size];
                    }
                };
    }

}
