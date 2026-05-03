package com.example.myhealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private static final String[] WEEK_DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private final int[] heartRateData = new int[7];
    private final int[] bloodPressureData = new int[7];
    private final int[] oxygenData = new int[7];

    private TextView textHeartRateValue;
    private TextView textBloodPressureValue;
    private TextView textOxygenLevelValue;
    private TextView textWeeklyStatTitle;
    private TextView textWeeklyAverage;
    private LinearLayout layoutWeeklyBars;
    private MaterialButton heartRateTabButton;
    private MaterialButton bloodPressureTabButton;
    private MaterialButton oxygenTabButton;
    private MaterialButton addStatsButton;
    private StatType selectedStatType = StatType.HEART_RATE;

    public StatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        LinearLayout heartRateCard = view.findViewById(R.id.card_heart_rate);
        LinearLayout bloodPressureCard = view.findViewById(R.id.card_blood_pressure);
        LinearLayout oxygenLevelCard = view.findViewById(R.id.card_oxygen_level);

        textHeartRateValue = view.findViewById(R.id.text_heart_rate_value);
        textBloodPressureValue = view.findViewById(R.id.text_blood_pressure_value);
        textOxygenLevelValue = view.findViewById(R.id.text_oxygen_level_value);
        textWeeklyStatTitle = view.findViewById(R.id.text_weekly_stat_title);
        textWeeklyAverage = view.findViewById(R.id.text_weekly_average);
        layoutWeeklyBars = view.findViewById(R.id.layout_weekly_bars);
        heartRateTabButton = view.findViewById(R.id.button_tab_heart_rate);
        bloodPressureTabButton = view.findViewById(R.id.button_tab_blood_pressure);
        oxygenTabButton = view.findViewById(R.id.button_tab_oxygen);
        addStatsButton = view.findViewById(R.id.button_add_stats);

        View.OnClickListener addStatsClick = v -> showAddStatsDialog();
        addStatsButton.setOnClickListener(addStatsClick);
        heartRateCard.setOnClickListener(addStatsClick);
        bloodPressureCard.setOnClickListener(addStatsClick);
        oxygenLevelCard.setOnClickListener(addStatsClick);

        heartRateTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.HEART_RATE));
        bloodPressureTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.BLOOD_PRESSURE));
        oxygenTabButton.setOnClickListener(v -> updateWeeklyGraph(StatType.OXYGEN));

        showEmptyStats();
        listenForFirebaseStats();
        return view;
    }

    private void listenForFirebaseStats() {
        String patientUid = getPatientUid();
        if (patientUid == null || patientUid.trim().isEmpty()) {
            showEmptyStats();
            return;
        }

        FirebaseDatabase.getInstance().getReference("health_stats")
                .child(patientUid)
                .orderByChild("createdAt")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HealthStat> stats = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            HealthStat stat = child.getValue(HealthStat.class);
                            if (stat != null) {
                                stats.add(stat);
                            }
                        }
                        applyStats(stats);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Unable to load stats", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyStats(List<HealthStat> stats) {
        clearWeeklyData();
        if (stats.isEmpty()) {
            showEmptyStats();
            return;
        }

        Collections.sort(stats, Comparator.comparingLong(HealthStat::getCreatedAt));
        HealthStat latest = stats.get(stats.size() - 1);
        textHeartRateValue.setText(String.valueOf(latest.getHeartRate()));
        textBloodPressureValue.setText(latest.getBloodPressureSystolic() + "/" + latest.getBloodPressureDiastolic());
        textOxygenLevelValue.setText(String.valueOf(latest.getOxygenLevel()));

        for (HealthStat stat : stats) {
            int dayIndex = getDayIndex(stat.getDayLabel());
            if (dayIndex >= 0) {
                heartRateData[dayIndex] = stat.getHeartRate();
                bloodPressureData[dayIndex] = stat.getBloodPressureSystolic();
                oxygenData[dayIndex] = stat.getOxygenLevel();
            }
        }
        updateWeeklyGraph(selectedStatType);
    }

    private void showEmptyStats() {
        clearWeeklyData();
        textHeartRateValue.setText("No data");
        textBloodPressureValue.setText("No data");
        textOxygenLevelValue.setText("No data");
        updateWeeklyGraph(selectedStatType);
    }

    private void clearWeeklyData() {
        for (int i = 0; i < WEEK_DAYS.length; i++) {
            heartRateData[i] = 0;
            bloodPressureData[i] = 0;
            oxygenData[i] = 0;
        }
    }

    private void showAddStatsDialog() {
        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16);
        form.setPadding(padding, padding, padding, padding);

        EditText heartRateInput = createNumberInput("Heart Rate");
        EditText bpSystolicInput = createNumberInput("Blood Pressure Systolic");
        EditText bpDiastolicInput = createNumberInput("Blood Pressure Diastolic");
        EditText oxygenInput = createNumberInput("Oxygen Level");

        form.addView(heartRateInput);
        form.addView(bpSystolicInput);
        form.addView(bpDiastolicInput);
        form.addView(oxygenInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Stats")
                .setView(form)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Integer heartRate = parseRequiredNumber(heartRateInput);
            Integer bpSystolic = parseRequiredNumber(bpSystolicInput);
            Integer bpDiastolic = parseRequiredNumber(bpDiastolicInput);
            Integer oxygen = parseRequiredNumber(oxygenInput);

            if (heartRate == null || bpSystolic == null || bpDiastolic == null || oxygen == null) {
                return;
            }

            saveStats(heartRate, bpSystolic, bpDiastolic, oxygen, dialog);
        }));

        dialog.show();
    }

    private EditText createNumberInput(String hint) {
        EditText input = new EditText(requireContext());
        input.setHint(hint);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setSingleLine(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(8));
        input.setLayoutParams(params);
        return input;
    }

    private Integer parseRequiredNumber(EditText input) {
        String value = input.getText().toString().trim();
        if (value.isEmpty() || !value.matches("^\\d{1,3}$")) {
            input.setError("Required");
            return null;
        }
        return Integer.parseInt(value);
    }

    private void saveStats(int heartRate, int bpSystolic, int bpDiastolic, int oxygen, AlertDialog dialog) {
        String patientUid = getPatientUid();
        if (patientUid == null || patientUid.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please login again to save stats", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("health_stats")
                .child(patientUid)
                .push();
        String statId = ref.getKey();
        long now = System.currentTimeMillis();
        HealthStat stat = new HealthStat(
                statId,
                patientUid,
                heartRate,
                bpSystolic,
                bpDiastolic,
                oxygen,
                new SimpleDateFormat("EEE", Locale.US).format(new Date(now)),
                now
        );

        ref.setValue(stat)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Stats saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> Toast.makeText(
                        requireContext(),
                        error.getMessage() != null ? error.getMessage() : "Unable to save stats",
                        Toast.LENGTH_SHORT
                ).show());
    }

    private String getPatientUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sp.getString("user_uid", "");
    }

    private void updateWeeklyGraph(StatType statType) {
        selectedStatType = statType;
        int[] values = getValues(statType);
        textWeeklyStatTitle.setText(statType.title);
        textWeeklyAverage.setText("Avg " + getAverage(values) + statType.averageSuffix);
        updateTabStyles(statType);
        renderBars(values);
    }

    private int[] getValues(StatType statType) {
        if (statType == StatType.BLOOD_PRESSURE) {
            return bloodPressureData;
        }
        if (statType == StatType.OXYGEN) {
            return oxygenData;
        }
        return heartRateData;
    }

    private void updateTabStyles(StatType selectedType) {
        styleTab(heartRateTabButton, selectedType == StatType.HEART_RATE);
        styleTab(bloodPressureTabButton, selectedType == StatType.BLOOD_PRESSURE);
        styleTab(oxygenTabButton, selectedType == StatType.OXYGEN);
    }

    private void styleTab(MaterialButton button, boolean selected) {
        int themePrimary = ContextCompat.getColor(requireContext(), R.color.theme_primary);
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int black = ContextCompat.getColor(requireContext(), R.color.black);

        button.setBackgroundTintList(ColorStateList.valueOf(selected ? themePrimary : white));
        button.setTextColor(selected ? white : black);
        button.setStrokeColor(ColorStateList.valueOf(themePrimary));
        button.setStrokeWidth(selected ? 0 : dp(1));
    }

    private void renderBars(int[] values) {
        layoutWeeklyBars.removeAllViews();

        int maxValue = Math.max(1, getMax(values));
        int barAreaHeight = dp(124);
        int minBarHeight = dp(6);

        for (int i = 0; i < values.length; i++) {
            LinearLayout column = new LinearLayout(requireContext());
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            TextView valueText = new TextView(requireContext());
            valueText.setText(String.valueOf(values[i]));
            valueText.setGravity(Gravity.CENTER);
            valueText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            valueText.setTextSize(11);

            FrameLayout barFrame = new FrameLayout(requireContext());
            LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, barAreaHeight);
            frameParams.topMargin = dp(4);
            barFrame.setLayoutParams(frameParams);

            View bar = new View(requireContext());
            int barHeight = values[i] == 0 ? minBarHeight : minBarHeight + Math.round((values[i] / (float) maxValue) * (barAreaHeight - minBarHeight));
            FrameLayout.LayoutParams barParams = new FrameLayout.LayoutParams(dp(18), barHeight);
            barParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.theme_primary));
            barFrame.addView(bar);

            TextView dayText = new TextView(requireContext());
            dayText.setText(WEEK_DAYS[i]);
            dayText.setGravity(Gravity.CENTER);
            dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            dayText.setTextSize(12);

            column.addView(valueText);
            column.addView(barFrame);
            column.addView(dayText);
            layoutWeeklyBars.addView(column);
        }
    }

    private int getAverage(int[] values) {
        int total = 0;
        int count = 0;
        for (int value : values) {
            if (value > 0) {
                total += value;
                count++;
            }
        }
        return count == 0 ? 0 : Math.round(total / (float) count);
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

    private int getDayIndex(String dayLabel) {
        for (int i = 0; i < WEEK_DAYS.length; i++) {
            if (WEEK_DAYS[i].equalsIgnoreCase(dayLabel)) {
                return i;
            }
        }
        return -1;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private enum StatType {
        HEART_RATE("Heart Rate", " BPM"),
        BLOOD_PRESSURE("Blood Pressure", " mmHg"),
        OXYGEN("Oxygen", "%");

        private final String title;
        private final String averageSuffix;

        StatType(String title, String averageSuffix) {
            this.title = title;
            this.averageSuffix = averageSuffix;
        }
    }
}
