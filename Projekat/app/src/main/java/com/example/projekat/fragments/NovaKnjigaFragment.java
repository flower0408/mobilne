package com.example.projekat.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import com.example.projekat.database.AutorRepository;
import com.example.projekat.database.KnjigeRepository;

import java.util.ArrayList;

public class NovaKnjigaFragment extends Fragment {
    EditText naslov;
    EditText brStranica;
    EditText povez;
    Spinner zanrSpinner;
    Spinner autorSpinner;
    int autorID;
    private KnjigeRepository knjigeRepository;
    private AutorRepository autorRepository;

    public NovaKnjigaFragment() {
        // Required empty public constructor
    }
    public static NovaKnjigaFragment newInstance() {
        return new NovaKnjigaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knjigeRepository = new KnjigeRepository(getContext());
        autorRepository = new AutorRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nova_knjiga, container, false);

    }

    @SuppressLint("Range")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        naslov = view.findViewById(R.id.new_naslov);
        brStranica = view.findViewById(R.id.new_brStranica);
        Log.i("ASD", String.valueOf(brStranica));
        povez = view.findViewById(R.id.new_povez);
        zanrSpinner = view.findViewById(R.id.new_zanr);
        autorSpinner = view.findViewById(R.id.new_autor);

        // Load values into the genre spinner
        ArrayList<String> zanrValues = new ArrayList<>();
        zanrValues.add("ROMAN");
        zanrValues.add("FANTASTIKA");
        zanrValues.add("AKCIONI");
        zanrValues.add("BIOGRAFIJA");
        zanrValues.add("FILOZOFIJA");

        ArrayAdapter<String> zanrAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, zanrValues);
        zanrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zanrSpinner.setAdapter(zanrAdapter);

        String[] projection = { DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                DatabaseConstants.COLUMN_GODISTE, DatabaseConstants.COLUMN_MESTO_RODJENJA};
        Cursor autori = autorRepository.getData(projection);
        ArrayList<String> imena = new ArrayList<>();
        while (autori.moveToNext()){
            imena.add(autori.getString(autori.getColumnIndex("imeIPrezime")));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, imena);
        // Specify the layout to use when the list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        autorSpinner.setAdapter(arrayAdapter);
        autorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("ShopApp", String.valueOf(position));
                autori.moveToPosition(position);
                autorID = autori.getInt(autori.getColumnIndex("_id"));
                Log.v("ShopApp", position + " " + autorID);

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
                dodajNovuKnjigu();
            }
        });

    }


    private void dodajNovuKnjigu() {

        knjigeRepository.insertData(naslov.getText().toString(), Integer.parseInt(brStranica.getText().toString()),
                povez.getText().toString(), zanrSpinner.getSelectedItem().toString(), autorID);
//        ProductRepository productRepository = new ProductRepository(getContext());
//        productRepository.open();
//        productRepository.insertData(title, description, "image");
//        productRepository.close();

        requireActivity().getSupportFragmentManager().popBackStack();
    }
}