package com.unimol.prova_upload_image.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unimol.prova_upload_image.models.Product;
import com.unimol.prova_upload_image.R;
import com.unimol.prova_upload_image.adapter.ProductViewHolder;

import java.util.Date;
import java.util.List;

public class Notebook extends AppCompatActivity {
    private RecyclerView recyclerViewNotebook;
    private FirebaseFirestore firestoreDB;
    private CollectionReference referenceProducts;
    private CollectionReference referenceUsers;
    private FirestoreRecyclerOptions<Product> options;
    private FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;
    private String idUser, idProduct, sellerEmail;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        recyclerViewNotebook = findViewById(R.id.recycleview_notebook);

        firestoreDB = FirebaseFirestore.getInstance();
        referenceProducts = firestoreDB.collection("products");

        firestoreDB.collection("products").whereLessThanOrEqualTo("deadline", new Date().getTime()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = firestoreDB.batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            batch.delete(snapshot.getReference());
                        }
                        System.out.println("deleted expired article");

                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.v("DeleteAll", "delete all documente where seller = id");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("DeleteAll", "Failure");
                            }
                        });
                    }
                });

        referenceUsers = firestoreDB.collection("users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerViewNotebook.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewNotebook.setHasFixedSize(true);

        showNotebookProducts();

    }

    private void showNotebookProducts() {
        options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(referenceProducts.whereEqualTo("category", "Notebook"), Product.class).build();

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
                                Glide.with(Notebook.this).load(photoUrl).into(holder.photoProduct);
                            }
                        });

                holder.cityProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Notebook.this, FindPlace.class);
                        intent.putExtra("endpoint", holder.cityProduct.getText().toString());
                        startActivity(intent);
                    }
                });


                holder.moreInfoProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Notebook.this);
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
                            if (intent.resolveActivity(getPackageManager()) != null ){
                                startActivity(intent);
                            } else {
                                Toast.makeText(Notebook.this, "There is no application that support this action" , Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Notebook.this, "Error" + e , Toast.LENGTH_LONG).show();
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
        recyclerViewNotebook.setAdapter(adapter);

    }
}