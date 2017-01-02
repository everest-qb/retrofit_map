package tw.housemart.test.retrofit.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import tw.housemart.test.retrofit.cllback.LocateUpdateGoogleMap;
import tw.housemart.test.retrofit.net.client.ClientProtocolCodecFactory;
import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.SHCProtocal;


public class NetService extends Service {
    private static final String TAG="QB:NetService";
    private  IBinder mBinder;
    private String HOSTNAME="59.126.51.143";
    private int PORT=5999;
    private NioSocketConnector connector;
    private FriendHandler handler;
    private LocateUpdateGoogleMap locateListener;
    private byte[] deviceUUID;
    private byte[] groupUUID;
    private String name;
    private LocationManager locationManager;


    public NetService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"on Bind");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, handler);
        deviceUUID=intent.getByteArrayExtra("DEVICE_UUID");
        groupUUID=intent.getByteArrayExtra("GROUP_UUID");
        name=intent.getStringExtra("MY_NAME");
        mBinder = new LocalBinder();
        if(handler!=null){
            handler.setDeviceID(deviceUUID);
            handler.setGroupID(groupUUID);
            handler.setName(name);
        }
       return mBinder;
    }


    @Override
    public void onCreate() {
        Log.d(TAG,"on Create");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientProtocolCodecFactory()));
        connector.getSessionConfig().setMaxReadBufferSize(1048704);
        connector.getSessionConfig().setMinReadBufferSize(2048);
        handler=new FriendHandler();
        connector.setHandler(handler);
            super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"on Destroy");
        connector.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"on Unbind");
        if(locateListener!=null) {
            locationManager.removeUpdates(locateListener);
            handler.removeTogetherListener(locateListener);
        }
        locationManager.removeUpdates(handler);
        handler.leave();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public NetService getService() {
            return NetService.this;
        }
    }

    //service
    public void connect(){
        new Thread(new Runnable() {
            public void run() {
                ConnectFuture future=connector.connect(new InetSocketAddress(HOSTNAME, PORT));
                future.addListener(new IoFutureListener<IoFuture>() {
                    @Override
                    public void operationComplete(IoFuture future) {

                        IoSession session= future.getSession();
                        SHCData obj=new SHCData();
                        obj.setCommand(SHCProtocal.CONTROL_REGISTER_GROUP_UUID);
                        obj.setUuid(deviceUUID);
                        obj.setGroupId(groupUUID);
                        session.write(obj);
                    }
                });
            }
        }).start();
    }

    public void requestAllGroupUUID(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                handler.requestAllGroupUUID();
            }
        }).start();
    }

    public CopyOnWriteArrayList<byte[]> findGroupGUUID(){
       return  handler.getUuidList();
    }

    public void registerLocate(LocateUpdateGoogleMap listener){
        locateListener=listener;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locateListener);
        handler.addTogetherListener(locateListener);
        Log.d(TAG,"registerLocate");
    }
}
