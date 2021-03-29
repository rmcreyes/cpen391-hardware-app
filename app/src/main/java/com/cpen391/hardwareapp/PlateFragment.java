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
import android.widget.Toast;

public class PlateFragment extends btFragment {
    private View v;
    private String plateNo;
    private Bundle bundle = new Bundle();
    private Boolean detected = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_plate, container, false);

        plateNo = getArguments().getString("plateNo");
        if (plateNo == null){
            /* User manually started session*/
            detected = false;
        }
        else{
            /* User wants to modify the detected plate number */
            EditText plateNoText = v.findViewById(R.id.PlateNumber);
            plateNoText.setText(plateNo);
            detected = true;
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        EditText plateNoText = v.findViewById(R.id.PlateNumber);

        /* User confirms/submits the plate number */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plateNo = plateNoText.getText().toString().toUpperCase();

                /* check if entered plate number is valid */
                if(plateNo.length() != 6){
                    Toast.makeText(getContext(), "Invalid Entry: Please enter a valid 6 digit plate number", Toast.LENGTH_SHORT).show();
                }
                else {
                    bundle.putString("plateNo", plateNo);
                    sendPlateNo(plateNo);
                }
            }
        });
    }

    /**
     * send entered plate number to DE1
     * Message in the format of FALSE,ABCABC or NEW,ABCABC
     * where ABCABC is the manually entered plate number
     * FALSE - the plate number detected by DE1 is incorrect, user manually modified it
     * NEW - this is a parking session manually started by the user (DE1 should initiate the new parking session with isConfirmed = true)
     */
    private void sendPlateNo (String plateNo){
        String sendString;
        if (detected == true){
            sendString = "CONFIRM,FALSE," + plateNo;
        }else{
            sendString = "CONFIRM,NEW," + plateNo;
        }
        MainActivity.btWrite(sendString);
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
                navController.navigate(R.id.action_plateFragment_to_occupiedFragment, bundle);
            }else{
                navController.navigate(R.id.action_plateFragment_to_paymentFragment, bundle);
            }
        }
    }
}