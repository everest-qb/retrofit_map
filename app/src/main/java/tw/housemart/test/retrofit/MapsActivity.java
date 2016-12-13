package tw.housemart.test.retrofit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Arrays;
import java.util.List;

import tw.housemart.test.retrofit.net.NetService;
import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private static final String TAG="QB";
    private GoogleMap mMap;
    private NetService mService;
    private byte[] deviceID;
    private byte[] groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        prepareConfig();

    }


    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(MapsActivity.this, NetService.class);
        intent.putExtra("DEVICE_UUID",deviceID);
        intent.putExtra("GROUP_UUID",groupID);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    private void prepareConfig(){
        SharedPreferences settings = getSharedPreferences("MAPSETS", 0);
        boolean ready = settings.getBoolean("INIT_COMPLETE", false);
        if(ready){
            deviceID=DatatypeConverter.hexStringToByteArray(settings.getString("DEVICE_ID",""));
            groupID=DatatypeConverter.hexStringToByteArray(settings.getString("GROUP_ID",""));
        }else{
            deviceID=SHCProtocal.genUUID();
            groupID=SHCProtocal.genGroupID("                                      0123456789");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("INIT_COMPLETE", true);
            editor.putString("DEVICE_ID",DatatypeConverter.printHexBinary(deviceID));
            editor.putString("GROUP_ID",DatatypeConverter.printHexBinary(groupID));
            editor.commit();
        }
    }

    //service link
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            NetService.LocalBinder binder = (NetService.LocalBinder) service;
            mService = binder.getService();
            mService.connect();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService=null;
        }
    };

    //google map service
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(24.1983066, 120.6605483);
        MarkerOptions mark= new MarkerOptions().position(sydney).title("You are here.");

        mMap.addMarker(mark);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i(TAG,DatatypeConverter.printHexBinary(deviceID));
                Log.i(TAG,DatatypeConverter.printHexBinary(groupID));
                if(mService!=null){
                    mService.requestAllGroupUUID();
                }
            }
        });
    }

}
