package com.example.vetclinicapp.models;

public class pets_and_users_details_model {
    String ownerContact, ownerEmail, ownerName, petAddress, petAge,
            petBirth, petImage, petName, breed, sched_date;

    public pets_and_users_details_model() {
    }


    public pets_and_users_details_model(String ownerContact, String ownerEmail, String ownerName, String petAddress, String petAge, String petBirth, String petImage, String petName, String breed, String sched_date) {
        this.ownerContact = ownerContact;
        this.ownerEmail = ownerEmail;
        this.ownerName = ownerName;
        this.petAddress = petAddress;
        this.petAge = petAge;
        this.petBirth = petBirth;
        this.petImage = petImage;
        this.petName = petName;
        this.breed = breed;
        this.sched_date = sched_date;
    }

    public String getSched_date() {
        return sched_date;
    }

    public void setSched_date(String sched_date) {
        this.sched_date = sched_date;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPetAddress() {
        return petAddress;
    }

    public void setPetAddress(String petAddress) {
        this.petAddress = petAddress;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public String getPetBirth() {
        return petBirth;
    }

    public void setPetBirth(String petBirth) {
        this.petBirth = petBirth;
    }

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
