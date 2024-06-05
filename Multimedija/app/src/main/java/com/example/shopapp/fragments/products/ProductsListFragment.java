package com.example.shopapp.fragments.products;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.shopapp.R;
import com.example.shopapp.activities.HomeActivity;
import com.example.shopapp.adapters.ProductListAdapter;
import com.example.shopapp.database.DBContentProvider;
import com.example.shopapp.database.SQLiteHelper;
import com.example.shopapp.databinding.FragmentProductsListBinding;
import com.example.shopapp.model.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ProductsListFragment extends ListFragment implements SensorEventListener {
    private ProductListAdapter adapter;
    private ArrayList<Product> products = null;
    private FragmentProductsListBinding binding;
    private MenuProvider menuProvider;
    private SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 1000;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private SharedPreferences sharedPreferences;
    public static ProductsListFragment newInstance(){
        return new ProductsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Products List Fragment");
        binding = FragmentProductsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = requireActivity().getSharedPreferences("pref_file", Context.MODE_PRIVATE);

        addMenu();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ShopApp", "onCreate Products List Fragment");
        this.getListView().setDividerHeight(2);
        fillData();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void addMenu()
    {
        menuProvider = new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater)
            {
                menu.clear();
                menuInflater.inflate(R.menu.products_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                // Nakon toga, koristi se NavigationUI.onNavDestinationSelected(item, navController)
                // kako bi se omogućila integracija između MenuItem-a i odredišta unutar aplikacije
                // definisanih unutar navigacionog grafa (NavGraph).
                // Ova funkcija proverava da li je odabrana stavka izbornika povezana s nekim
                // odredištem unutar navigacionog grafa i pokreće tu navigaciju ako postoji
                // odgovarajuće podudaranje.
                return NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
        };

        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Handle the click on item at 'position'
    }
    private void fillData() {
        String[] projection = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TITLE, SQLiteHelper.COLUMN_DESCRIPTION, SQLiteHelper.COLUMN_IMAGE };
        Cursor cursor = requireActivity().getContentResolver()
                .query(DBContentProvider.CONTENT_URI_PRODUCTS, projection, null, null, null);

        products = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Product row = new Product();
                row.setId(cursor.getLong(0));
                row.setTitle(cursor.getString(1));
                row.setDescription(cursor.getString(2));
                row.setImage(cursor.getString(3));

                products.add(row);
            }
            if (sharedPreferences.contains("sort_type")) {
                Log.i("REZ", "SORT = " + sharedPreferences.getString("sort_type", "ascending"));

                if (sharedPreferences.getString("sort_type", "ascending").equals("ascending")) {
                    products = (ArrayList<Product>) products.stream()
                            .sorted(Comparator.comparing(Product::getTitle, Comparator.naturalOrder()))
                            .collect(Collectors.toList());
                }
                else {
                    products = (ArrayList<Product>) products.stream()
                            .sorted(Comparator.comparing(Product::getTitle, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
                }
            }
            adapter = new ProductListAdapter(getActivity(), products);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Spinner spinner = requireActivity().findViewById(R.id.btnSort);

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 200) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD && spinner != null) {
                    if (products != null){
                        ArrayList<Product> newList = new ArrayList<>();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (sharedPreferences.contains("sort_type")) {
                            if (sharedPreferences.getString("sort_type", "ascending").equals("ascending")){
                                sortList(newList, Comparator.reverseOrder());
                                editor.putString("sort_type", "descending");
                                spinner.setSelection(1);
                            } else {
                                sortList(newList, Comparator.naturalOrder());
                                editor.putString("sort_type", "ascending");
                                spinner.setSelection(0);
                            }
                            editor.apply();
                            Log.i("REZ", "SORT = " + sharedPreferences.getString("sort_type", "ascending"));
                        } else {
                            sortList(newList, Comparator.naturalOrder());
                            editor.putString("sort_type", "ascending");
                            spinner.setSelection(0);
                            editor.apply();
                        }
                        products.clear();
                        products.addAll(newList);
                        adapter.notifyDataSetChanged();
                        Log.d("REZ", "shake detected w/ speed: " + speed);
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.i("REZ_ACCELEROMETER", String.valueOf(accuracy));
        }
    }

    public void sortList(ArrayList<Product> newList, Comparator<? super String> keyComparator){
        newList.addAll(products.stream()
                .sorted(Comparator.comparing(Product::getTitle, keyComparator))
                .collect(Collectors.toList()));
    }
}
