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
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetectFragment extends Fragment {
    private View v;
    private String plateNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_detect, container, false);

        plateNo = getArguments().getString("plateNo");
        TextView plateNoText = v.findViewById(R.id.PlateNumber);
        plateNoText.setText(plateNo);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Navigation to adding a car to the account */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("plateNo", plateNo);
                if(sendPlateNo(plateNo)){
                    navController.navigate(R.id.action_detectFragment_to_occupiedFragment, bundle);
                }
                else{
                    navController.navigate(R.id.action_detectFragment_to_paymentFragment, bundle);
                }
            }
        });

        /* Navigation to adding a car to the account */
        Button modifyBtn = v.findViewById(R.id.noBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("plateNo", plateNo);
                navController.navigate(R.id.action_detectFragment_to_plateFragment, bundle);
            }
        });
    }

    /**
     * TODO: Implement function to send entered plate number to DE1
     * @return returns true if account exists
     */
    private Boolean sendPlateNo (String plateNo){
        return false;
    }
}