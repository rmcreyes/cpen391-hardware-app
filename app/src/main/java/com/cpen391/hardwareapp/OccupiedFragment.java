package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OccupiedFragment extends Fragment {
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
     * TODO: Wait for DE1 to notify that parking session ended
     */
    private void endSession(){
        final NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_occupiedFragment_to_initialFragment);
    }
}