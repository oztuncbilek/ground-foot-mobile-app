package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.security.auth.login.LoginException;

public class login1 extends AppCompatActivity {
    EditText email2,pass2;
    Button login2;
    TextView regtext;
    FirebaseAuth mAuth;
    ProgressBar progbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_login1);
        email2=findViewById(R.id.Email2);
        pass2=findViewById(R.id.pass2);;
        login2=findViewById(R.id.btnlogin);;
        regtext=findViewById(R.id.text_reg);
        mAuth=FirebaseAuth.getInstance();
        progbar=findViewById(R.id.progressBarlogin);

        String str="Don't have an account? <font color='#b30000'>REGISTER</font>";
        regtext.setText(Html.fromHtml(str));

        login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lemail=email2.getText().toString().trim();
                String lpass=pass2.getText().toString().trim();
                if(TextUtils.isEmpty(lemail)){
                    email2.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(lpass)){
                    pass2.setError("Password is Required.");
                    return;
                }
                if(lpass.length()< 6){
                    pass2.setError("Password must be >= 6 Characters.");
                    return;
                }
                progbar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(lemail,lpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(login1.this,"Logged In Successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            startActivity(new Intent(getApplicationContext(),Introduction.class));


                        }else{
                            Toast.makeText(login1.this,"Error !"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progbar.setVisibility(View.GONE);
                        }
                    }

                });
            }
        });
        regtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register1.class));
            }
        });
    }
}