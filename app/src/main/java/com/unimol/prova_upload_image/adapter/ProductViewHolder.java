package com.unimol.prova_upload_image.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimol.prova_upload_image.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public TextView titleProduct, descriptionProduct, cityProduct, categoryProduct, conditionProduct, priceProduct, sellerMail, sellerPhone, moreInfoProduct, deadlineProduct;
    public ImageView photoProduct;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        titleProduct = itemView.findViewById(R.id.title_product);
        descriptionProduct = itemView.findViewById(R.id.description_product);
        cityProduct = itemView.findViewById(R.id.city_product);
        categoryProduct = itemView.findViewById(R.id.category_product);
        conditionProduct = itemView.findViewById(R.id.conditions_product);
        priceProduct = itemView.findViewById(R.id.price_product);
        sellerMail = itemView.findViewById(R.id.seller_mail);
        sellerPhone = itemView.findViewById(R.id.seller_phone);
        photoProduct = itemView.findViewById(R.id.photo_product);
        moreInfoProduct = itemView.findViewById(R.id.more_info_product);
        deadlineProduct = itemView.findViewById(R.id.expiry_ads);
    }
}
