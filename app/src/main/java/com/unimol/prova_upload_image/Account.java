package com.unimol.prova_upload_image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Account extends Fragment {

    private ImageView photoProfile;
    private TextView userFullname, userEmail, userPhone, unverifiedEmail;
    private MaterialButton edit, deleteAccount, changePassword, logout;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private String id;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentAccount = inflater.inflate(R.layout.fragment_account, container, false);

        photoProfile = fragmentAccount.findViewById(R.id.photo_profile_user);
        userFullname = fragmentAccount.findViewById(R.id.user_fullname_profile);
        userEmail = fragmentAccount.findViewById(R.id.user_email_profile);
        userPhone = fragmentAccount.findViewById(R.id.user_phone_profile);
        unverifiedEmail = fragmentAccount.findViewById(R.id.unverified_email);
        edit = fragmentAccount.findViewById(R.id.btn_edit_profile);
        deleteAccount = fragmentAccount.findViewById(R.id.btn_delete_account);
        changePassword = fragmentAccount.findViewById(R.id.btn_change_password);
        logout = fragmentAccount.findViewById(R.id.btn_logout);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        firestore.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (user.getEmail().equals(document.getId())){
                                    id = document.getId();
                                    userFullname.setText(document.get("name").toString());
                                    userEmail.setText(document.getId());
                                    userPhone.setText(document.get("phone").toString());
                                    storageReference.child("images/"+"profiles/"+ user.getEmail() + ".jpg").getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Glide.with(getContext()).load(imageUrl).into(photoProfile);
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w("ErrorDoc", "Error getting documents.", task.getException());
                        }

                    }
                });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("DELETE ACCOUNT");
                alertDialog.setMessage("Are you sure you want to delete your account? " +"\n"+
                        "Deleting this account will result in completely removing your account from system " +
                        "and you won't be able to access the app.");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //elimina il profilo dell'utente
                        firestore.collection("users").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Account Deleted.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                        //elimina tutti i documenti legati all'utente che si va ad eliminare
                        firestore.collection("products").whereEqualTo("seller", id).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                //permette di gestire più query nello stesso tempo
                                WriteBatch batch = firestore.batch();
                                ArrayList<String> idsArray = new ArrayList<>();
                                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot snapshot: snapshotList){
                                    idsArray.add(snapshot.getId());
                                   batch.delete(snapshot.getReference());
                                }
                                for (String idProd : idsArray) {
                                    storageReference.child("images/" + "products/" + idProd + ".jpg")
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.v("products images", "delete");
                                            Toast.makeText(getContext(), "superato", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.v("DeleteAll", "delete all documente where seller = id");
                                    }
                                }).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("DeleteAll", "Failure");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        //elimina l'immagine di profilo dell'utente
                        storageReference.child("images/"+"profiles/"+ id + ".jpg")
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.v("ProfileImage", "delete");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        //elimina l'autenticazione dell'utente
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.v("authUser", "delete");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.create().show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());
                AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
                passwordreset.setTitle("CHANGE YOUR PASSWORD");
                passwordreset.setMessage("Enter the new password: ");
                passwordreset.setView(resetPassword);
                passwordreset.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
                        String password = resetPassword.getText().toString();
                        fireUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Password changed successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                Toast.makeText(getContext(), "Error: Password not changed.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), "Please login again and try again.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(getContext(), Login.class));
                            }
                        });

                    }
                });
                passwordreset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordreset.create().show();
            }
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            startActivity(new Intent(getContext(), Login.class));
            Toast.makeText(getContext(), "User logged out", Toast.LENGTH_SHORT).show();
        });

        return fragmentAccount;
    }
}