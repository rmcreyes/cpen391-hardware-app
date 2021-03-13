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

public class PaymentFragment extends btFragment {
    private View v;
    private String plateNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_payment, container, false);
        plateNo = getArguments().getString("plateNo");

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(v);

        /* Navigation to adding a car to the account */
        Button confirmBtn = v.findViewById(R.id.ConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("plateNo", plateNo);

                EditText cardEdit = v.findViewById(R.id.CardEdit);
                EditText expDateEdit = v.findViewById(R.id.expDateEdit);
                EditText cvvEdit = v.findViewById(R.id.cvvEdit);
                EditText countryEdit = v.findViewById(R.id.countryEdit);

                sendPayment(cardEdit.getText().toString(),expDateEdit.getText().toString(),cvvEdit.getText().toString(),countryEdit.getText().toString());

                navController.navigate(R.id.action_paymentFragment_to_occupiedFragment, bundle);
            }
        });
    }

    /**
     * TODO: send payment information to DE1
     * @param cardNum
     * @param expDate
     * @param cvv
     * @param country
     */
    private void sendPayment (String cardNum, String expDate, String cvv, String country){

    }
}