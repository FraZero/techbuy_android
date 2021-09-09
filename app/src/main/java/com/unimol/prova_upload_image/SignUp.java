package com.unimol.prova_upload_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private TextInputLayout fullnameText, phoneText, emailAddressText, passwordUserText,
            confirmPasswordText;
    private TextInputEditText fullnameEdit, phoneEdit, emailAddressEdit, passwordUserEdit,
            confirmPasswordEdit;
    private MaterialButton createAccounttBtn, loginBtn;
    private ProgressBar progressBarSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullnameText = findViewById(R.id.fullname_text_input);
        phoneText = findViewById(R.id.phone_text_input);
        emailAddressText = findViewById(R.id.email_address_text_input);
        passwordUserText = findViewById(R.id.password_user_text_input);
        confirmPasswordText = findViewById(R.id.confirm_password_text_input);

        fullnameEdit = findViewById(R.id.fullname_edit_input);
        phoneEdit= findViewById(R.id.phone_edit_input);
        emailAddressEdit = findViewById(R.id.email_address_edit_input);
        passwordUserEdit = findViewById(R.id.password_user_edit_input);
        confirmPasswordEdit = findViewById(R.id.confirm_password_edit_input);

        createAccounttBtn = findViewById(R.id.create_account_btn);
        loginBtn = findViewById(R.id.login_btn);

        progressBarSignUp = findViewById(R.id.progress_bar_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


    }

    public void signup(View view) {
        String fullname;
        String phone;
        final  String email;
        String password;
        String confirmPass;

        fullname = fullnameEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        email = emailAddressEdit.getText().toString().trim();
        password = passwordUserEdit.getText().toString().trim();
        confirmPass = confirmPasswordEdit.getText().toString().trim();

        if (fullname.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPass.isEmpty()){
            Toast.makeText(SignUp.this, "Error: All fields must be completed!", Toast.LENGTH_SHORT).show();
            return;
        } else if (fullname.isEmpty()){
            fullnameText.setError("Fullname is required!");
            return;
        } else if (phone.length() != 10) {
            phoneText.setError("Phone not correct");
            return;
        } else if (email.isEmpty() || !email.contains("@")){
            emailAddressText.setError("Email is not valid");
            return;
        }else if (password.isEmpty() || password.length() < 8) {
            passwordUserText.setError("Password must be 8 character");
            Toast.makeText(SignUp.this, "Password must be 8 character", Toast.LENGTH_SHORT).show();
            return;
        }else if (confirmPass.isEmpty() || !confirmPass.equals(password)){
            confirmPasswordText.setError("Password not match!");
            Toast.makeText(SignUp.this, "Password not match!", Toast.LENGTH_SHORT).show();
            return;
        } else {

            progressBarSignUp.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", fullname);
                                user.put("phone", phone);

                                firestore.collection("users")
                                        .document(email)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUp.this, "New user was added successfully ", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Message", "OnFailure : " + e.toString());
                                    }
                                });
                            } else {
                                Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressBarSignUp.setVisibility(View.GONE);
                            }
                        }

                    });
        }
    }


    public void goToLogin(View view) {
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
    }
}