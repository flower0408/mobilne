package com.example.shopapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.shopapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private BroadcastReceiver receiver;
    private  PreferenceManager prefMgr;

    private static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(getString(R.string.pref_file));
        prefMgr.setSharedPreferencesMode(MODE_PRIVATE);

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sp = prefMgr.getSharedPreferences();
                assert sp != null;
                SharedPreferences.Editor sp_editor = sp.edit();

                boolean playTone = sp.getBoolean("play_tone", false);
                Log.i("PLAY_TONE_STATUS", String.valueOf(playTone));
                if (playTone) {
                    sp_editor.putBoolean("play_tone", false);
                    sp_editor.apply();
                    sp_editor.commit();
                    setPlayTonePreference(false);
                }
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, new IntentFilter("update-play-tone-action"));
    }

    public void setPlayTonePreference(boolean value){
        SwitchPreference playTonePref = findPreference("play_tone");
        if (playTonePref != null) {
            playTonePref.setChecked(value);
        }
    }
}
