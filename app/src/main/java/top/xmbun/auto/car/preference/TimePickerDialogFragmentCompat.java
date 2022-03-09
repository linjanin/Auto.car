package top.xmbun.auto.car.preference;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import top.xmbun.auto.car.R;

/**
 * 时间选择器配置项显示碎片.
 *
 * @author bun
 * @since 2022-03-08
 */
public class TimePickerDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    public static final String TAG = "TimePickerDialogFragmentCompat";

    private static final String SAVE_STATE_INTERVAL = "TimePickerDialogFragmentCompat.interval";

    /**
     * 选择的时间值.
     */
    private int interval;

    /**
     * 时选择器.
     */
    private NumberPicker hourPicker;

    /**
     * 分选择器.
     */
    private NumberPicker minutePicker;

    /**
     * 秒选择器.
     */
    private NumberPicker secondPicker;

    public static TimePickerDialogFragmentCompat newInstance(String key) {
        final TimePickerDialogFragmentCompat f = new TimePickerDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            interval = getTimePickerPreference().getInterval();
        } else {
            interval = savedInstanceState.getInt(SAVE_STATE_INTERVAL);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_INTERVAL, getCurrentInterval());
    }

    @Override
    protected View onCreateDialogView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.time_picker_preference, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final int hour = interval / 3600;
        final int minute = interval % 3600 / 60;
        final int second = interval % 3600 % 60;

        final TimePickerPreference preference = getTimePickerPreference();

        hourPicker = view.findViewById(R.id.hour_picker);
        init(hourPicker, preference.getHourAttribute(), hour);

        minutePicker = view.findViewById(R.id.minute_picker);
        init(minutePicker, preference.getMinuteAttribute(), minute);

        secondPicker = view.findViewById(R.id.second_picker);
        init(secondPicker, preference.getSecondAttribute(), second);
    }

    private void init(NumberPicker picker, TimePickerPreference.PickerAttribute attribute, int initValue) {
        picker.setMinValue(attribute.getMinValue());
        picker.setMaxValue(attribute.getMaxValue());
        picker.setValue(initValue);
        picker.setDisplayedValues(obtainDisplayedValues(initValue, attribute));
        picker.setWrapSelectorWheel(attribute.isWrapSelectorWheel());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setOnValueChangedListener((p, oldVal, newVal) -> {
            p.setDisplayedValues(obtainDisplayedValues(newVal, attribute));
        });
        if (!attribute.isEnabled()) {
            picker.setValue(0);
            picker.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // NOTE: this clearFocus() triggers validateInputTextView() internally
            if (hourPicker != null) {
                hourPicker.clearFocus();
            }
            if (minutePicker != null) {
                minutePicker.clearFocus();
            }
            if (secondPicker != null) {
                secondPicker.clearFocus();
            }
            interval = getCurrentInterval();

            final TimePickerPreference preference = getTimePickerPreference();
            if (preference.callChangeListener(interval)) {
                preference.setInterval(interval);
            }
        }
    }

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }

    private int getCurrentInterval() {
        final int second = secondPicker != null ? secondPicker.getValue() : 0;
        final int minute = minutePicker != null ? minutePicker.getValue() : 0;
        final int hour = hourPicker != null ? hourPicker.getValue() : 0;
        return hour * 3600 + minute * 60 + second;
    }

    private static String[] obtainDisplayedValues(int value, TimePickerPreference.PickerAttribute attribute) {
        return obtainDisplayedValues(value, attribute.getMinValue(), attribute.getMaxValue(), attribute.getUnitText());
    }

    private static String[] obtainDisplayedValues(int value, int min, int max, CharSequence unit) {
        final int n = max - min + 1;
        final String[] values = new String[n];
        for (int v = min; v <= max; ++v) {
            final int i = v - min;
            values[i] = String.valueOf(v);
            if (v == 0) {
                values[i] = "00";
            }
            if (value == v) {
                values[i] = v + " " + unit;
            }
        }
        return values;
    }
}