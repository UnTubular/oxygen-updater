package com.arjanvlek.oxygenupdater.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arjanvlek.oxygenupdater.Model.Device;
import com.arjanvlek.oxygenupdater.Model.UpdateMethod;
import com.arjanvlek.oxygenupdater.R;

import java.util.List;
import java.util.Locale;

import static com.arjanvlek.oxygenupdater.ApplicationData.LOCALE_DUTCH;

public class CustomDropdown {

    static View initCustomDeviceDropdown(int currentPosition, View convertView, ViewGroup parent, @LayoutRes int layoutType, List<Device> devices, int recommendedPosition, Context context) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutType, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(devices.get(currentPosition).getName());

        if(recommendedPosition != -1) {
            if(currentPosition == recommendedPosition) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.holo_green_dark));
            } else {
                textView.setTextColor(Color.BLACK);
            }
        } else {
            textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    static View initCustomUpdateMethodDropdown(int currentPosition, View convertView, ViewGroup parent, @LayoutRes int layoutType, List<UpdateMethod> updateMethods, int[] recommendedPositions, Context context) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutType, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        switch (Locale.getDefault().getDisplayLanguage()) {
            case LOCALE_DUTCH:
                textView.setText(updateMethods.get(currentPosition).getDutchName());
                break;
            default:
                textView.setText(updateMethods.get(currentPosition).getEnglishName());
        }

        textView.setTextColor(Color.BLACK);

        if(recommendedPositions !=  null) {
            for(Integer recommendedPosition : recommendedPositions) {
                if(currentPosition == recommendedPosition) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.holo_green_dark));
                }
            }
        }

        return convertView;
    }
}