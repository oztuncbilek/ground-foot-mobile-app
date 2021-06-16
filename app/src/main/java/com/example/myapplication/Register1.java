package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register1 extends AppCompatActivity {
    Database myDb;

    private static final String TAG = "Register1";
    EditText name,email1,pass1,phone1;
    String userID1;
    Button reg;
    TextView Loginbtn;
    ProgressBar progbar2;
    private FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDb= new Database(this);

        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_register1);
        name=findViewById(R.id.Name);
        email1=findViewById(R.id.Email1);;
        pass1=findViewById(R.id.Pass1);
        reg=findViewById(R.id.Register);;
        Loginbtn=findViewById(R.id.loginbtn);
        progbar2=findViewById(R.id.progressBarreg);
        phone1=findViewById(R.id.Phone);
        mAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        TextView loginbtn=(TextView)findViewById(R.id.loginbtn);
        String str="Already a member? <font color='#b30000'>LOGIN</font>";
        loginbtn.setText(Html.fromHtml(str));


        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // data storage
                String remail=email1.getText().toString().trim();
                String rpass=pass1.getText().toString().trim();
                String rname=name.getText().toString().trim();
                String rphone=phone1.getText().toString().trim();

                myDb.insertData(name.getText().toString().trim(),email1.getText().toString().trim(),phone1.getText().toString().trim());

                if(TextUtils.isEmpty(remail)){
                    email1.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(rpass)){
                    pass1.setError("Password is Required.");
                    return;
                }
                if(rpass.length()< 6){
                    pass1.setError("Password must be >= 6 Characters");
                    return;
                }
                if(TextUtils.isEmpty(rphone)){
                    phone1.setError("Phone number is Required.");
                    return;
                }
                progbar2.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(remail,rpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register1.this,"User Created",Toast.LENGTH_SHORT).show();
                            userID1 = mAuth.getCurrentUser().getUid();
                             String userEmail= (String) mAuth.getCurrentUser().getEmail();
                            DocumentReference documentReference = fstore.collection("database").document(userID1);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",rname);
                            user.put("Phone Number",rphone);
                            user.put("Email",userEmail);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   Log.d(TAG,"User Profile is created !" + userID1);

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }
                        else{
                            Toast.makeText(Register1.this,"Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progbar2.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });
        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),login1.class));
            }
        });
    }
}