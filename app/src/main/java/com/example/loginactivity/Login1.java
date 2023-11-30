package com.example.loginactivity;

import static com.example.loginactivity.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login1 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    CheckBox stayco;
    Switch swi;
    EditText eTname, eTphone, eTemail, eTpass;
    Button bt1;
    String email, password, uid, name, phone;
    User userdb;
    boolean stayCon, registerd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        swi = findViewById(R.id.switch1);
        eTname = findViewById(R.id.fname);
        eTemail = findViewById(R.id.email1);
        eTpass = findViewById(R.id.password);
        eTphone = findViewById(R.id.phone);
        bt1 = findViewById(R.id.button1);
        stayco = findViewById(R.id.checkBox);
        stayCon = false;
        mAuth = FirebaseAuth.getInstance();
        registerd = true;
        logreg();// check if login or register



    }
    // shared preferences
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        Intent si = new Intent(Login1.this, MainActivity.class);
        if (isChecked && stayCon && mAuth.getCurrentUser() != null) {
            si.putExtra("newuser", false);
            startActivity(si);
        }
    }
    protected void onPause() {
        super.onPause();
        if (stayCon) finish();
    }

    public void logreg(){
        swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putBoolean("stayConnect",stayco.isChecked());
                    editor.commit();
                    stayCon = true;
                    eTname.setHint("Full name");
                    eTname.setVisibility(View.VISIBLE);
                    eTphone.setHint("Phone number");
                    eTphone.setVisibility(View.VISIBLE);
                    bt1.setText("REGISTER");
                    Log.d("a1" ,"switch works");
                    reg();
                } else {
                    stayCon = false;  // Update stayCon when the switch is not checked
                    eTname.setHint(""); // Set to empty string or any default hint
                    eTname.setVisibility(View.GONE);
                    eTphone.setHint(""); // Set to empty string or any default hint
                    eTphone.setVisibility(View.GONE);
                    bt1.setText("LOGIN");
                    log();
                    //צריך להוסי  בדיקת קלט
                }
            }
        });
    }
    public void log(){
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = eTemail.getText().toString();
                password = eTpass.getText().toString();
                registerd = true;
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login1.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=settings.edit();
                                    editor.putBoolean("stayConnect",stayco.isChecked());
                                    editor.commit();
                                    Toast.makeText(Login1.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login1.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                     Toast.makeText(Login1.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void reg(){
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = String.valueOf(eTemail.getText());
                password = String.valueOf(eTpass.getText());
                name=eTname.getText().toString();
                phone=eTphone.getText().toString();
                registerd = false;
                Log.d("ab","button works");
                // Handle registration logic
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    uid = user.getUid();
                                    userdb=new User(name,email,phone,uid);
                                    refUsers.child(uid).setValue(userdb);
                                    Intent intent = new Intent(Login1.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.d("abcde","reg doean't work");
                                    Toast.makeText(Login1.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}