package com.example.projekat.models;

public class Autor {
    private int ID;
    private String imeIPrezime;
    private int godiste;
    private String mestoRodjenja;

    public Autor(int id,String imeIPrezime, int godiste, String mestoRodjenja) {
        this.ID = id;
        this.imeIPrezime = imeIPrezime;
        this.godiste = godiste;
        this.mestoRodjenja = mestoRodjenja;
    }

    public Autor() {

    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImeIPrezime() {
        return imeIPrezime;
    }

    public void setImeIPrezime(String imeIPrezime) {
        this.imeIPrezime = imeIPrezime;
    }

    public int getGodiste() {
        return godiste;
    }

    public void setGodiste(int godiste) {
        this.godiste = godiste;
    }

    public String getMestoRodjenja() {
        return mestoRodjenja;
    }

    public void setMestoRodjenja(String mestoRodjenja) {
        this.mestoRodjenja = mestoRodjenja;
    }
}
