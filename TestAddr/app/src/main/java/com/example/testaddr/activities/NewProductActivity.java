package com.example.testaddr.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testaddr.R;
import com.example.testaddr.clients.ClientUtils;
import com.example.testaddr.model.Product;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewProductActivity extends AppCompatActivity {

    private EditText titleText;
    private EditText descrText;
    private Button confirmButton;
    private Product newProduct;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_product);

        confirmButton = findViewById(R.id.add_new_pr);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Product> call;
                Log.d("ShopApp", "Add product call");
                titleText = findViewById(R.id.new_title);
                descrText = findViewById(R.id.new_descr);
                addNewProduct();
                call = ClientUtils.productService.add(newProduct);

                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                        if (response.code() == 200){
                            Log.d("REZ","Meesage recieved");
                            Product product1 = response.body();
                            Log.d("REZ", String.valueOf(product1));

                        }else{
                            Log.d("REZ","Meesage recieved: "+response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                        Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
                    }
                });
                finish();
            }
        });
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
        newProduct.setId(4L);
    }
}