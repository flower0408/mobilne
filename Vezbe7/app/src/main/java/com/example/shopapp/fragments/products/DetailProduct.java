package com.example.shopapp.fragments.products;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopapp.R;
import com.example.shopapp.adapters.ProductListAdapter;
import com.example.shopapp.clients.ClientUtils;
import com.example.shopapp.model.Product;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProduct extends Fragment {
    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE = "image";

    private Product product = new Product();

    private TextView id;
    private TextView title;
    private TextView description;
    private ImageView imageView;
    private ImageButton deleteProduct;
    private ImageButton editProduct;

    public DetailProduct() {
        // Required empty public constructor
    }

    public static DetailProduct newInstance(Long id, String title, String descriptiion, String image) {
        DetailProduct fragment = new DetailProduct();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, descriptiion);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product.setId(getArguments().getLong(ARG_ID));
            product.setTitle(getArguments().getString(ARG_TITLE));
            product.setDescription(getArguments().getString(ARG_DESCRIPTION));
            product.setImagePath(getArguments().getString(ARG_IMAGE));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        naknadno dobavi resurs
        if(product.getId() != null){
            Call<Product> call = ClientUtils.productService.getById(product.getId());
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.code() == 200){
                        Log.d("ShopApp","GET BY ID");
                        product = response.body();
                        title.setText(product.getTitle());
                        description.setText(product.getDescription());
                        String uri = "@drawable/" + product.getImagePath();
                        Resources resources = getContext().getResources();
                        final int resourceId = resources.getIdentifier(uri, "drawable", getContext().getPackageName());
                        imageView.setImageResource(resourceId);

                    }else{
                        Log.d("ShopApp","Meesage recieved: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Log.d("ShopApp", t.getMessage() != null?t.getMessage():"error");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("ShopApp", "DetailProduct onCreateView()");
        View view = inflater.inflate(R.layout.fragment_detail_product, container, false);

        id = view.findViewById(R.id.product_id);
        id.setText(product.getId().toString());

        title = view.findViewById(R.id.product_detail_title);
        title.setText(product.getTitle());

        description = view.findViewById(R.id.product_description);
        description.setText(product.getDescription());

        imageView = view.findViewById(R.id.product_image);
        String uri = "@drawable/" + product.getImagePath();
        Resources resources = getContext().getResources();
        final int resourceId = resources.getIdentifier(uri, "drawable", getContext().getPackageName());
        imageView.setImageResource(resourceId);

        deleteProduct = view.findViewById(R.id.deleteProductButton);
        deleteProduct.setOnClickListener(v -> {
            Log.d("ShopApp", "DELETE PRODUCT WITH ID " + product.getId().toString());
            Call<ResponseBody> call = ClientUtils.productService.deleteById(product.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200){
                        Log.d("ShopApp","Meesage recieved");
                        System.out.println(response.body());
                        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_nav_content_main);
                        navController.navigate(R.id.nav_products);
                    }else{
                        Log.d("ShopApp","Meesage recieved: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("ShopApp", t.getMessage() != null?t.getMessage():"error");
                }
            });
        });

        editProduct = view.findViewById(R.id.editProductButton);
        editProduct.setOnClickListener(v -> {
            Log.d("ShopApp", "EDIT PRODUCT WITH ID " + product.getId().toString());
            Bundle args = new Bundle();
            args.putLong("id", product.getId());
            args.putString("title", product.getTitle());
            args.putString("description", product.getDescription());
            args.putString("image", product.getImagePath());
            NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_nav_content_main);
            navController.navigate(R.id.nav_product_edit, args);
        });
        return view;
    }

}