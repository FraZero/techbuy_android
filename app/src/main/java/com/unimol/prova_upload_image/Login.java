package com.unimol.prova_upload_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputLayout emailText, passwordText;
    private TextInputEditText emailEdit, passwordEdit;
    private MaterialButton btnLogin, btnCreateAccount;
    private CheckBox rememberMe;
    private TextView forgetPasswordText;
    private ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.email_text_input);
        passwordText = findViewById(R.id.password_text_input);
        emailEdit = findViewById(R.id.email_edit_input);
        passwordEdit = findViewById(R.id.password_edit_text);
        rememberMe = findViewById(R.id.remember_me);
        forgetPasswordText = findViewById(R.id.forget_password);
        btnLogin = findViewById(R.id.btn_login);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        progressBarLogin = findViewById(R.id.progress_bar_login);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("Credentials",MODE_PRIVATE);
        String username = sharedPreferences.getString("Email","");
        String pass = sharedPreferences.getString("Password","");

        emailEdit.setText(username);
        passwordEdit.setText(pass);

    }

    public void login(View view) {
        checkCredentialslogin();
    }

    private void checkCredentialslogin() {
        final String email;
        final String password;

        email = emailEdit.getText().toString().trim();
        password = passwordEdit.getText().toString().trim();


        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(Login.this, "Error: all fields must be completed! ", Toast.LENGTH_LONG).show();
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            emailText.setError("Email is not valid");
            Toast.makeText(Login.this, "Email is not valid", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty() || password.length() < 8) {
            passwordText.setError("Password must be 8 character");
            Toast.makeText(Login.this, "Password must be 8 character", Toast.LENGTH_LONG).show();
            return;
        }
        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences("Credentials",MODE_PRIVATE).edit();
                    if (rememberMe.isChecked()){
                        //rememberCredentials(email, password);
                        editor.clear();
                        editor.putString("Email", email);
                        editor.putString("Password",password);
                        editor.apply();
                        Toast.makeText(Login.this, "User logged ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        editor.clear();
                        editor.remove("Credentials");
                        editor.apply();
                        rememberMe.setChecked(false);
                        Toast.makeText(Login.this, "User logged ", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(Login.this, "Error: Access is denied", Toast.LENGTH_SHORT).show();
                    progressBarLogin.setVisibility(View.GONE);
                }
            }
        });

    }


    public void goToRegistration(View view) {
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

    public void forgetPassword(View view) {
        EditText resetEmail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Did you forget your password?");
        passwordResetDialog.setMessage("Enter your email to get the reset link:");
        passwordResetDialog.setView(resetEmail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail;
                mail = resetEmail.getText().toString();

                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error: Reset link not sent ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        passwordResetDialog.create().show();
    }
}