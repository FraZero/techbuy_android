package com.unimol.prova_upload_image.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unimol.prova_upload_image.R;

public class MyProductViewHolder extends RecyclerView.ViewHolder {
    public TextView titleMyProduct, descriptionMyProduct, cityMyProduct, categoryMyProduct, conditionMyProduct, priceMyProduct, moreInfoMyProduct, deadlineMyProduct;
    public ImageView photoMyProduct;
    public MaterialButton deleteMyProduct, editMyProduct;


    public MyProductViewHolder(@NonNull View itemView) {
        super(itemView);
        titleMyProduct = itemView.findViewById(R.id.title_my_product);
        descriptionMyProduct = itemView.findViewById(R.id.description_my_product);
        cityMyProduct = itemView.findViewById(R.id.city_my_product);
        categoryMyProduct = itemView.findViewById(R.id.category_my_product);
        conditionMyProduct = itemView.findViewById(R.id.conditions_my_product);
        priceMyProduct= itemView.findViewById(R.id.price_my_product);
        photoMyProduct = itemView.findViewById(R.id.photo_my_product);
        moreInfoMyProduct = itemView.findViewById(R.id.more_info_my_product);
        deleteMyProduct = itemView.findViewById(R.id.btn_delete_product);
        editMyProduct = itemView.findViewById(R.id.btn_edit_product);
        deadlineMyProduct = itemView.findViewById(R.id.expiry_my_ads);
    }
}
