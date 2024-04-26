package com.example.projekat.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.database.OsobaRepository;


public class ProfilFragment extends Fragment {

    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextName;
    EditText editTextLastname;
    EditText editTextNumber;
    Button buttonIzmeni;
    String ulogovaniEmail;

    public ProfilFragment() {
        // Required empty public constructor
    }

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref_file", Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String ulogovani = sharedPreferences.getString("pref_email", "default");
            String[] niz = ulogovani.split("\n");
            String poslednjiUlogovani = niz[niz.length - 1];
            ulogovaniEmail = poslednjiUlogovani;
        } else {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Inicijalizacija polja iz XML-a
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);
        editTextName = view.findViewById(R.id.name);
        editTextLastname = view.findViewById(R.id.lastname);
        editTextNumber = view.findViewById(R.id.number);
        buttonIzmeni = view.findViewById(R.id.izmena);

        // Dohvatanje i prikazivanje podataka o korisniku kada se otvori fragment
        prikaziPodatke();

        // Postavljanje slušača događaja klikanja na dugme "Izmeni profil"
        buttonIzmeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Poziv metode za ažuriranje podataka korisnika
                azurirajPodatke();
            }
        });

        return view;
    }

    // Metoda za prikazivanje podataka o korisniku
    // Metoda za prikazivanje podataka o korisniku
    private void prikaziPodatke() {
        OsobaRepository osobaRepository = new OsobaRepository(getContext());
        Cursor cursor = osobaRepository.getEntityByEmail(ulogovaniEmail);
        if (cursor != null && cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_EMAIL);
            int lozinkaIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_LOZINKA);
            int imeIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_IME);
            int prezimeIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_PREZIME);
            int telefonIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_TELEFON);

            // Provera da li su sve potrebne kolone pronađene
            if (emailIndex != -1 && lozinkaIndex != -1 && imeIndex != -1 && prezimeIndex != -1 && telefonIndex != -1) {
                editTextUsername.setText(cursor.getString(emailIndex));
                editTextPassword.setText(cursor.getString(lozinkaIndex));
                editTextName.setText(cursor.getString(imeIndex));
                editTextLastname.setText(cursor.getString(prezimeIndex));
                editTextNumber.setText(cursor.getString(telefonIndex));
            } else {
                // Prikazivanje poruke o neuspešnom pronalaženju kolona
                Toast.makeText(getContext(), "Podaci nisu pronađeni", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        } else {
            // Prikazivanje poruke o neuspešnom pronalaženju korisnika
            Toast.makeText(getContext(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
        }
        osobaRepository.DBClose();
    }



    // Metoda za ažuriranje podataka korisnika
    // Metoda za ažuriranje podataka korisnika
    // Metoda za ažuriranje podataka korisnika
    private void azurirajPodatke() {
        // Dohvatanje novih vrednosti iz polja
        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String lastname = editTextLastname.getText().toString();
        String number = editTextNumber.getText().toString();

        // Dohvatanje ID-ja korisnika na osnovu e-maila
        OsobaRepository osobaRepository = new OsobaRepository(getContext());
        Cursor cursor = osobaRepository.getEntityByEmail(ulogovaniEmail);
        //int id = -1; // Inicijalizacija ID-ja na neku podrazumevanu vrednost
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_ID));
            cursor.close();

            // Ažuriranje podataka u bazi podataka
            int updatedRows = osobaRepository.updateData(id, email, password, name, lastname, number);
            osobaRepository.DBClose();

            // Provera da li su podaci uspešno ažurirani
            if (updatedRows > 0) {
                // Prikazivanje poruke o uspešnoj izmeni
                Toast.makeText(getContext(), "Podaci uspešno izmenjeni", Toast.LENGTH_SHORT).show();

                // Ponovno prikazivanje podataka nakon izmene
                prikaziPodatke();
            } else {
                // Prikazivanje poruke o neuspešnoj izmeni
                Toast.makeText(getContext(), "Izmena podataka nije uspela", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Prikazivanje poruke o neuspešnom pronalaženju korisnika
            Toast.makeText(getContext(), "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
        }
    }


}