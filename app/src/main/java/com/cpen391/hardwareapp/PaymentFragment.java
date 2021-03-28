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
import android.widget.Toast;

public class PaymentFragment extends btFragment {
    private View v;
    private String plateNo;
    private Bundle bundle = new Bundle();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_payment, container, false);
        plateNo = getArguments().getString("plateNo");
        bundle.putString("plateNo", plateNo);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Send payment info to de1*/
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText cardEdit = v.findViewById(R.id.CardEdit);
                EditText expDateEdit = v.findViewById(R.id.expDateEdit);
                EditText cvvEdit = v.findViewById(R.id.cvvEdit);
                EditText countryEdit = v.findViewById(R.id.countryEdit);

                sendPayment(cardEdit.getText().toString(),expDateEdit.getText().toString(),cvvEdit.getText().toString(),countryEdit.getText().toString());
            }
        });
    }

    /**
     * send payment information to DE1
     * string format = "cardNum,expDate,cvv,country,plateNo"
     */
    private boolean sendPayment (String cardNum, String expDate, String cvv, String country){

        /* Check if any field are empty */
        if(cardNum == null || expDate == null|| cvv == null ||country == null){
            Toast.makeText(getContext(), "Payment information cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            String message = "PAYMENT," + cardNum + "," + expDate + "," + cvv + "," + country + "," + plateNo;
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
        if (strArray[0].equals("OK") && strArray[1].equals("DONE")){
            final NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_paymentFragment_to_occupiedFragment, bundle);
        }
    }
}