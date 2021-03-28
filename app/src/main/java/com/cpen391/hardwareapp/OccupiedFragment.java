package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OccupiedFragment extends btFragment {
    private View v;
    private String plateNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_occupied, container, false);
        plateNo = getArguments().getString("plateNo");

        TextView plateNoText = v.findViewById(R.id.PlateNumber);
        plateNoText.setText(plateNo);

        return v;
    }


    /**
     * Received data from the DE1
     * We are expecting a string in the format "END,ABCABC" where ABCABC is the plate number
     *
     * Messages in any other formats are ignored
     */
    @Override
    public void readBtData(String msg) {
        String[] strArray = msg.split(",", 2);
        if (strArray[0].equals("END")){
            final NavController navController = Navigation.findNavController(v);
            if(strArray[1].equals(plateNo)) {
                navController.navigate(R.id.action_occupiedFragment_to_initialFragment);
            }
        }
    }
}