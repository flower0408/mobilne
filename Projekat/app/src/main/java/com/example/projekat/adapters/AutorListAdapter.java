package com.example.projekat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.models.Autor;

import java.util.ArrayList;

public class AutorListAdapter extends ArrayAdapter<Autor> {
    private ArrayList<Autor> listAutori;
    private KnjigeRepository knjigeRepository;
    public AutorListAdapter(@NonNull Context context, @NonNull ArrayList<Autor> autori) {
        super(context, R.layout.autor_card, autori);
        listAutori = autori;
    }

    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return listAutori.size();
    }

    /*
     * Ova metoda vraca pojedinacan element na osnovu pozicije
     * */
    @Nullable
    @Override
    public Autor getItem(int position) {
        return listAutori.get(position);
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
        Autor autor = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autor_card,
                    parent, false);
        }
        TextView imeIPrezime = convertView.findViewById(R.id.ime_i_prezime);
        TextView godiste = convertView.findViewById(R.id.godiste);
        TextView mestoRodenja = convertView.findViewById(R.id.mesto_rodjenja);
        LinearLayout autorCard = convertView.findViewById(R.id.autor_card_item);

        autorCard.setOnClickListener(v -> {
            // Handle click on the item at 'position'
            String whereClause = DatabaseConstants.COLUMN_AUTOR + " = ?";
            String[] whereArgs = {String.valueOf(autor.getID())};

            Cursor cursor = knjigeRepository.getData(new String[]{DatabaseConstants.COLUMN_NASLOV}, whereClause, whereArgs, null);
            String imenaAutora = "";
            while (cursor.moveToNext()){
                imenaAutora += cursor.getString(0) + "\r\n";
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(imenaAutora)
                    .setCancelable(false)
                    .setPositiveButton("U redu", (dialogInterface, i) -> {

                    });
            AlertDialog alert = dialog.create();
            alert.show();
        });

        if(autor != null){
            imeIPrezime.setText(autor.getImeIPrezime());
            godiste.setText("Godiste: " + autor.getGodiste());
            mestoRodenja.setText("Mesto rodjenja: " + autor.getMestoRodjenja());
        }

        return convertView;
    }
}
