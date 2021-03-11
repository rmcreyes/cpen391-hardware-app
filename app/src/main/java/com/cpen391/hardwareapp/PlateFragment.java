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

public class PlateFragment extends Fragment {
    private View v;
    private String plateNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_plate, container, false);

        plateNo = getArguments().getString("plateNo");
        if (plateNo == null){
            /* User manually started session*/
        }
        else{
            /* User wants to modify the detected plate number */
            EditText plateNoText = v.findViewById(R.id.PlateNumber);
            plateNoText.setText(plateNo);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        EditText plateNoText = v.findViewById(R.id.PlateNumber);
        /* Navigation to adding a car to the account */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("plateNo", plateNoText.getText().toString());
                if(sendPlateNo(plateNoText.getText().toString())){
                    navController.navigate(R.id.action_plateFragment_to_occupiedFragment, bundle);
                }
                else{
                    navController.navigate(R.id.action_plateFragment_to_paymentFragment, bundle);
                }
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