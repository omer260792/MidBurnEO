package com.example.omer.midburneo.Class;

public class ListCampAc {

    public String name;



    public ListCampAc(){


    }
    public ListCampAc(String name){
        this.name = name;

    }

    public String getName() {
        return name;

    }

    public void setName(String name) {
        this.name = name;

    }

    @Override
    public String toString() {
        return name;
    }

    }
