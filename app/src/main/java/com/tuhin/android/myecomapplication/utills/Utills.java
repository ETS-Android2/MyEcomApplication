package com.tuhin.android.myecomapplication.utills;

import android.content.Context;
import android.location.LocationManager;

public class Utills {

    public static boolean checkWheatherLocationIsEnebeledOrNot(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
