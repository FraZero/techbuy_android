package com.unimol.prova_upload_image;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimol.prova_upload_image.adapter.ProductViewHolder;
import com.unimol.prova_upload_image.models.Product;

import java.util.Locale;

public class Search extends Fragment {

    private RecyclerView recyclerViewSearch;
    private TextInputLayout searchText;
    private AutoCompleteTextView search;
    private FirebaseFirestore firestore;
    private CollectionReference referenceProducts;
    private CollectionReference referenceUsers;
    private FirestoreRecyclerOptions<Product> options;
    private FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;
    private String idUser, idProduct, sellerEmail;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ChipGroup chipGroup;
    private Chip chipTitle, chipCity, chipCondition;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentSearch = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewSearch = fragmentSearch.findViewById(R.id.recycleview_search);
        searchText = fragmentSearch.findViewById(R.id.search_text);
        search = fragmentSearch.findViewById(R.id.auto_complete_search);

        chipGroup = fragmentSearch.findViewById(R.id.chipgroup);
        chipTitle = fragmentSearch.findViewById(R.id.chip_title);
        chipCity = fragmentSearch.findViewById(R.id.chip_city);
        chipCondition = fragmentSearch.findViewById(R.id.chip_condition);

        firestore = FirebaseFirestore.getInstance();
        referenceProducts = firestore.collection("products");
        referenceUsers = firestore.collection("users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setHasFixedSize(true);

       // showAllProduct();

       initialFilter();

        if (!chipTitle.isChecked() || !chipCity.isChecked() || !chipCondition.isChecked()){
            Toast.makeText(getContext(), "FOR SEARCH SELECT A FILTER", Toast.LENGTH_SHORT).show();
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                System.out.println(checkedId);
                filterProducts(group);
            }
        });

        return fragmentSearch;

    }

    private void filterProducts(ChipGroup group) {

        if (group.getCheckedChipId() == chipTitle.getId()){
            String title;
            title = "title";
            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
            search(title);

        } else if (group.getCheckedChipId() == chipCity.getId()) {
            String city;
            city = "city";
            Toast.makeText(getContext(), city, Toast.LENGTH_SHORT).show();
            search(city);

        }  else if (group.getCheckedChipId() == chipCondition.getId()) {
            String condition;
            condition = "condition";
            Toast.makeText(getContext(), condition, Toast.LENGTH_SHORT).show();
            search(condition);
        }
    }

    private void initialFilter(){
        String title;
        title = "title";
        Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
        search(title);
    }

    public void search( String choice) {

        String finalChoice = choice;
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String key;
                key = search.getText().toString();

                firestore.collection("products").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                          //  Query query = referenceProducts.whereEqualTo(finalChoice, key.toLowerCase());
                         Query query = referenceProducts.whereGreaterThanOrEqualTo(finalChoice, key.toLowerCase())
                                 .whereLessThanOrEqualTo(finalChoice, key.toLowerCase() + '~');

                        options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
                        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {

                            @Override
                            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                                holder.titleProduct.setText(model.getTitle());
                                holder.descriptionProduct.setText(model.getDescription());
                                holder.cityProduct.setText(model.getCity());
                                holder.categoryProduct.setText(model.getCategory());
                                holder.conditionProduct.setText(model.getCondition());
                                holder.priceProduct.setText(model.getPrice());
                                holder.sellerMail.setText(model.getSeller());

                                referenceUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()){
                                                idUser = model.getSeller();
                                                if(idUser.equals(document.getId())){
                                                    holder.sellerPhone.setText(document.get("phone").toString());
                                                }
                                            }
                                        } else {
                                            Log.v("ErrorDoc", "Error getting documents.", task.getException());
                                        }

                                    }
                                });

                                idProduct = model.getCodeID();
                                storageReference.child("images/" + "products/" + idProduct + ".jpg").getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String photoUrl = uri.toString();
                                                Glide.with(getContext()).load(photoUrl).into(holder.photoProduct);
                                            }
                                        });

                                holder.moreInfoProduct.setOnClickListener(new View.OnClickListener() {
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


                                sellerEmail = model.getSeller();
                                holder.sellerMail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {sellerEmail});
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), "Error" + e , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                holder.sellerPhone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + holder.sellerPhone.getText().toString()));
                                        startActivity(intent);
                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_view_layout, parent, false);
                                ProductViewHolder productViewHolder = new ProductViewHolder(view);
                                return productViewHolder;
                            }
                        };

                        adapter.startListening();
                        adapter.updateOptions(options);
                        recyclerViewSearch.setAdapter(adapter);


                    }
                });


                    }
                });



              //  Query query = referenceProducts.whereEqualTo(finalChoice, key.toLowerCase());



    }

    private void showAllProduct() {

        options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(referenceProducts, Product.class).build();

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                holder.titleProduct.setText(model.getTitle());
                holder.descriptionProduct.setText(model.getDescription());
                holder.cityProduct.setText(model.getCity());
                holder.categoryProduct.setText(model.getCategory());
                holder.conditionProduct.setText(model.getCondition());
                holder.priceProduct.setText(model.getPrice());
                holder.sellerMail.setText(model.getSeller());

                referenceUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                idUser = model.getSeller();
                                if(idUser.equals(document.getId())){
                                    holder.sellerPhone.setText(document.get("phone").toString());
                                }
                            }
                        } else {
                            Log.v("ErrorDoc", "Error getting documents.", task.getException());
                        }

                    }
                });

                idProduct = model.getCodeID();
                storageReference.child("images/" + "products/" + idProduct + ".jpg").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoUrl = uri.toString();
                                Glide.with(getContext()).load(photoUrl).into(holder.photoProduct);
                            }
                        });

                holder.moreInfoProduct.setOnClickListener(new View.OnClickListener() {
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


                sellerEmail = model.getSeller();
                holder.sellerMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {sellerEmail});
                            intent.setType("message/rfc822");
                          /*  if (intent.resolveActivity(getPackageManager()) != null ){
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "There is no application that support this action" , Toast.LENGTH_LONG).show();
                            }*/
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error" + e , Toast.LENGTH_LONG).show();
                        }
                    }
                });

                holder.sellerPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + holder.sellerPhone.getText().toString()));
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_view_layout, parent, false);
                ProductViewHolder productViewHolder = new ProductViewHolder(view);
                return productViewHolder;
            }
        };

        adapter.startListening();
        recyclerViewSearch.setAdapter(adapter);

    }

}