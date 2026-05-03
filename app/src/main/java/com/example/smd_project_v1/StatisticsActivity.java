package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class StatisticsActivity extends AppCompatActivity {

    private static final String[] WEEK_DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final int[] HEART_RATE_DATA = {72, 75, 80, 78, 74, 76, 73};
    private static final int[] BLOOD_PRESSURE_DATA = {120, 118, 122, 119, 121, 117, 120};
    private static final int[] OXYGEN_DATA = {98, 97, 99, 98, 96, 98, 97};

    private TextView textHeartRateValue;
    private TextView textBloodPressureValue;
    private TextView textOxygenLevelValue;
    private TextView textWeeklyStatTitle;
    private TextView textWeeklyAverage;
    private LinearLayout layoutWeeklyBars;
    private MaterialButton heartRateTabButton;
    private MaterialButton bloodPressureTabButton;
    private MaterialButton oxygenTabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        View root = findViewById(R.id.root_statistics);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        LinearLayout heartRateCard = findViewById(R.id.card_heart_rate);
        LinearLayout bloodPressureCard = findViewById(R.id.card_blood_pressure);
        LinearLayout oxygenLevelCard = findViewById(R.id.card_oxygen_level);

        textHeartRateValue = findViewById(R.id.text_heart_rate_value);
        textBloodPressureValue = findViewById(R.id.text_blood_pressure_value);
        textOxygenLevelValue = findViewById(R.id.text_oxygen_level_value);
        textWeeklyStatTitle = findViewById(R.id.text_weekly_stat_title);
        textWeeklyAverage = findViewById(R.id.text_weekly_average);
        layoutWeeklyBars = findViewById(R.id.layout_weekly_bars);
        heartRateTabButton = findViewById(R.id.button_tab_heart_rate);
        bloodPressureTabButton = findViewById(R.id.button_tab_blood_pressure);
        oxygenTabButton = findViewById(R.id.button_tab_oxygen);

        heartRateCard.setOnClickListener(v ->
                showEditDialog(
                        getString(R.string.edit_heart_rate_title),
                        textHeartRateValue,
                        InputType.TYPE_CLASS_NUMBER,
                        ValueType.NUMBER_ONLY
                )
        );

        bloodPressureCard.setOnClickListener(v ->
                showEditDialog(
                        getString(R.string.edit_blood_pressure_title),
                        textBloodPressureValue,
                        InputType.TYPE_CLASS_TEXT,
                        ValueType.BLOOD_PRESSURE
                )
        );

        oxygenLevelCard.setOnClickListener(v ->
                showEditDialog(
                        getString(R.string.edit_oxygen_level_title),
                        textOxygenLevelValue,
                        InputType.TYPE_CLASS_NUMBER,
                        ValueType.NUMBER_ONLY
                )
        );

        heartRateTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.HEART_RATE));
        bloodPressureTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.BLOOD_PRESSURE));
        oxygenTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.OXYGEN));
        updateWeeklyGraph(StatType.HEART_RATE);
    }

    private void updateWeeklyGraph(StatType statType) {
        textWeeklyStatTitle.setText(statType.title);
        textWeeklyAverage.setText("Avg " + getAverage(statType.values) + statType.averageSuffix);
        updateTabStyles(statType);
        renderBars(statType.values);
    }

    private void updateTabStyles(StatType selectedType) {
        styleTab(heartRateTabButton, selectedType == StatType.HEART_RATE);
        styleTab(bloodPressureTabButton, selectedType == StatType.BLOOD_PRESSURE);
        styleTab(oxygenTabButton, selectedType == StatType.OXYGEN);
    }

    private void styleTab(MaterialButton button, boolean selected) {
        int themePrimary = ContextCompat.getColor(this, R.color.theme_primary);
        int white = ContextCompat.getColor(this, R.color.white);
        int textPrimary = ContextCompat.getColor(this, R.color.text_primary);

        button.setBackgroundTintList(ColorStateList.valueOf(selected ? themePrimary : white));
        button.setTextColor(selected ? white : textPrimary);
        button.setStrokeColor(ColorStateList.valueOf(themePrimary));
        button.setStrokeWidth(selected ? 0 : dp(1));
    }

    private void renderBars(int[] values) {
        layoutWeeklyBars.removeAllViews();

        int minValue = getMin(values);
        int maxValue = getMax(values);
        int valueRange = Math.max(1, maxValue - minValue);
        int barAreaHeight = dp(124);
        int minBarHeight = dp(18);

        for (int i = 0; i < values.length; i++) {
            LinearLayout column = new LinearLayout(this);
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            column.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            column.setLayoutParams(columnParams);

            TextView valueText = new TextView(this);
            valueText.setText(String.valueOf(values[i]));
            valueText.setGravity(Gravity.CENTER);
            valueText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            valueText.setTextSize(11);

            FrameLayout barFrame = new FrameLayout(this);
            LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, barAreaHeight);
            frameParams.topMargin = dp(4);
            barFrame.setLayoutParams(frameParams);

            View bar = new View(this);
            float normalizedValue = (values[i] - minValue) / (float) valueRange;
            int barHeight = minBarHeight + Math.round(normalizedValue * (barAreaHeight - minBarHeight));
            FrameLayout.LayoutParams barParams = new FrameLayout.LayoutParams(dp(18), barHeight);
            barParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(ContextCompat.getColor(this, R.color.theme_primary));
            barFrame.addView(bar);

            TextView dayText = new TextView(this);
            dayText.setText(WEEK_DAYS[i]);
            dayText.setGravity(Gravity.CENTER);
            dayText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            dayText.setTextSize(12);

            column.addView(valueText);
            column.addView(barFrame);
            column.addView(dayText);
            layoutWeeklyBars.addView(column);
        }
    }

    private int getAverage(int[] values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return Math.round(total / (float) values.length);
    }

    private int getMax(int[] values) {
        int max = values[0];
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int getMin(int[] values) {
        int min = values[0];
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void showEditDialog(String title, TextView targetValue, int inputType, ValueType valueType) {
        EditText input = new EditText(this);
        input.setInputType(inputType);
        input.setSingleLine(true);
        input.setText(targetValue.getText().toString().trim());
        input.setSelection(input.getText().length());
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (isValidInput(value, valueType)) {
                targetValue.setText(value);
                dialog.dismiss();
            } else {
                Toast.makeText(this, getErrorMessage(valueType), Toast.LENGTH_SHORT).show();
            }
        }));

        dialog.show();
    }

    private boolean isValidInput(String value, ValueType valueType) {
        if (value.isEmpty()) {
            return false;
        }

        if (valueType == ValueType.BLOOD_PRESSURE) {
            return value.matches("^\\d{2,3}/\\d{2,3}$");
        }

        return value.matches("^\\d{1,3}$");
    }

    private int getErrorMessage(ValueType valueType) {
        if (valueType == ValueType.BLOOD_PRESSURE) {
            return R.string.invalid_blood_pressure;
        }
        return R.string.invalid_numeric_value;
    }

    private enum ValueType {
        NUMBER_ONLY,
        BLOOD_PRESSURE
    }

    private enum StatType {
        HEART_RATE("Heart Rate", HEART_RATE_DATA, " BPM"),
        BLOOD_PRESSURE("Blood Pressure", BLOOD_PRESSURE_DATA, " mmHg"),
        OXYGEN("Oxygen", OXYGEN_DATA, "%");

        private final String title;
        private final int[] values;
        private final String averageSuffix;

        StatType(String title, int[] values, String averageSuffix) {
            this.title = title;
            this.values = values;
            this.averageSuffix = averageSuffix;
        }
    }
}
