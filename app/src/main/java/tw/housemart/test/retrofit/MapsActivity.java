package tw.housemart.test.retrofit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ConcurrentHashMap;

import tw.housemart.test.retrofit.cllback.LocateUpdateGoogleMap;
import tw.housemart.test.retrofit.net.NetService;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;
import tw.housemart.test.retrofit.together.InfoObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG="QB";
    private GoogleMap mMap;
    private NetService mService;
    private byte[] deviceID;
    private byte[] groupID;
    private Marker me;
    private ConcurrentHashMap<String,Marker> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        prepareConfig();
        friends=new ConcurrentHashMap<>();
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
            mService.registerLocate(locateListener);
            mService.connect();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService=null;
        }
    };

    //google map service listener
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        Log.d(TAG,mMap.getMinZoomLevel()+"  "+mMap.getMaxZoomLevel());
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


    //locate lishtener for updte map
    private LocateUpdateGoogleMap locateListener=new LocateUpdateGoogleMap(){
        @Override
        public void onLocationChanged(Location location) {
            final double lon=location.getLongitude();
            final double lat=location.getLatitude();
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"locate change LON:"+lon+" LAT:"+lat);
                    if(me==null){
                        MarkerOptions mark= new MarkerOptions().position(new LatLng(lat,lon)).title("You are here");
                        me=mMap.addMarker(mark);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lon)));
                    }else{
                        me.setPosition(new LatLng(lat,lon));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lon)));
                    }
                }
            });
        }


        @Override
        public void onJoin(InfoObject obj) {
            final String uuid=DatatypeConverter.printHexBinary(obj.getUuid());
            final double lat =obj.getLatitude();
            final double lon =obj.getLongitude();
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(me==null){
                        MarkerOptions mark= new MarkerOptions().position(new LatLng(lat,lon)).title("You are here");
                        me=mMap.addMarker(mark);
                    }
                    double plus=0.01*(friends.size()+1);
                    if(friends.containsKey(uuid)){
                        friends.get(uuid).setPosition(new LatLng(me.getPosition().latitude+plus,me.getPosition().longitude+plus));
                    }else{
                        MarkerOptions mark= new MarkerOptions().position(new LatLng(me.getPosition().latitude+plus,me.getPosition().longitude+0.01)).title(uuid.substring(31,48));
                        friends.put(uuid, mMap.addMarker(mark));
                    }
                }
            });
        }

        @Override
        public void onLeave(InfoObject obj) {
            Log.d(TAG,"onLeave");
            final String uuid=DatatypeConverter.printHexBinary(obj.getUuid());
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(friends.containsKey(uuid)){
                        friends.get(uuid).remove();
                        friends.remove(uuid);
                    }
                }
            });
        }

        @Override
        public void onLocate(InfoObject obj) {
            Log.d(TAG,"onLocate");
            final String uuid=DatatypeConverter.printHexBinary(obj.getUuid());
            final double lat =obj.getLatitude();
            final double lon =obj.getLongitude();
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(friends.containsKey(uuid)){
                        friends.get(uuid).setPosition(new LatLng(lat,lon));
                    }else{
                        MarkerOptions mark= new MarkerOptions().position(new LatLng(lat,lon)).title(uuid.substring(31,48));
                        friends.put(uuid, mMap.addMarker(mark));
                    }
                }
            });
        }
    };

}
