package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InitialFragment extends btFragment{
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_initial, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);
    }

    /**
     * Bluetooth connection received a message from the DE1
     * We are expecting a string in the format "CONFIRM,ABCABC" where ABCABC is the detected plate number
     * Any messages in other formats are ignored.
     */
    @Override
    public void readBtData(String msg) {
        String[] strArray = msg.split(",", 2);
        if (strArray[0].equals(Constants.CONFIRM)){
            navigateToDetectView(strArray[1]);
        }
    }

    /**
     * Navigate to the Detected Fragment after DE1 detects a new parking session
     * @param plateNo - plate number parsed from DE1 message
     */
    private void navigateToDetectView (String plateNo){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.plateNo, plateNo);
        final NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_initialFragment_to_detectFragment, bundle);
    }
}