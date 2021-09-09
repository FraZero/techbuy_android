package com.unimol.prova_upload_image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private ImageView photo;
    private MaterialButton editPhoto, takePicture, save;
    private EditText fullnameEdit, phoneEdit;
    private String id;
    private Uri imageUri;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        photo = findViewById(R.id.photo);
        editPhoto = findViewById(R.id.btn_edit_photo);
        takePicture = findViewById(R.id.btn_take_picture);
        fullnameEdit = findViewById(R.id.fullname_text);
        phoneEdit = findViewById(R.id.phone_text);
        save = findViewById(R.id.btn_save);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firestore.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.getEmail().equals(document.getId())){
                                    id = document.getId();
                                    fullnameEdit.setText(document.get("name").toString());
                                    phoneEdit.setText(document.get("phone").toString());
                                    storageReference.child("images/"+"profiles/"+ id + ".jpg").getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Glide.with(EditProfile.this).load(imageUrl).into(photo);
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w("ErrorDoc", "Error getting documents.", task.getException());
                        }

                    }
                });

    }


    public void save(View view) {
        String fullname;
        String phone;

        fullname = fullnameEdit.getText().toString();
        phone = phoneEdit.getText().toString();

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("name", fullname);
        userMap.put("phone", phone);

        firestore.collection("users")
                .document(id)
                .update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfile.this, "Edit successfully ", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditProfile.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Edit fail! ", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void takePictureCamera(View view) {
        //chiedo i permessi
        if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfile.this, new String[] {Manifest.permission.CAMERA}, 101);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else {
                Toast.makeText(EditProfile.this, "Camera authorization required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    public void editPhotoProfile(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            photo.setImageURI(imageUri);
            uploadImage();
        }

        if (requestCode == 101) {
            assert data != null;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            photo.setImageBitmap(bitmap);
            uploadBitmapImage(bitmap);
        }
    }

    private void uploadBitmapImage(Bitmap bitmapImage) {
        final StorageReference referenceImage = storageReference.child("images/"+"profiles/"+ id + ".jpg");
        photo.setDrawingCacheEnabled(true);
        photo.buildDrawingCache();
        bitmapImage = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = referenceImage.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Unmodified image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Correctly edited image", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void uploadImage() {
        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setTitle("Caricamento file...");
        dialog.show();

        StorageReference imageRef;
        imageRef = FirebaseStorage.getInstance().getReference("images/"+"profiles/"+ id + ".jpg");
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photo.setImageURI(imageUri);
                Toast.makeText(EditProfile.this, "Correctly edited image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
    }


    public void goBack(View view) {
        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        startActivity(intent);
    }
}