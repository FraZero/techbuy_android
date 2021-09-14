package com.unimol.prova_upload_image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimol.prova_upload_image.adapter.MyProductViewHolder;
import com.unimol.prova_upload_image.adapter.ProductViewHolder;
import com.unimol.prova_upload_image.models.Product;
import com.unimol.prova_upload_image.products.PcDesktop;

public class MyProducts extends Fragment {
    RecyclerView recyclerViewMyProducts;
    private FirebaseFirestore firestore;
    private CollectionReference referenceProducts;
    private CollectionReference referenceUsers;
    private FirestoreRecyclerOptions<Product> options;
    private FirestoreRecyclerAdapter<Product, MyProductViewHolder> adapter;
    private String idUser, idProduct;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentMyProducts = inflater.inflate(R.layout.fragment_my_products, container, false);

        recyclerViewMyProducts = fragmentMyProducts.findViewById(R.id.recycleview_my_products);

        firestore = FirebaseFirestore.getInstance();
        referenceProducts = firestore.collection("products");
        referenceUsers = firestore.collection("users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.v("currentUser", firebaseUser.toString());

        recyclerViewMyProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMyProducts.setHasFixedSize(true);

        showMyProducts();

        return fragmentMyProducts;
    }

    private void showMyProducts() {
        idUser = firebaseUser.getEmail();
        options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(referenceProducts.whereEqualTo("seller", idUser), Product.class).build();

        adapter = new FirestoreRecyclerAdapter<Product, MyProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyProductViewHolder holder, int position, @NonNull Product model) {
                holder.titleMyProduct.setText(model.getTitle());
                holder.descriptionMyProduct.setText(model.getDescription());
                holder.cityMyProduct.setText(model.getCity());
                holder.categoryMyProduct.setText(model.getCategory());
                holder.conditionMyProduct.setText(model.getCondition());
                holder.priceMyProduct.setText(model.getPrice());

                idProduct = model.getCodeID();
                storageReference.child("images/" + "products/" + idProduct + ".jpg").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoUrl = uri.toString();
                                Glide.with(getContext()).load(photoUrl).into(holder.photoMyProduct);
                            }
                        });


                holder.moreInfoMyProduct.setOnClickListener(new View.OnClickListener() {
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

                holder.deleteMyProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                        alertDialog.setTitle("DELETE PRODUCT");
                        alertDialog.setMessage("Are you sure you want to delete this product?");
                        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                idProduct = model.getCodeID();
                                referenceProducts.document(idProduct).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Product Deleted.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }


                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                                storageReference.child("images/" + "products/" + idProduct + ".jpg").delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.v("ProductPhoto", "delete");
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

                holder.editMyProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idProduct = model.getCodeID();
                        Intent intent = new Intent(getContext(), EditProduct.class);
                        intent.putExtra("IdProduct",idProduct);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_product_view_layout, parent, false);
                return new MyProductViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerViewMyProducts.setAdapter(adapter);
    }
}