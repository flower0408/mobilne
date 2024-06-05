package com.example.shopapp.fragments.new_product;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopapp.database.DBContentProvider;
import com.example.shopapp.database.ProductRepository;
import com.example.shopapp.database.SQLiteHelper;
import com.example.shopapp.databinding.FragmentNewProductBinding;
import com.example.shopapp.tools.GenericFileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewProductFragment extends Fragment {

    private NewProductViewModel mViewModel;
    private FragmentNewProductBinding binding;
    private EditText titleText;
    private EditText descrText;
    private Button takePictureButton;
    private ImageView imageView;
    private Uri file;
    private ActivityResultLauncher<Intent> startActivityForResult;
    private ActivityResultLauncher<String[]> permissionsResult;
    private String [] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Button confirmButton;
    public static NewProductFragment newInstance() {
        return new NewProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(NewProductViewModel.class);
        binding = FragmentNewProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NewProductViewModel.class);
        titleText = binding.newTitle;
        descrText = binding.newDescr;
        confirmButton = binding.addNewPr;
        takePictureButton = binding.buttonImage;
        imageView = binding.imageview;

        permissionsResult =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            if (list.get(0) && list.get(1)) {
                                takePictureButton.setEnabled(true);
                            }
                            else {
                                takePictureButton.setEnabled(false);
                                Toast.makeText(getActivity(), "No permission.", Toast.LENGTH_SHORT).show();
                            }
                        });
        processPermission();

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        imageView.setImageURI(file);
                    }
                }
        );

        // TODO: Use the ViewModel
        confirmButton.setOnClickListener(view1 -> addNewProduct());
        takePictureButton.setOnClickListener(view12 -> takePicture());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void processPermission() {
        boolean permissionsGiven = true;
        for (int i = 0; i < permissions.length; i++){
            int perm = ContextCompat.checkSelfPermission(requireContext(), permissions[i]);
            if (perm != PackageManager.PERMISSION_GRANTED) {
                permissionsGiven = false;
            }
        }
        if (!permissionsGiven) {
            permissionsResult.launch(permissions);
        } else {
            takePictureButton.setEnabled(true);
        }

    }
    private void addNewProduct() {
        String title = titleText.getText().toString();
        String description = descrText.getText().toString();
        // only save if either summary or description
        // is available
        if (description.length() == 0 && title.length() == 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(SQLiteHelper.COLUMN_IMAGE, String.valueOf(file));
        requireActivity().getContentResolver().insert(
                    DBContentProvider.CONTENT_URI_PRODUCTS,
                    values);
//        ProductRepository productRepository = new ProductRepository(getContext());
//        productRepository.open();
//        productRepository.insertData(title, description, "image");
//        productRepository.close();

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ShopApp");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        Log.d("REZ_IMG", mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        file = FileProvider.getUriForFile(requireContext(), GenericFileProvider.MY_PROVIDER,
                Objects.requireNonNull(getOutputMediaFile()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult.launch(intent);
    }
}








