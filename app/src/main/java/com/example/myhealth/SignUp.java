package com.example.myhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase rtdb;
    private EditText etFullName, etEmail, etPassword, etCPassword;
    private View btn_signUp, progressSignup;
    private TextView tv_login, textSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        auth= FirebaseAuth.getInstance();
        rtdb=FirebaseDatabase.getInstance(
                "https://myhealth-b7de0-default-rtdb.firebaseio.com/"
        );

        tv_login.setOnClickListener(v->{
            Intent i = new Intent(SignUp.this, Login.class);
            startActivity(i);
            finish();
        });

        btn_signUp.setOnClickListener(v->{
            String name=etFullName.getText().toString().trim();
            String email=etEmail.getText().toString().trim();
            String password= etPassword.getText().toString().trim();
            String cpassword= etCPassword.getText().toString().trim();

            if(name.isEmpty())
            {
                showAlert(R.string.validation_title, "Name can not be empty");
            }else if(email.isEmpty())
            {
                showAlert(R.string.validation_title, "Email can not be empty");
            }else if(password.isEmpty())
            {
                showAlert(R.string.validation_title, "Password can not be empty");
            }else if(cpassword.isEmpty())
            {
                showAlert(R.string.validation_title, "Confirm Password can not be empty");
            }else if(!password.equals(cpassword)){
                showAlert(R.string.validation_title, "Password and Confirm Password should be same");
            }else
            {
                setLoading(true);
                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(result->{
                    String uid=result.getUser().getUid();
                    User user= new User(uid,name,email);

                    rtdb.getReference("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                saveUserSession(uid, email, name);
                                openHomePage();
                            })
                            .addOnFailureListener(e -> {
                                setLoading(false);
                                showAlert(R.string.error_title, "DB Error: " + e.getMessage());
                            });

                }).addOnFailureListener(e -> {
                    setLoading(false);
                    showAlert(R.string.error_title, "Signup Failed: " + e.getMessage());
                });
            }

        });
    }

    private void init()
    {
        tv_login= findViewById(R.id.tv_login);
        etFullName=findViewById(R.id.etFullName);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        etCPassword=findViewById(R.id.etCPassword);
        btn_signUp=findViewById(R.id.btn_signup);
        textSignupButton=findViewById(R.id.text_signup_button);
        progressSignup=findViewById(R.id.progress_signup);
    }

    private void saveUserSession(String uid, String email, String name) {
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        sp.edit()
                .putBoolean("logged_in", true)
                .putString("user_uid", uid)
                .putString("user_email", email)
                .putString("user_name", name)
                .apply();
    }

    private void openHomePage() {
        Intent intent = new Intent(SignUp.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        btn_signUp.setEnabled(!loading);
        tv_login.setEnabled(!loading);
        etFullName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        etCPassword.setEnabled(!loading);
        textSignupButton.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressSignup.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showAlert(int titleResId, String message) {
        new AlertDialog.Builder(this)
                .setTitle(titleResId)
                .setMessage(message != null ? message : getString(R.string.error_title))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
