package tw.housemart.test.retrofit.cllback;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import tw.housemart.test.retrofit.together.ChangeListener;
import tw.housemart.test.retrofit.together.InfoObject;

/**
 * Created by user on 2016/12/16.
 */

public class LocateUpdateGoogleMap implements LocationListener ,ChangeListener {
    private static final String TAG="QB";
    //locate
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    //together
    @Override
    public void onJoin(InfoObject obj) {

    }

    @Override
    public void onLeave(InfoObject obj) {

    }

    @Override
    public void onLocate(InfoObject obj) {

    }
}
