package com.example.projekat.fragments;

import android.annotation.SuppressLint;
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
import com.example.projekat.adapters.KnjigeListAdapter;
import com.example.projekat.adapters.KomentariListAdapter;
import com.example.projekat.database.AutorRepository;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.models.Autor;
import com.example.projekat.models.Knjiga;
import com.example.projekat.models.Komentar;
import com.example.projekat.models.Zanr;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class KnjigeFragment extends ListFragment {
    private KnjigeRepository knjigeRepository;
    private KnjigeListAdapter knjigeListAdapter;

    private ArrayList<Knjiga> knjige;

    public KnjigeFragment() {
        // Required empty public constructor
    }
    public static KnjigeFragment newInstance() {
        return new KnjigeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knjigeRepository = new KnjigeRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_knjige, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadKnjige();

        Button dodaj = view.findViewById(R.id.dodaj);
        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransition.to(NovaKnjigaFragment.newInstance(), requireActivity(), true, R.id.fragmentContainer);
            }
        });

        EditText pretraga = view.findViewById(R.id.searchBar1);
        Button pretrazi = view.findViewById(R.id.pretrazi1);
        pretrazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Knjiga> res = (ArrayList<Knjiga>) knjige.stream()
                        .filter(s -> s.getAutor().getImeIPrezime().equals(pretraga.getText().toString()))
                        .collect(Collectors.toList());
                if (pretraga.getText().toString().equals("")){
                    knjigeListAdapter = new KnjigeListAdapter(getActivity(), knjige);
                } else {
                    knjigeListAdapter = new KnjigeListAdapter(getActivity(), res);
                }
                setListAdapter(knjigeListAdapter);
            }
        });
    }

    @SuppressLint("Range")
    private void loadKnjige() {
        String[] projection = { DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_NASLOV, DatabaseConstants.COLUMN_BRSTRANICA,
                DatabaseConstants.COLUMN_POVEZ, DatabaseConstants.COLUMN_ZANR, DatabaseConstants.COLUMN_AUTOR};
        Cursor cursor = knjigeRepository.getData(projection, null, null, null);

        knjige = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Knjiga row = new Knjiga();
                row.setID(Integer.parseInt(cursor.getString(0)));
                row.setNaslov(cursor.getString(1));
                row.setBrStranica(cursor.getInt(2));
                row.setPovez(cursor.getString(3));
                //row.setZanr(cursor.getString(4));
                row.setZanr(Zanr.valueOf(cursor.getString(4)));

                AutorRepository autorRepository = new AutorRepository(getContext());
                Cursor cursor_a = autorRepository.getEntity(cursor.getInt(5));
                cursor_a.moveToNext();
                Autor autor = new Autor(cursor_a.getInt(0),
                        cursor_a.getString(1), cursor_a.getInt(2),
                        cursor_a.getString(3));
                row.setAutor(autor);

                knjige.add(row);
            }
            knjigeListAdapter = new KnjigeListAdapter(getActivity(), knjige);
            setListAdapter(knjigeListAdapter);
        }
        knjigeRepository.DBCLose();
    }

}