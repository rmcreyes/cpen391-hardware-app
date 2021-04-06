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
import android.widget.TextView;
import android.widget.Toast;

public class PaymentFragment extends btFragment {
    private View v;
    private String plateNo;
    private Bundle bundle = new Bundle();
    private long timeLeftInMillis = 2*60*1000; // 2 minutes timeout for waiting for an confirmation
    private CountDownTimer countDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_payment, container, false);
        plateNo = getArguments().getString(Constants.plateNo);
        bundle.putString(Constants.plateNo, plateNo);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Send payment info to de1*/
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setEnabled(true);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel(); // stop the timer since user confirmed
                EditText cardEdit = v.findViewById(R.id.CardEdit);
                EditText expDateEdit = v.findViewById(R.id.expDateEdit);
                EditText cvvEdit = v.findViewById(R.id.cvvEdit);
                confirmBtn.setEnabled(false);

                sendPayment(cardEdit.getText().toString(),expDateEdit.getText().toString(),cvvEdit.getText().toString());
            }
        });
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
        String message = Constants.PAYMENT_TIMEOUT;
        MainActivity.btWrite(message);
        final NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_paymentFragment_to_initialFragment);
        return;
    }

    /**
     * send payment information to DE1
     * string format = "cardNum,expDate,cvv,plateNo"
     */
    private boolean sendPayment (String cardNum, String expDate, String cvv){

        /* Check if any field are empty */
        if(cardNum == null || expDate == null|| cvv == null ){
            Toast.makeText(getContext(), "Payment information cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            String message = Constants.PAYMENT + cardNum + "," + expDate + "," + cvv + "," + plateNo;
            MainActivity.btWrite(message);
            return true;
        }
    }

    /**
     * Received Bluetooth message from DE1
     * We are expecting a string in the format "OK,DONE,ABCABC"
     * ABCABC - is the plate number
     *
     * Messages in any other formats are currently ignored
     */
    @Override
    public void readBtData(String msg) {
        String[] strArray = msg.split(",", 3);
        if (strArray[0].equals(Constants.OK) && strArray[1].equals(Constants.DONE)){
            final NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_paymentFragment_to_occupiedFragment, bundle);
        }
    }
}