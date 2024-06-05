package com.example.shopapp.fragments.new_product;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.shopapp.adapters.ProductListAdapter;
import com.example.shopapp.clients.ClientUtils;
import com.example.shopapp.databinding.FragmentNewProductBinding;
import com.example.shopapp.fragments.products.DetailProduct;
import com.example.shopapp.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewProductFragment extends Fragment {

    private NewProductViewModel mViewModel;
    private FragmentNewProductBinding binding;
    private EditText titleText;
    private EditText descrText;
    private Button confirmButton;

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE = "image";

    private Product editProduct;
    private Product newProduct;

    public static NewProductFragment newInstance(Long id, String title, String descriptiion, String image) {
        NewProductFragment fragment = new NewProductFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, descriptiion);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(NewProductViewModel.class);

        binding = FragmentNewProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editProduct = new Product();
            editProduct.setId(getArguments().getLong(ARG_ID));
            editProduct.setTitle(getArguments().getString(ARG_TITLE));
            editProduct.setDescription(getArguments().getString(ARG_DESCRIPTION));
            editProduct.setImagePath(getArguments().getString(ARG_IMAGE));
        }
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NewProductViewModel.class);
        titleText = binding.newTitle;
        descrText = binding.newDescr;
        if(editProduct != null){
            titleText.setText(editProduct.getTitle());
            descrText.setText(editProduct.getDescription());
        }

        confirmButton = binding.addNewPr;
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Product> call;
                if(editProduct != null){
                    Log.d("ShopApp", "Edit product call");
                    editProduct();
                    call = ClientUtils.productService.edit(editProduct);
                }else{
                    Log.d("ShopApp", "Add product call");
                    addNewProduct();
                    call = ClientUtils.productService.add(newProduct);
                }
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.code() == 200){
                            Log.d("REZ","Meesage recieved");
                            System.out.println(response.body());
                            Product product1 = response.body();
                            System.out.println(product1);
                            getActivity().getSupportFragmentManager().popBackStack();
                        }else{
                            Log.d("REZ","Meesage recieved: "+response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
                    }
                });

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void editProduct(){
        String title = titleText.getText().toString();
        String description = descrText.getText().toString();
        if (title.length() == 0 && description.length() == 0) {
            return;
        }
        editProduct.setTitle(title);
        editProduct.setDescription(description);

    }

    private void addNewProduct() {
        String title = titleText.getText().toString();
        String description = descrText.getText().toString();

        if (title.length() == 0 && description.length() == 0) {
            return;
        }
        newProduct = new Product();
        newProduct.setTitle(title);
        newProduct.setDescription(description);
        newProduct.setImagePath("samsung_galaxy_s23_ultra");
        newProduct.setId(4L);
    }


}