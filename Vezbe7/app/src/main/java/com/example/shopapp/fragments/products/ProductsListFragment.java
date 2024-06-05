package com.example.shopapp.fragments.products;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.shopapp.R;
import com.example.shopapp.adapters.ProductListAdapter;
import com.example.shopapp.clients.ClientUtils;
import com.example.shopapp.databinding.FragmentProductsListBinding;
import com.example.shopapp.model.Product;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsListFragment extends ListFragment{

    private ProductListAdapter adapter;
    private FragmentProductsListBinding binding;
    private MenuProvider menuProvider;
    private ArrayList<Product> products = new ArrayList<>();


    public static ProductsListFragment newInstance(){
        ProductsListFragment fragment = new ProductsListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Products List Fragment");
        binding = FragmentProductsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addMenu();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ShopApp", "onCreate Products List Fragment");
        this.getListView().setDividerHeight(2);
        getDataFromClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromClient();
    }

    private void addMenu() {
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.products_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_nav_content_main);
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
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void getDataFromClient(){
        /*
         * Poziv REST servisa se odvija u pozadini i mi ne moramo da vodimo racuna o tome
         * Samo je potrebno da registrujemo sta da se desi kada odgovor stigne od nas
         * Taj deo treba da implementiramo dodavajuci Callback<List<Event>> unutar enqueue metode
         *
         * Servis koji pozivamo izgleda:
         * http://<service_ip_adress>:<service_port>/api/product
         * */
        Call<ArrayList<Product>> call = ClientUtils.productService.getAll();
        call.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.code() == 200){
                    Log.d("REZ","Meesage recieved");
                    System.out.println(response.body());
                    products = response.body();
                    adapter = new ProductListAdapter(getActivity(), getActivity().getSupportFragmentManager(), products);
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{
                    Log.d("REZ","Meesage recieved: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
            }
        });

    }

}
