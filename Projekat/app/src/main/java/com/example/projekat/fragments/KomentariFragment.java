package com.example.projekat.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.Utils.FragmentTransition;
import com.example.projekat.adapters.KomentariListAdapter;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.database.KomentarRepository;
import com.example.projekat.models.Autor;
import com.example.projekat.models.Knjiga;
import com.example.projekat.models.Komentar;
import com.example.projekat.models.Zanr;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class KomentariFragment extends ListFragment {
    private KomentarRepository komentarRepository;
    private KomentariListAdapter komentariListAdapter;
    private ArrayList<Komentar> komentari;

    public KomentariFragment() {
        // Required empty public constructor
    }

    public static KomentariFragment newInstance() {
        return new KomentariFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        komentarRepository = new KomentarRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_komentari, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button dodaj = view.findViewById(R.id.dodaj);
        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransition.to(NoviKomentarFragment.newInstance(), requireActivity(), true, R.id.fragmentContainer);
            }
        });

        loadKomentari();

        EditText pretraga = view.findViewById(R.id.searchBar);
        Button pretrazi = view.findViewById(R.id.pretrazi);
        pretrazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Komentar> res = (ArrayList<Komentar>) komentari.stream()
                        .filter(s -> s.getKnjiga().getNaslov().equals(pretraga.getText().toString()))
                        .collect(Collectors.toList());
                if (pretraga.getText().toString().equals("")){
                    komentariListAdapter = new KomentariListAdapter(getActivity(), komentari);
                } else {
                    komentariListAdapter = new KomentariListAdapter(getActivity(), res);
                }
                setListAdapter(komentariListAdapter);
            }
        });

    }

    private void loadKomentari() {
        String[] projection = { DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                DatabaseConstants.COLUMN_KOMENTAR, DatabaseConstants.COLUMN_KNJIGA};
        Cursor cursor = komentarRepository.getData(projection, null, null, null);

        komentari = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Komentar row = new Komentar();
                row.setID(Integer.parseInt(cursor.getString(0)));
                row.setImeIPrezime(cursor.getString(1));
                row.setKomentar(cursor.getString(2));

                KnjigeRepository knjigeRepository = new KnjigeRepository(getContext());
                Cursor cursor_k = knjigeRepository.getEntity(cursor.getInt(3));
                if (cursor_k.getCount() != 0) {
                    cursor_k.moveToNext();
                    Knjiga knjiga = new Knjiga(cursor_k.getInt(0),
                            cursor_k.getString(1), cursor_k.getInt(2),
                            cursor_k.getString(3), Zanr.valueOf(cursor_k.getString(4)), new Autor());
                    row.setKnjiga(knjiga);
                    komentari.add(row);
                }
            }
            komentariListAdapter = new KomentariListAdapter(getActivity(), komentari);
            setListAdapter(komentariListAdapter);
        }
        komentarRepository.DBClose();
    }
}