package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InitialFragment extends Fragment {
    private View v;
    private String plateNo;


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
        /* User Manually starting parking instance */
        Button enterBtn = v.findViewById(R.id.EnterBtn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_initialFragment_to_plateFragment);
            }
        });
    }

    /**
     * TODO: Implement bluetooth functionality
     */
    private void navigateToDetectView (){
        Bundle bundle = new Bundle();
        bundle.putString("plateNo", plateNo);
        final NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_initialFragment_to_detectFragment, bundle);
    }
}