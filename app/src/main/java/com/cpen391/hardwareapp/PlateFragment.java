package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class PlateFragment extends btFragment {
    private View v;
    private String plateNo;
    private Bundle bundle = new Bundle();
    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_plate, container, false);

        plateNo = getArguments().getString(Constants.plateNo,"").trim();
        /* User wants to modify the detected plate number */
        EditText plateNoText = v.findViewById(R.id.PlateNumber);
        plateNoText.setText(plateNo);

        /* Continue the 2 minutes timeout for waiting for a confirmation */
        timeLeftInMillis = getArguments().getLong(Constants.counterTimerInMilli);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        EditText plateNoText = v.findViewById(R.id.PlateNumber);

        /* User confirms/submits the plate number */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setEnabled(true);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel(); // stop the timer since user confirmed
                plateNo = plateNoText.getText().toString().toUpperCase();
                Pattern p = Pattern.compile("[^A-Z0-9 ]");

                /* check if entered plate number is valid */
                if(p.matcher(plateNo).find()){
                    Toast.makeText(getContext(), "Invalid Entry: Please enter a valid plate number with only letters and spaces.", Toast.LENGTH_SHORT).show();
                }
                else {
                    bundle.putString(Constants.plateNo, plateNo);
                    confirmBtn.setEnabled(false);
                    sendPlateNo(plateNo);
                }
            }
        });

        /* Start a count down timer until app times out waiting for the user's input */
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timeout();
            }
        }.start();
    }


    /**
     * Timeout function since user did not respond
     * send timeout message to DE1
     * return back to initial screen
     */
    private void timeout (){
        String message = Constants.CONFIRM_TIMEOUT;
        MainActivity.btWrite(message);
        final NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_plateFragment_to_initialFragment);
        return;
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
        sendString = Constants.CONFIRM_FALSE + plateNo;
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
        if (strArray[0].equals(Constants.OK)){
            final NavController navController = Navigation.findNavController(v);
            if(strArray[1].equals(Constants.USER)){
                navController.navigate(R.id.action_plateFragment_to_occupiedFragment, bundle);
            }else{
                navController.navigate(R.id.action_plateFragment_to_paymentFragment, bundle);
            }
        }
    }
}