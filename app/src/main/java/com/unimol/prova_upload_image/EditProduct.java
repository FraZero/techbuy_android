package com.unimol.prova_upload_image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimol.prova_upload_image.adapter.PlaceAutoSuggestAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProduct extends AppCompatActivity {
    private TextInputLayout titleText, descriptionText, categoryChoice, conditionChoice, placeText, priceText;
    private TextInputEditText titleEdit, descriptionEdit, priceEdit;
    private AutoCompleteTextView autoCompleteCategory, autoCompleteCondition, autoCompletePlace;
    private ImageView imageProduct;
    private MaterialButton btnEditPhotoProduct, btnSaveSpecifications, btnBack;
    private TextView moreInfo;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    private String idUser, idMyProduct;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CollectionReference referenceProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        idMyProduct = intent.getStringExtra("IdProduct");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        referenceProducts = firestore.collection("products");

        idUser = user.getEmail();

        imageProduct = findViewById(R.id.image_myproduct);

        titleText = findViewById(R.id.title_my_text);
        titleEdit = findViewById(R.id.title_my_edit);

        descriptionText = findViewById(R.id.description_my_text);
        descriptionEdit = findViewById(R.id.description_my_edit);

        categoryChoice = findViewById(R.id.category_my_choice);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory_my);

        conditionChoice = findViewById(R.id.conditions_my_choice);
        autoCompleteCondition = findViewById(R.id.autoCompleteConditions_my);

        placeText = findViewById(R.id.place_my_text);
        autoCompletePlace = findViewById(R.id.autoCompletePlace_my);

        priceText = findViewById(R.id.price_my_text);
        priceEdit = findViewById(R.id.price_my_edit);

        moreInfo = findViewById(R.id.more_info_my);

        btnEditPhotoProduct = findViewById(R.id.btn_edit_photo_product);
        btnSaveSpecifications = findViewById(R.id.btn_save_my_changes);
        btnBack = findViewById(R.id.btn_back);

        priceEdit.setFilters(new InputFilter[]{ new EditProduct.DecimalDigitsInputFilter(9,2)});

        autoCompletePlace.setAdapter(new PlaceAutoSuggestAdapter(getApplicationContext(),R.layout.dropdown_item_place));

        String[] categories = new String[]
                {"Pc Desktop", "Notebook", "Tablet", "Smartphone", "Smartwatch", "Accessories"};
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_categories, categories);
        autoCompleteCategory.setAdapter(adapterCategories);

        String[] conditions = new String[]
                {"New of stock", "Grading A", "Grading B", "Grading C", "Grading D" };
        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item_conditions, conditions);
        autoCompleteCondition.setAdapter(adapterConditions);

        referenceProducts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if (idMyProduct.equals(document.getId())){
                            titleEdit.setText(document.get("title").toString());
                            descriptionEdit.setText(document.get("description").toString());
                            autoCompletePlace.setText(document.get("city").toString());
                            autoCompleteCategory.setText(document.get("category").toString());
                            autoCompleteCondition.setText(document.get("condition").toString());
                            priceEdit.setText(document.get("price").toString());

                            storageReference.child("images/"+"products/"+ idMyProduct + ".jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            Glide.with(EditProduct.this).load(imageUrl).into(imageProduct);
                                        }
                                    });
                        }
                    }

                } else {
                    Log.w("ErrorDoc", "Error getting documents.", task.getException());
                }

            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditProduct.this);
                builder.setTitle("Products grading");
                builder.setMessage("New of stock : The product is new, it's still packed in its original box." + "\n" + "\n"
                        + "Grading A : The products have no aesthetic defects, are functional and are to be considered as like new." + "\n"+ "\n"
                        + "Grading B : The products are technically functional but may have very slight aesthetic defects on the body " +
                        "or slight scratches on the screen, visible when the screen is off" + "\n" + "\n"
                        + "Grading C : The products have scratches / signs of wear or fading along the frame " +
                        "that do not compromise operation and are to be considered in normal wear." + "\n"  + "\n"
                        + "Grading D : The products have scratches / signs of wear or fading along the frame in some cases " +
                        "they may also have scratches on the glass or small dents that do not compromise operation " +
                        "are to be considered very worn. ");

                builder.create().show();
            }
        });

    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void editPhotoProduct(View view) {
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
            imageProduct.setImageURI(imageUri);
            uploadImageProduct();
        }
    }

    private void uploadImageProduct() {
        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setTitle("Caricamento file...");
        dialog.show();

        StorageReference imageRef;
        imageRef = FirebaseStorage.getInstance().getReference("images/"+"products/"+ idMyProduct + ".jpg");
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageProduct.setImageURI(imageUri);
                Toast.makeText(EditProduct.this, "Correctly edited image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
    }

    public void saveSpecifications(View view) {
        String title, description, city, category, condition, price;

        title = titleEdit.getText().toString();
        description = descriptionEdit.getText().toString();
        city = autoCompletePlace.getText().toString();
        category = autoCompleteCategory.getText().toString();
        condition = autoCompleteCondition.getText().toString();
        price = priceEdit.getText().toString();

        Map<String, Object> editProductMap = new HashMap<>();
        editProductMap.put("title", title);
        editProductMap.put("description", description);
        editProductMap.put("city", city);
        editProductMap.put("category", category);
        editProductMap.put("condition", condition);
        editProductMap.put("price", price);

        referenceProducts.document(idMyProduct)
                .update(editProductMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditProduct.this, "Edit successfully ", Toast.LENGTH_LONG).show();
                startActivity(new Intent(EditProduct.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProduct.this, "Edit fail! ", Toast.LENGTH_LONG).show();
            }
        });
    }


    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }
}