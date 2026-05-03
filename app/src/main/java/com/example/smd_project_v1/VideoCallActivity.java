package com.example.smd_project_v1;

import com.example.myhealth.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class VideoCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_call);

        View root = findViewById(R.id.root_video_call);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backButton = findViewById(R.id.button_back);
        MaterialButton muteButton = findViewById(R.id.button_mute);
        MaterialButton cameraButton = findViewById(R.id.button_camera);
        MaterialButton endCallButton = findViewById(R.id.button_end_call);
        TextView status = findViewById(R.id.text_call_status);

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        muteButton.setOnClickListener(v -> {
            status.setText(R.string.call_status_muted);
            Toast.makeText(this, R.string.mute_demo_toast, Toast.LENGTH_SHORT).show();
        });
        cameraButton.setOnClickListener(v -> {
            status.setText(R.string.call_status_camera_off);
            Toast.makeText(this, R.string.camera_demo_toast, Toast.LENGTH_SHORT).show();
        });
        endCallButton.setOnClickListener(v -> finish());
    }
}
