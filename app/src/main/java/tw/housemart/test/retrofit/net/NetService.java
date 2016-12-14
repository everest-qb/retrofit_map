package tw.housemart.test.retrofit.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import tw.housemart.test.retrofit.net.client.ClientProtocolCodecFactory;
import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.SHCProtocal;


public class NetService extends Service {
    private static final String TAG="QB";
    private  IBinder mBinder;
    private String HOSTNAME="192.168.7.14";
    private int PORT=5888;
    private NioSocketConnector connector;
    private FriendHandler handler;
    private byte[] deviceUUID;
    private byte[] groupUUID;
    private LocationManager locationManager;

    public NetService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"on Bind");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, handler);
        deviceUUID=intent.getByteArrayExtra("DEVICE_UUID");
        groupUUID=intent.getByteArrayExtra("GROUP_UUID");
        mBinder = new LocalBinder();
        if(handler!=null){
            handler.setDeviceID(deviceUUID);
            handler.setGroupID(groupUUID);
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
        locationManager.removeUpdates(handler);
        this.handler.leave();
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
                handler.getSession();
                SHCData tmp=new SHCData();
                tmp.setCommand(SHCProtocal.CONTROL_GET_GROUP_UUIDS);
                tmp.setGroupId(groupUUID);
                handler.getSession().write(tmp);
            }
        }).start();
    }

    public List<byte[]> findGroupGUUID(){
       return  handler.getUuidList();
    }
}
