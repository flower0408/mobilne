package com.example.projekat.models;

public class Komentar {
    private int ID;
    private String imeIPrezime;
    private String komentar;
    private Knjiga knjiga;

    public Komentar(int id, String imeIPrezime, String komentar, Knjiga knjiga) {
        this.ID = id;
        this.imeIPrezime = imeIPrezime;
        this.komentar = komentar;
        this.knjiga = knjiga;
    }

    public Komentar() {

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

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public Knjiga getKnjiga() {
        return knjiga;
    }

    public void setKnjiga(Knjiga knjiga) {
        this.knjiga = knjiga;
    }
}
