package com.example.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private TextView tv_signup;
    private View btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        auth= FirebaseAuth.getInstance();

        btn_login.setOnClickListener(v->{
            String email=etEmail.getText().toString().trim();
            String password=etPassword.getText().toString().trim();

            if(email.isEmpty()){
                etEmail.setError("Enter Email");
            }else if(password.isEmpty())
            {
                etPassword.setError("Enter Password");
            }else{
                auth.signInWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Login Unsuccessful"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        tv_signup.setOnClickListener(v->{
            Intent i = new Intent(Login.this, SignUp.class);
            startActivity(i);
        });
    }

    private void init()
    {
        tv_signup= findViewById(R.id.tv_signup);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btn_login= findViewById(R.id.btn_login);
    }
}