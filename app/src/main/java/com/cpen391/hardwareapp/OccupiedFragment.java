package com.cpen391.hardwareapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OccupiedFragment extends btFragment {
    private View v;
    private String plateNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_occupied, container, false);
        plateNo = getArguments().getString(Constants.plateNo);
        TextView plateNoText = v.findViewById(R.id.PlateNumber);
        plateNoText.setText(plateNo);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        TextView duration = v.findViewById(R.id.duration);
        TextView cost = v.findViewById(R.id.cost);

        /* Start a timer to keep track of current parking session tme*/
        long startTime = System.currentTimeMillis();
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                /* Calculate parking session duration */
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000) %60;
                int minutes = (int) (millis/(1000 * 60)%60);
                int hours = (int) (millis/(1000 * 60 * 60));
                String durationStr = String.format("%02d", hours)+ ":" + String.format("%02d", minutes);

                /* Update displayed timer and re-calculate cost */
                duration.setText(durationStr);
                cost.setText(calcCost(MainActivity.sp.getInt(Constants.unitPriceStr,0), durationStr));
                timerHandler.postDelayed(this, 30000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }


    /**
     * Received data from the DE1
     * We are expecting a string in the format "END,ABCABC" where ABCABC is the plate number
     *
     * Messages in any other formats are ignored
     */
    @Override
    public void readBtData(String msg) {
        String[] strArray = msg.split(",", 2);
        if (strArray[0].equals(Constants.OK)){
            final NavController navController = Navigation.findNavController(v);
            if(strArray[1].equals(Constants.LEAVE)) {
                navController.navigate(R.id.action_occupiedFragment_to_initialFragment);
            }
        }
    }

    /**
     * Calculate cost based on the given unit price and parking duration
     * Round up to the nearest hour
     * Return a string with the cost formatted up to 2 decimal places X.XX
     */
    public static String calcCost(int unitPrice, String duration) {
        String delims = "[:]";
        String[] tokens = duration.split(delims);
        
        /* +1 minute takes care of case when parking session is in the first minute of the hour (like 00:00:30), this already counts as parking for the hour */
        double timeHr = Math.ceil(Double.parseDouble(tokens[0]) + ((Double.parseDouble(tokens[1]) + 1)/60));
        return(String.format("%.2f", timeHr*unitPrice));
    }
}