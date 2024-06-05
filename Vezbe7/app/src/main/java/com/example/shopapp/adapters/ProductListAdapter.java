package com.example.shopapp.adapters;



import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.shopapp.activities.HomeActivity;
import com.example.shopapp.clients.ClientUtils;
import com.example.shopapp.fragments.FragmentTransition;
import com.example.shopapp.fragments.products.DetailProduct;
import com.example.shopapp.fragments.products.ProductsPageFragment;
import com.example.shopapp.model.Product;

import com.example.shopapp.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Adapteri unutar Android-a sluze da prikazu unapred nedefinisanu kolicinu podataka
 * pristigle sa interneta ili ucitane iz baze ili filesystem-a uredjaja.
 * Da bi napravili adapter treba da napraivmo klasu, koja nasledjuje neki od postojecih adaptera.
 * Za potrebe ovih vezbi koristicemo ArrayAdapter koji kao izvor podataka iskoristi listu ili niz.
 * Nasledjivanjem bilo kog adaptera, dobicemo
 * nekolko metoda koje moramo da referinisemo da bi adapter ispravno radio.
 * */
public class ProductListAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> aProducts;
    private Activity activity;
    private FragmentManager fragmentManager;

    public ProductListAdapter(Activity context, FragmentManager fragmentManager, ArrayList<Product> products){
        super(context, R.layout.product_card, products);
        aProducts = products;
        activity = context;
        fragmentManager = fragmentManager;
    }
    /*
     * Ova metoda vraca ukupan broj elemenata u listi koje treba prikazati
     * */
    @Override
    public int getCount() {
        return aProducts.size();
    }

    /*
     * Ova metoda vraca pojedinacan element na osnovu pozicije
     * */
    @Nullable
    @Override
    public Product getItem(int position) {
        return aProducts.get(position);
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

    /*
     * Ova metoda popunjava pojedinacan element ListView-a podacima.
     * Ako adapter cuva listu od n elemenata, adapter ce u petlji ici
     * onoliko puta koliko getCount() vrati. Prilikom svake iteracije
     * uzece java objekat sa odredjene poziciuje (model) koji cuva podatke,
     * i layout koji treba da prikaze te podatke (view) npr R.layout.product_card.
     * Kada adapter ima model i view, prosto ce uzeti podatke iz modela,
     * popuniti view podacima i poslati listview da prikaze, i nastavice
     * sledecu iteraciju.
     * */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_card,
                    parent, false);
        }
        LinearLayout productCard = convertView.findViewById(R.id.product_card_item);
        ImageView imageView = convertView.findViewById(R.id.product_image);
        TextView productTitle = convertView.findViewById(R.id.product_title);
        TextView productDescription = convertView.findViewById(R.id.product_description);
        ImageButton imageButton = convertView.findViewById(R.id.imgButton);

        if(product != null){
            String uri = "@drawable/" + product.getImagePath();
            Resources resources = getContext().getResources();
            final int resourceId = resources.getIdentifier(uri, "drawable", getContext().getPackageName());
            imageView.setImageResource(resourceId);

            productTitle.setText(product.getTitle());
            productDescription.setText(product.getDescription());
            productCard.setOnClickListener(v -> {
                // Handle click on the item at 'position'
                Log.i("ShopApp", "Clicked: " + product.getTitle() + ", id: " +
                        product.getId().toString());
                Bundle args = new Bundle();
                args.putLong("id", product.getId());
                args.putString("title", product.getTitle());
                args.putString("description", product.getDescription());
                args.putString("image", product.getImagePath());
                NavController navController = Navigation.findNavController(activity, R.id.fragment_nav_content_main);
                navController.navigate(R.id.nav_product_detail, args);


//                Call<Product> call = ClientUtils.productService.getById(product.getId());
//                call.enqueue(new Callback<Product>() {
//                    @Override
//                    public void onResponse(Call<Product> call, Response<Product> response) {
//                        if (response.code() == 200){
//                            Log.d("ShopApp","GET PRODUCT BY ID " + product.getId());
//                            Log.d("ShopApp", response.body().toString());
//
//                            Product detailProduct = response.body();
//                            // Start the DetailFragment and pass data using Bundle
//                            Bundle args = new Bundle();
//                            args.putLong("id", detailProduct.getId());
//                            args.putString("title", detailProduct.getTitle());
//                            args.putString("description", detailProduct.getDescription());
//                            args.putString("image", detailProduct.getImagePath());
//                            NavController navController = Navigation.findNavController(activity, R.id.fragment_nav_content_main);
//                            navController.navigate(R.id.nav_product_detail, args);
//                        }else{
//                            Log.d("REZ","Meesage recieved: "+response.code());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Product> call, Throwable t) {
//                        Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
//                    }
//                });




            });
            imageButton.setOnClickListener(v -> {
                Log.i("ShopApp", "Dodaj u korpu " + product.getId());
            });
        }

        return convertView;
    }
}
