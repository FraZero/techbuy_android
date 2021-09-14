package com.unimol.prova_upload_image.models;

public class Product {

    public String title, description, city, category, condition, price, seller, codeID;

    public Product(String title, String description, String city, String category, String condition, String price, String seller, String codeID) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.category = category;
        this.condition = condition;
        this.price = price;
        this.seller = seller;
        this.codeID = codeID;
    }

    public Product() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCodeID() {
        return codeID;
    }

    public void setCodeID(String codeID) {
        this.codeID = codeID;
    }
}
