package com.example.shopapp.fragments.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopapp.databinding.FragmentAudioBinding;

import java.io.IOException;
import java.util.Objects;


public class AudioFragment extends Fragment  {
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start, stop, play, reset;
    private String permission = Manifest.permission.RECORD_AUDIO;
    private ActivityResultLauncher<String> mPermissionResult;
    private FragmentAudioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAudioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        setUpRecorder();
                    } else {
                        Toast.makeText(getActivity(), "No permission.", Toast.LENGTH_SHORT).show();
                    }
                });
        processPermission();
        return root;
    }
    private void setUpRecorder(){
        start = binding.button1;
        stop = binding.button2;
        play = binding.button3;
        reset = binding.button4;

        start.setOnClickListener(this::start);
        stop.setOnClickListener(this::stop);
        play.setOnClickListener(view -> {
            try {
                play(view);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reset.setOnClickListener(this::reset);

        stop.setEnabled(false);
        play.setEnabled(false);
        reset.setEnabled(false);
        outputFile = Objects.requireNonNull(requireActivity().getExternalCacheDir())
                .getAbsolutePath() + "/recording.3gp";
        Log.d("REZ_FILE", outputFile);
        myAudioRecorder = new MediaRecorder();
    }
    private void processPermission() {
        int getContacts = ContextCompat.checkSelfPermission(requireContext(), permission);
        if (getContacts != PackageManager.PERMISSION_GRANTED) {
            mPermissionResult.launch(permission);
        } else {
            setUpRecorder();
        }
    }

    public void start(View view) {
        try {
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            Log.d("REZ_RECORDING", "Start recording.");
        } catch (IllegalStateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        start.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    public void stop(View view) {
        myAudioRecorder.stop();
        Log.d("REZ_RECORDING", "Stop recording.");
        myAudioRecorder.release();
        myAudioRecorder = null;
        stop.setEnabled(false);
        play.setEnabled(true);
        reset.setEnabled(true);
        Toast.makeText(requireContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
    }

    public void play(View view) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException {
        MediaPlayer m = new MediaPlayer();
        m.setDataSource(outputFile);
        m.prepare();
        m.start();
        Log.d("REZ_RECORDING", "Play recording.");
        Toast.makeText(requireContext(), "Playing audio", Toast.LENGTH_LONG).show();

    }
    private void reset(View view) {
        stop.setEnabled(false);
        play.setEnabled(false);
        start.setEnabled(true);
        reset.setEnabled(false);
        myAudioRecorder = new MediaRecorder();
    }
}