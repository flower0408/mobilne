package com.example.projekat.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.projekat.R;
import com.example.projekat.Utils.DatabaseConstants;
import com.example.projekat.database.AutorRepository;
import com.example.projekat.database.KnjigeRepository;
import com.example.projekat.models.Knjiga;

import java.util.ArrayList;


public class IzmenaKnjigeFragment extends Fragment {

    private Knjiga selectedKnjiga;

    private EditText naslovEditText;
    private EditText brStranicaEditText;
    private EditText povezEditText;
    private Spinner zanrSpinner;
    private Spinner autorSpinner;
    private int autorID;
    private KnjigeRepository knjigeRepository;
    private AutorRepository autorRepository;

    public IzmenaKnjigeFragment() {
        // Required empty public constructor
    }

    public void setSelectedKnjiga(Knjiga knjiga) {
        selectedKnjiga = knjiga;
    }

    public static IzmenaKnjigeFragment newInstance(Knjiga knjiga) {
        IzmenaKnjigeFragment fragment = new IzmenaKnjigeFragment();
        Bundle args = new Bundle();
        args.putSerializable("knjiga", knjiga);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedKnjiga = (Knjiga) getArguments().getSerializable("knjiga");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_izmena_knjige, container, false);

        naslovEditText = view.findViewById(R.id.new_naslov);
        brStranicaEditText = view.findViewById(R.id.new_brStranica);
        povezEditText = view.findViewById(R.id.new_povez);
        zanrSpinner = view.findViewById(R.id.new_zanr);
        autorSpinner = view.findViewById(R.id.new_autor);

        // Initialize repositories
        knjigeRepository = new KnjigeRepository(requireContext());
        autorRepository = new AutorRepository(requireContext());

        // Load values into the genre spinner
        ArrayList<String> zanrValues = new ArrayList<>();
        zanrValues.add("ROMAN");
        zanrValues.add("FANTASTIKA");
        zanrValues.add("AKCIONI");
        zanrValues.add("BIOGRAFIJA");
        zanrValues.add("FILOZOFIJA");

        ArrayAdapter<String> zanrAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, zanrValues);
        zanrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zanrSpinner.setAdapter(zanrAdapter);

        // Populate author spinner
        populateAutorSpinner();

        // Pre-fill form fields if a book is selected
        if (selectedKnjiga != null) {
            naslovEditText.setText(selectedKnjiga.getNaslov());
            brStranicaEditText.setText(String.valueOf(selectedKnjiga.getBrStranica()));
            povezEditText.setText(selectedKnjiga.getPovez());
            zanrSpinner.setSelection(getIndex(zanrSpinner, selectedKnjiga.getZanr().toString()));
            autorSpinner.setSelection(getIndex(autorSpinner, selectedKnjiga.getAutor().getImeIPrezime()));
        }

        Button potvrdiButton = view.findViewById(R.id.potvrdi);
        potvrdiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateKnjiga(); // Call the method to update the book
                requireActivity().getSupportFragmentManager().popBackStack(); // Go back to the previous fragment
            }
        });

        return view;
    }

    private void populateAutorSpinner() {
        String[] projection = {DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME};
        Cursor cursor = autorRepository.getData(projection);
        ArrayList<String> imena = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int imeIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_IME_I_PREZIME);
                if (imeIndex != -1) {
                    imena.add(cursor.getString(imeIndex));
                } else {
                    // Log a warning if the column index is -1
                    Log.w("Column Index Error", "Column not found: " + DatabaseConstants.COLUMN_IME_I_PREZIME);
                }
            }
            cursor.close(); // Close the cursor after reading data
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, imena);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autorSpinner.setAdapter(arrayAdapter);
        autorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // No need to access the cursor here, as the data is already read
                autorID = position; // Assuming position represents the author ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    private void updateKnjiga() {
        String naslov = naslovEditText.getText().toString();
        int brStranica = Integer.parseInt(brStranicaEditText.getText().toString());
        String povez = povezEditText.getText().toString();
        String zanr = zanrSpinner.getSelectedItem().toString();

        // Retrieve the selected author name from autorSpinner
        String autorName = autorSpinner.getSelectedItem().toString();
        // Retrieve the autor ID based on the selected autor's name
        int autorID = getAutorIDByName(autorName);

        // Update the knjiga using the retrieved autor ID
        knjigeRepository.updateData(selectedKnjiga.getID(), naslov, brStranica, povez, zanr, autorID);
    }

    // Helper method to get the autor ID by autor name
    private int getAutorIDByName(String autorName) {
        String[] projection = {DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME};
        Cursor cursor = autorRepository.getData(projection);
        int autorID = -1; // Default value if autor ID is not found

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_IME_I_PREZIME);
            int idIndex = cursor.getColumnIndex(DatabaseConstants.COLUMN_ID);

            if (nameIndex != -1 && idIndex != -1) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    if (name.equals(autorName)) {
                        autorID = cursor.getInt(idIndex);
                        break;
                    }
                }
            } else {
                Log.e("Column Index Error", "Column not found: " + DatabaseConstants.COLUMN_IME_I_PREZIME
                        + " or " + DatabaseConstants.COLUMN_ID);
            }

            cursor.close(); // Close the cursor after reading data
        } else {
            Log.e("Cursor Error", "Cursor is null");
        }
        return autorID;
    }


    // Helper method to get the index of a specific item in a spinner
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

}