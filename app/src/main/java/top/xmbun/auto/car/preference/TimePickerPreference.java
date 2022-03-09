package top.xmbun.auto.car.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import java.util.Locale;

import top.xmbun.auto.car.R;

/**
 * 时间选择器配置项.
 *
 * @author bun
 * @since 2022-03-08
 */
public class TimePickerPreference extends DialogPreference {
    /**
     * 选择的时间值.
     */
    private int interval;

    /**
     * 秒选择器属性.
     */
    private final PickerAttribute second = new PickerAttribute(0, 0, 60, "秒");

    /**
     * 分选择器属性.
     */
    private final PickerAttribute minute = new PickerAttribute(0, 0, 60, "分");

    /**
     * 时选择器属性.
     */
    private final PickerAttribute hour = new PickerAttribute(0, 0, 24, "时");

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TimePickerPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreferenceCompat, defStyleAttr, defStyleRes);

        second.enabled = t.getBoolean(R.styleable.TimePickerPreferenceCompat_secondEnabled, true);
        second.minValue = t.getInt(R.styleable.TimePickerPreferenceCompat_secondMinValue, second.minValue);
        second.maxValue = t.getInt(R.styleable.TimePickerPreferenceCompat_secondMaxValue, second.maxValue);
        if (t.hasValue(R.styleable.TimePickerPreferenceCompat_secondUnitText)) {
            second.unitText = t.getString(R.styleable.TimePickerPreferenceCompat_secondUnitText);
        }

        minute.enabled = t.getBoolean(R.styleable.TimePickerPreferenceCompat_minuteEnabled, true);
        minute.minValue = t.getInt(R.styleable.TimePickerPreferenceCompat_minuteMinValue, minute.minValue);
        minute.maxValue = t.getInt(R.styleable.TimePickerPreferenceCompat_minuteMaxValue, minute.maxValue);
        if (t.hasValue(R.styleable.TimePickerPreferenceCompat_minuteUnitText)) {
            minute.unitText = t.getString(R.styleable.TimePickerPreferenceCompat_minuteUnitText);
        }

        hour.enabled = t.getBoolean(R.styleable.TimePickerPreferenceCompat_hourEnabled, true);
        hour.minValue = t.getInt(R.styleable.TimePickerPreferenceCompat_hourMinValue, hour.minValue);
        hour.maxValue = t.getInt(R.styleable.TimePickerPreferenceCompat_hourMaxValue, hour.maxValue);
        if (t.hasValue(R.styleable.TimePickerPreferenceCompat_hourUnitText)) {
            hour.unitText = t.getString(R.styleable.TimePickerPreferenceCompat_hourUnitText);
        }

        final boolean wrapSelectorWheel = t.getBoolean(R.styleable.TimePickerPreferenceCompat_wrapSelectorWheel, true);
        second.wrapSelectorWheel = wrapSelectorWheel;
        minute.wrapSelectorWheel = wrapSelectorWheel;
        hour.wrapSelectorWheel = wrapSelectorWheel;

        t.recycle();

        setDialogLayoutResource(R.layout.time_picker_preference);
    }

    /**
     * 设置时间值.
     *
     * @param interval 时间值
     */
    public void setInterval(int interval) {
        final boolean wasBlocking = shouldDisableDependents();

        // 缓存时间值
        this.interval = interval;
        this.hour.value = interval / 3600;
        this.minute.value = interval % 3600 / 60;
        this.second.value = interval % 3600 % 60;

        // 持久化属性值
        persistInt(interval);

        // 通知依赖更新
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }

        // 显示属性值
        showSummary();
        notifyChanged();
    }

    public int getInterval() {
        return interval;
    }

    public PickerAttribute getSecondAttribute() {
        return second;
    }

    public PickerAttribute getMinuteAttribute() {
        return minute;
    }

    public PickerAttribute getHourAttribute() {
        return hour;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0;
        }
        setInterval(getPersistedInt((Integer) defaultValue));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = getInterval();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setInterval(myState.value);
    }

    /**
     * 显示属性值.
     */
    private void showSummary() {
        setSummary(String.format(Locale.getDefault(), "%d %s", interval, second.unitText));
    }

    /**
     * 选择器属性.
     */
    public static class PickerAttribute {
        /**
         * 初始值.
         */
        private int value;

        /**
         * 最小值.
         */
        private int minValue;

        /**
         * 最大值.
         */
        private int maxValue;

        /**
         * 单位.
         */
        private CharSequence unitText;

        /**
         * 是否循环可选值.
         */
        private boolean wrapSelectorWheel = true;

        /**
         * 是否启用.
         */
        private boolean enabled = true;

        public PickerAttribute() {
        }

        public PickerAttribute(int value, int minValue, int maxValue, CharSequence unitText) {
            this.value = value;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.unitText = unitText;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getMinValue() {
            return minValue;
        }

        public void setMinValue(int minValue) {
            this.minValue = minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        public CharSequence getUnitText() {
            return unitText;
        }

        public void setUnitText(CharSequence unitText) {
            this.unitText = unitText;
        }

        public boolean isWrapSelectorWheel() {
            return wrapSelectorWheel;
        }

        public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
            this.wrapSelectorWheel = wrapSelectorWheel;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 状态保存对象.
     */
    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }
    }
}