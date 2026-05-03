package com.example.myhealth;

import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private View outer_ring;
    private TextView tvno1ring;
    private View pill_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        Animation pulse= AnimationUtils.loadAnimation(this, R.anim.pulse);
        outer_ring.startAnimation(pulse);
        tvno1ring.startAnimation(pulse);

        pill_btn.setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        });


    }
    private void init()
    {
        outer_ring=findViewById(R.id.outer_ring);
        tvno1ring=findViewById(R.id.no_1_text);
        pill_btn=findViewById(R.id.btn_pill);
    }

}