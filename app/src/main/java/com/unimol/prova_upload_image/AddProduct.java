package com.unimol.prova_upload_image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unimol.prova_upload_image.adapter.PlaceAutoSuggestAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddProduct extends Fragment {

    private TextInputLayout titleText, descriptionText, categoryChoice, conditionChoice, placeText, priceText;
    private TextInputEditText titleEdit, descriptionEdit, priceEdit;
    private AutoCompleteTextView autoCompleteCategory, autoCompleteCondition, autoCompletePlace;
    private ImageView imageProduct;
    private MaterialButton btnPhotoProduct, btnAddProduct;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    private String id;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView moreInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final  View fragmentAddProduct = inflater.inflate(R.layout.fragment_add_product, container, false);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        titleText = fragmentAddProduct.findViewById(R.id.title_text);
        titleEdit = fragmentAddProduct.findViewById(R.id.title_edit);

        descriptionText = fragmentAddProduct.findViewById(R.id.description_text);
        descriptionEdit = fragmentAddProduct.findViewById(R.id.description_edit);

        placeText = fragmentAddProduct.findViewById(R.id.place_text);
        autoCompletePlace = fragmentAddProduct.findViewById(R.id.autoCompletePlace);

        categoryChoice = fragmentAddProduct.findViewById(R.id.category_choice);
        autoCompleteCategory = fragmentAddProduct.findViewById(R.id.autoCompleteCategory);

        conditionChoice = fragmentAddProduct.findViewById(R.id.conditions_choice);
        autoCompleteCondition = fragmentAddProduct.findViewById(R.id.autoCompleteConditions);

        priceText = fragmentAddProduct.findViewById(R.id.price_text);
        priceEdit = fragmentAddProduct.findViewById(R.id.price_edit);

        imageProduct = fragmentAddProduct.findViewById(R.id.image_product);

        btnPhotoProduct = fragmentAddProduct.findViewById(R.id.btn_photo_product);
        btnAddProduct = fragmentAddProduct.findViewById(R.id.btn_add);

        moreInfo = fragmentAddProduct.findViewById(R.id.more_info);

       id = GenerateRandomString.randomString(20);

       priceEdit.setFilters(new InputFilter[]{ new DecimalDigitsInputFilter(9,2)});

       autoCompletePlace.setAdapter(new PlaceAutoSuggestAdapter(getContext(),R.layout.dropdown_item_place));

        //creiamo un array di stringhe che contine le nostre categorie
        String[] categories = new String[]
                {"Pc Desktop", "Notebook", "Tablet", "Smartphone", "Smartwatch", "Accessories"};
        //creiamo un arrayadapter e andiamo a configurarlo
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(getContext(), R.layout.dropdown_item_categories, categories);
        //settiamo l'adapter al autoCompleteText
        autoCompleteCategory.setAdapter(adapterCategories);

        //creiamo un array di stringhe che contine le condizioni del'oggetto
        String[] conditions = new String[]
                {"New of stock", "Grading A", "Grading B", "Grading C", "Grading D" };
        //creiamo un arrayadapter e andiamo a configurarlo
        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(getContext(), R.layout.dropdown_item_conditions, conditions);
        //settiamo l'adapter al autoCompleteText
        autoCompleteCondition.setAdapter(adapterConditions);

        btnPhotoProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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


        return fragmentAddProduct;
    }

    private void addProduct() {
        String title, description, city, category, condition, price, seller ,codeID;

        title = titleEdit.getText().toString();
        description = descriptionEdit.getText().toString();
        city = autoCompletePlace.getText().toString();
        category = autoCompleteCategory.getText().toString();
        condition = autoCompleteCondition.getText().toString();
        price = priceEdit.getText().toString();
        seller = user.getEmail();
        codeID = id;


        //checkFields
        if (title.isEmpty() && description.isEmpty() && city.isEmpty() && category.isEmpty()
                    && condition.isEmpty() && price.isEmpty() ) {
                Toast.makeText(getContext(), "Error: All fields must be completed!", Toast.LENGTH_SHORT).show();
        } else if (title.isEmpty()){
                titleText.setError("Title is required!");
        } else if (description.isEmpty()){
                descriptionText.setError("Description is required!");
        }else if (city.isEmpty()){
                placeText.setError("City is required!");
            Toast.makeText(getContext(), "City is required!", Toast.LENGTH_SHORT).show();
        }else if (category.isEmpty()){
                categoryChoice.setError("Category is required!");
            Toast.makeText(getContext(), "Category is required!", Toast.LENGTH_SHORT).show();
        }else if (condition.isEmpty()){
                conditionChoice.setError("Condition of product is required!");
            Toast.makeText(getContext(), "Condition of product is required!", Toast.LENGTH_SHORT).show();
        }else if (price.isEmpty()){
                priceText.setError("Price is required!");
        }else {

          /*  Map<String, Object> newProduct = new HashMap<>();
            newProduct.put("title", title);
            newProduct.put("description", description);
            newProduct.put("city", city);
            newProduct.put("category", category);
            newProduct.put("condition", condition);
            newProduct.put("price", price);
            newProduct.put("seller", seller);
            newProduct.put("codeID", codeID);*/

            Product newProduct = new Product(title, description, city, category, condition, price, seller, codeID);
            firestore.collection("products").document(id).set(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Added product!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error: product not added", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, 1);
        loadImage.launch(intent);
    }

    ActivityResultLauncher<Intent> loadImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data;
                    data = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = data.getData();
                        imageProduct.setImageURI(imageUri);
                        uploadImageProduct();
                    }
                }
            });

    private void uploadImageProduct() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Caricamento immagine...");
        pd.show();
        StorageReference imageRef = storageReference.child("images/"+"products/" + id + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Impossibile caricare immagine", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static class GenerateRandomString {
        public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static Random RANDOM = new Random();

        public static String randomString(int len) {
            StringBuilder sb = new StringBuilder(len);

            for (int i = 0; i < len; i++) {
                sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
            }

            return sb.toString();
        }
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


