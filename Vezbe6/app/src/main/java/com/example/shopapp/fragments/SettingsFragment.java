package com.example.shopapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shopapp.databinding.FragmentSettingsBinding;
import com.example.shopapp.services.ForegroundService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private BroadcastReceiver br;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentSettingsBinding.inflate(inflater, container, false);
        }
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("KKKKK", "K");
                if (intent.getAction().equals("STOP_MUSIC")){
                    binding.switch1.setChecked(false);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("STOP_MUSIC");
        requireActivity().registerReceiver(br, filter, Context.RECEIVER_EXPORTED);
        View root = binding.getRoot();
        if (binding != null) {
            SwitchMaterial startServiceButton = binding.switch1;
            startServiceButton.setOnCheckedChangeListener((compoundButton, b) -> {
                Intent intent = new Intent(getActivity(), ForegroundService.class);
                intent.setAction(ForegroundService.ACTION_START_FOREGROUND_SERVICE);
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireActivity().startForegroundService(intent);
                    } else {
                        requireActivity().startService(intent);
                    }
                } else {
                    Intent intent1 = new Intent(getActivity(), ForegroundService.class);
                    intent.setAction(ForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                    requireActivity().stopService(intent1);
                }
            });
        }
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
