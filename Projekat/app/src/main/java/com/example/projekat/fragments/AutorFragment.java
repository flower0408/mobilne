package com.example.projekat.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.adapters.AutorListAdapter;
import com.example.projekat.database.AutorRepository;
import com.example.projekat.models.Autor;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutorFragment extends ListFragment {
    private AutorRepository autorRepository;
    private AutorListAdapter autorListAdapter;

    public AutorFragment() {
        // Required empty public constructor
    }

    public static AutorFragment newInstance() {
        AutorFragment fragment = new AutorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autorRepository = new AutorRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_autor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAutori();
    }

    private void loadAutori() {
        String[] projection = { DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                DatabaseConstants.COLUMN_GODISTE, DatabaseConstants.COLUMN_MESTO_RODJENJA};
        Cursor cursor = autorRepository.getData(projection);

        ArrayList<Autor> autori = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Autor row = new Autor();
                row.setID(Integer.parseInt(cursor.getString(0)));
                row.setImeIPrezime(cursor.getString(1));
                row.setGodiste(cursor.getInt(2));
                row.setMestoRodjenja(cursor.getString(3));
                autori.add(row);
            }
            autorListAdapter = new AutorListAdapter(requireActivity(), autori);
            setListAdapter(autorListAdapter);
        }
        autorRepository.DBClose();
    }
}