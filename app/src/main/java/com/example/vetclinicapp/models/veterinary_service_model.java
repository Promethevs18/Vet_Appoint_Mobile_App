package com.example.vetclinicapp.models;

public class veterinary_service_model {
    String veterinary_image, veterinary_name, short_desc, price;

    public veterinary_service_model(String veterinary_image, String veterinary_name, String short_desc, String price) {
        this.veterinary_image = veterinary_image;
        this.veterinary_name = veterinary_name;
        this.short_desc = short_desc;
        this.price = price;
    }

    public veterinary_service_model() {
    }

    public String getVeterinary_image() {
        return veterinary_image;
    }

    public void setVeterinary_image(String veterinary_image) {
        this.veterinary_image = veterinary_image;
    }

    public String getVeterinary_name() {
        return veterinary_name;
    }

    public void setVeterinary_name(String veterinary_name) {
        this.veterinary_name = veterinary_name;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
