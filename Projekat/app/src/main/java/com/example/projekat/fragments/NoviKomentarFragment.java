package com.example.projekat.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.database.KomentarRepository;

import java.util.ArrayList;

public class NoviKomentarFragment extends Fragment {
    EditText imeIPrezime;
    EditText komentar;
    int knjigaID;
    Spinner knjigaSpinner;
    private KomentarRepository komentarRepository;
    private KnjigeRepository knjigeRepository;
    public NoviKomentarFragment() {
        // Required empty public constructor
    }

    public static NoviKomentarFragment newInstance() {
        return new NoviKomentarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knjigeRepository = new KnjigeRepository(getContext());
        komentarRepository = new KomentarRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_novi_komentar, container, false);
    }

    @SuppressLint("Range")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imeIPrezime = view.findViewById(R.id.new_ime_i_prezime);
        komentar = view.findViewById(R.id.new_komentar);
        knjigaSpinner = view.findViewById(R.id.new_naslov);
        Cursor cursor = knjigeRepository.getData(new String[]{DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_NASLOV}, null, null, null);
        ArrayList<String> naslovi = new ArrayList<>();
        while (cursor.moveToNext()){
            naslovi.add(cursor.getString(cursor.getColumnIndex("naslov")));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, naslovi);
        // Specify the layout to use when the list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        knjigaSpinner.setAdapter(arrayAdapter);
        knjigaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                knjigaID = cursor.getInt(cursor.getColumnIndex("_id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        Button potvrdi = view.findViewById(R.id.potvrdi);
        potvrdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajNoviKomentar();
            }
        });
    }

    private void dodajNoviKomentar() {

        komentarRepository.insertData(imeIPrezime.getText().toString(),
                komentar.getText().toString(), knjigaID);
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}