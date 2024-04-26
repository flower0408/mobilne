package com.example.projekat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projekat.R;
import com.example.projekat.database.KomentarRepository;
import com.example.projekat.models.Komentar;

import java.util.ArrayList;

public class KomentariListAdapter extends ArrayAdapter<Komentar> {

    private ArrayList<Komentar> listKomentari;
    private KomentarRepository komentarRepository;

    public KomentariListAdapter(Context context, ArrayList<Komentar> komentari){
        super(context, R.layout.komentar_card, komentari);
        listKomentari = komentari;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return listKomentari.size();
    }

    /*
     * Ova metoda vraca pojedinacan element na osnovu pozicije
     * */
    @Nullable
    @Override
    public Komentar getItem(int position) {
        return listKomentari.get(position);
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
        komentarRepository = new KomentarRepository(getContext());

        Komentar komentar = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.komentar_card,
                    parent, false);
        }
        TextView imeIPrezime = convertView.findViewById(R.id.ime_i_prezime);
        TextView komentarText = convertView.findViewById(R.id.komentar);
        TextView naslov = convertView.findViewById(R.id.naslov);

        if(komentar != null){
            imeIPrezime.setText("Ime i prezime: " + komentar.getImeIPrezime());
            komentarText.setText("Tekst komentara: " + komentar.getKomentar());
            naslov.setText("Knjiga: " + komentar.getKnjiga().getNaslov());
        }
        return convertView;
    }
}
