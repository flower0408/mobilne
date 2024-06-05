package com.example.shopapp.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopapp.R;
import com.example.shopapp.databinding.FragmentProductsListBinding;
import com.example.shopapp.databinding.FragmentProfileBinding;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private boolean isPermissions = true;
    private TextView contactView;
    private String permission = android.Manifest.permission.READ_CONTACTS;
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<String> mPermissionResult;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addMenu();
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactView = (TextView) binding.contactview;
        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        startProcessingContacts();
                    } else {
                        Toast.makeText(getActivity(), "No permission.", Toast.LENGTH_SHORT).show();
                    }
                });
        processContact();

    }
    private void processContact() {
        int getContacts = ContextCompat.checkSelfPermission(requireContext(), permission);
        if (getContacts != PackageManager.PERMISSION_GRANTED) {
            mPermissionResult.launch(permission);
        } else {
            startProcessingContacts();
        }
    }

    private void startProcessingContacts() {
        Cursor cursor = getContacts();
        Log.i("ShopApp", "GOT CONTACTS");
        contactView.append("Contacts:\n\n");
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            do {
                @SuppressLint("Range") String displayName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                if (displayName != null) {
                    Log.i("ShopApp", "IN CONTACTS");
                    contactView.append("Contact Name: ");
                    contactView.append(displayName);
                    contactView.append("\n");
                }
            } while (cursor.moveToNext());
        }
    }

    private void addMenu()
    {
        MenuProvider menuProvider = new MenuProvider()
        {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater)
            {
                menu.clear();
                menuInflater.inflate(R.menu.toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                return NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
        };

        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    private Cursor getContacts() {
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        Cursor cursor = requireActivity().getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,null, null, null);
        return cursor;
    }
}