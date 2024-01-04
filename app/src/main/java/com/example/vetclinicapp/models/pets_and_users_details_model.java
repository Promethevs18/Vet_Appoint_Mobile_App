package com.example.vetclinicapp.models;

public class pets_and_users_details_model {
    String ownerContact, ownerEmail, owner, petAddress, petAge,
            petBirth, petImage, petName, breed, sched_date, services, patients, notes, imageUrl, sched_time, status;

    public pets_and_users_details_model(String ownerContact, String ownerEmail, String owner, String petAddress, String petAge, String petBirth, String petImage, String petName, String breed, String sched_date, String services, String patients, String notes, String imageUrl, String sched_time, String status) {
        this.ownerContact = ownerContact;
        this.ownerEmail = ownerEmail;
        this.owner = owner;
        this.petAddress = petAddress;
        this.petAge = petAge;
        this.petBirth = petBirth;
        this.petImage = petImage;
        this.petName = petName;
        this.breed = breed;
        this.sched_date = sched_date;
        this.services = services;
        this.patients = patients;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.sched_time = sched_time;
        this.status = status;
    }

    public pets_and_users_details_model() {
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getSched_date() {
        return sched_date;
    }

    public void setSched_date(String sched_date) {
        this.sched_date = sched_date;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getPatients() {
        return patients;
    }

    public void setPatients(String patients) {
        this.patients = patients;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSched_time() {
        return sched_time;
    }

    public void setSched_time(String sched_time) {
        this.sched_time = sched_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}