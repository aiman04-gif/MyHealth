package com.example.myhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase rtdb;
    private EditText etFullName, etEmail, etPassword, etCPassword;
    private View btn_signUp;
    private TextView tv_login;
    private Button btnGoToDoctorSignUp;

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
                etFullName.setError("Name can not be empty");
            }else if(email.isEmpty())
            {
                etEmail.setError("Email can not be empty");
            }else if(password.isEmpty())
            {
                etPassword.setError("Password can not be empty");
            }else if(cpassword.isEmpty())
            {
                etCPassword.setError("Password can not be empty");
            }else if(!password.equals(cpassword)){
                Toast.makeText(this,"Password and Confirm Password should be same",Toast.LENGTH_SHORT).show();
            }else
            {
                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(result->{
                    String uid=result.getUser().getUid();
                    User user= new User(uid,name,email);

                    rtdb.getReference("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, Login.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );

                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Signup Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

        });

        btnGoToDoctorSignUp.setOnClickListener(v->{
            startActivity(new Intent(SignUp.this, DoctorSignUp.class));
            finish();
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
        btnGoToDoctorSignUp=findViewById(R.id.btnGoToDoctorSignUp);
    }
}