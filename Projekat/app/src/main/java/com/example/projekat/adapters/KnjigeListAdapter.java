package com.example.projekat.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.fragments.IzmenaKnjigeFragment;
import com.example.projekat.models.Knjiga;

import java.util.ArrayList;

/*
 * Adapteri unutar Android-a sluze da prikazu unapred nedefinisanu kolicinu podataka
 * pristigle sa interneta ili ucitane iz baze ili filesystem-a uredjaja.
 * Da bi napravili adapter treba da napraivmo klasu, koja nasledjuje neki od postojecih adaptera.
 * Za potrebe ovih vezbi koristicemo ArrayAdapter koji kao izvor podataka iskoristi listu ili niz.
 * Nasledjivanjem bilo kog adaptera, dobicemo
 * nekolko metoda koje moramo da referinisemo da bi adapter ispravno radio.
 * */
public class KnjigeListAdapter extends ArrayAdapter<Knjiga> {
    private ArrayList<Knjiga> listKnjige;
    private KnjigeRepository knjigeRepository;

    public KnjigeListAdapter(Context context, ArrayList<Knjiga> knjige){
        super(context, R.layout.knjiga_card, knjige);
        listKnjige = knjige;
    }

    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return listKnjige.size();
    }

    /*
     * Ova metoda vraca pojedinacan element na osnovu pozicije
     * */
    @Nullable
    @Override
    public Knjiga getItem(int position) {
        return listKnjige.get(position);
    }

    /*
     * Ova metoda vraca jedinstveni identifikator, za adaptere koji prikazuju
     * listu ili niz, pozicija je dovoljno dobra. Naravno mozemo iskoristiti i
     * jedinstveni identifikator objekta, ako on postoji.
     * */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        knjigeRepository = new KnjigeRepository(getContext());

        Knjiga knjiga = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.knjiga_card,
                    parent, false);
        }
        TextView naslov = convertView.findViewById(R.id.naslov);
        TextView brStranica = convertView.findViewById(R.id.brStranica);
        TextView povez = convertView.findViewById(R.id.povez);
        TextView zanr = convertView.findViewById(R.id.zanr);
        TextView autor = convertView.findViewById(R.id.autor);

        Button obrisi = convertView.findViewById(R.id.delButton);
        obrisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OBRISI", Long.toString(getItem(position).getID()));
                knjigeRepository.deleteData(getItem(position).getID());
                listKnjige.remove(position);
                notifyDataSetChanged();
            }
        });

        Button izmeni = convertView.findViewById(R.id.izmeni);
        izmeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on the "Izmeni" button
                Knjiga selectedKnjiga = getItem(position);
                if (selectedKnjiga != null) {
                    // Navigate to IzmenaKnjigeFragment with the selected Knjiga object
                    IzmenaKnjigeFragment fragment = IzmenaKnjigeFragment.newInstance(selectedKnjiga);
                    fragment.setSelectedKnjiga(selectedKnjiga);

                    FragmentTransaction transaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        if(knjiga != null){
            naslov.setText("Naslov: " + knjiga.getNaslov());
            brStranica.setText("Broj stranica: " + Integer.toString(knjiga.getBrStranica()));
            povez.setText("Povez: " + knjiga.getPovez());
            zanr.setText("Zanr: " + knjiga.getZanr());
            autor.setText("Autor: " + knjiga.getAutor().getImeIPrezime());

            autor.setOnClickListener(v -> {
                // Handle click on the item at 'position'
                String whereClause = DatabaseConstants.COLUMN_AUTOR + " = ?";
                String[] whereArgs = {String.valueOf(knjiga.getAutor().getID())};
                Log.i("KNJIGE: ", Integer.toString(knjiga.getAutor().getID()));

                Cursor cursor = knjigeRepository.getData(new String[]{DatabaseConstants.COLUMN_NASLOV}, whereClause, whereArgs, null);
                String imenaKnjiga = "";
                while (cursor.moveToNext()){
                    imenaKnjiga += cursor.getString(0) + "\r\n";
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage(imenaKnjiga)
                        .setCancelable(false)
                        .setPositiveButton("U redu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            });
        }

        return convertView;
    }
}
