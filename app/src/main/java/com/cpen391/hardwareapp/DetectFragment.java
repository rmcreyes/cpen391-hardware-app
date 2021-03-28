package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class DetectFragment extends btFragment {
    private View v;
    private String plateNo;
    Bundle bundle = new Bundle();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_detect, container, false);

        plateNo = getArguments().getString("plateNo");
        bundle.putString("plateNo", plateNo);
        TextView plateNoText = v.findViewById(R.id.PlateNumber);
        plateNoText.setText(plateNo);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* User confirms the plate number is correct, send confirmation to DE1 */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        /* Plate number is incorrect, user will manually modify the plate number */
        Button modifyBtn = v.findViewById(R.id.noBtn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_detectFragment_to_plateFragment, bundle);
            }
        });
    }

    /**
     * Send confirmation to DE1 that plate number is correct
     */
    private void confirm (){
        String message = "TRUE," + plateNo;
        MainActivity.btWrite(message);
        return;
    }

    /**
     * Received Bluetooth message from DE1
     * We are expecting a string in the format "OK,USER,ABCABC" or "OK,NOTUSER,ABCABC"
     * ABCABC - is the plate number
     * USER - means that the plate is connected to a registered account (don't need to enter payment info)
     * NOTUSER - means that no account exists, and user needs to enter payment info
     *
     * Messages in any other formats are ignored
     */
    @Override
    public void readBtData(String msg) {
        String[] strArray = msg.split(",", 3);
        if (strArray[0].equals("OK")){
            final NavController navController = Navigation.findNavController(v);
            if(strArray[1].equals("USER")){
                navController.navigate(R.id.action_detectFragment_to_occupiedFragment, bundle);
            }else{
                navController.navigate(R.id.action_detectFragment_to_paymentFragment, bundle);
            }
        }
    }
}