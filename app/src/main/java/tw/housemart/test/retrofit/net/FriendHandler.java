package tw.housemart.test.retrofit.net;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;
import tw.housemart.test.retrofit.together.ChangeListener;
import tw.housemart.test.retrofit.together.InfoObject;
import tw.housemart.test.retrofit.together.TOGETHER;

/**
 * Created by user on 2016/12/8.
 */

public class FriendHandler extends IoHandlerAdapter implements LocationListener {
    public enum NET {SESSION_CREATED,SESSION_OPENED
        ,SESSON_CLOSED,REGISTED,NOT_REGISTED
    ,OK,ERROR}
    private static final String TAG="QB";
    private byte[] deviceID;
    private byte[]  groupID;
    private CopyOnWriteArrayList<byte[]> uuidList=new CopyOnWriteArrayList<>();
    private IoSession session;
    private String STATUS;
    private boolean registered;
    private ChangeListener changeListener;

    public FriendHandler(){
    }


    //apache mina listener
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        this.registered=false;
        this.session=session;
        STATUS=NET.SESSION_CREATED.name();
        Log.d(TAG,STATUS);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        STATUS=NET.SESSION_OPENED.name();
        Log.d(TAG,STATUS);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.registered=false;
        this.session=null;
        STATUS=NET.SESSON_CLOSED.name();
        Log.d(TAG,STATUS);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        SHCData obj=(SHCData)message;
        Log.i(TAG,Thread.currentThread().getName()+" CMD:"+ DatatypeConverter.printHexBinary(obj.getCommand()));
        if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_SUCESS,obj.getCommand())){
            if(NET.SESSION_OPENED.name().equals(STATUS)){
                registered=true;
                STATUS=NET.REGISTED.name();
                Log.d(TAG,STATUS);
                requestAllGroupUUID();
            }else{
                STATUS=NET.OK.name();
                Log.d(TAG,STATUS);
            }
        }else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_ERROR,obj.getCommand())){
            if(!registered){
                STATUS=NET.NOT_REGISTED.name();
                Log.d(TAG,STATUS);
            }else{
                STATUS=NET.ERROR.name();
                Log.w(TAG,STATUS);
                removeUUID(obj.getsUUID());
                STATUS=NET.OK.name();
                Log.d(TAG,STATUS);
            }
        }else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_UUIDS,obj.getCommand())){
            uuidList.clear();
            uuidList.addAll(obj.getUuidList());
            if(NET.REGISTED.name().equals(STATUS)){
                STATUS=NET.OK.name();
                join();
            }
            Log.i(TAG,"UUID SIZE:"+uuidList.size());
        }else if(Arrays.equals(SHCProtocal.CONTROL_SEND,obj.getCommand())){
            if(NET.OK.name().equals(STATUS) || NET.REGISTED.name().equals(STATUS)) {
                String str = new String(obj.getData(), Charset.forName("US-ASCII"));
                if(str.length()>0)
                if(TOGETHER.JOIN.name().equals(str)){
                    addUUID(obj.getsUUID());
                    Log.d(TAG,"JOIN:"+uuidList.size());
                    if(changeListener!=null) {
                        InfoObject info=new InfoObject();
                        info.setUuid(obj.getsUUID());
                        info.setLatitude(0d);
                        info.setLongitude(0d);
                        changeListener.onJoin(info);
                    }
                }else if(TOGETHER.LEAVE.name().equals(str)){
                    removeUUID(obj.getsUUID());
                    Log.d(TAG,"LEAVE:"+uuidList.size());
                    if(changeListener!=null) {
                        InfoObject info=new InfoObject();
                        info.setUuid(obj.getsUUID());
                        changeListener.onLeave(info);
                    }
                }else if(str.startsWith(TOGETHER.LOCATE.name())){
                    Log.d(TAG,"LOCATE:"+str);
                    if(changeListener!=null) {
                        InfoObject info=new InfoObject();
                        Map<String,Double> map=InfoObject.strToLocate(str);
                        info.setUuid(obj.getsUUID());
                        info.setLongitude(map.get(InfoObject.LOCATE.longitude.name()));
                        info.setLatitude(map.get(InfoObject.LOCATE.latitude.name()));
                        changeListener.onLocate(info);
                    }
                }
            }
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.d(TAG,"messageSent");
    }


    //location listener
    @Override
    public void onLocationChanged(Location location) {
        String locateStr=InfoObject.locateToStr(location.getLongitude(),location.getLatitude());
        Log.d(TAG,"Locate String:"+locateStr);
        locate(locateStr);
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

    //private function
    private void addUUID(byte[] bs){
            boolean passed=false;
            for(byte[] lbs:uuidList){
                if(Arrays.equals(lbs,bs)){
                    passed=true;
                }
            }
            if(!passed)
                uuidList.add(bs);
    }

    private void removeUUID(byte[] bs){
            byte[] remove=null;
            for(byte[] lbs:uuidList){
                if(Arrays.equals(lbs,bs)){
                    remove=lbs;
                }
            }
            if(remove!=null)
                uuidList.remove(remove);
    }

    private void join(){
        for(byte[] destination:uuidList) {
            SHCData obj = new SHCData();
            obj.setCommand(SHCProtocal.CONTROL_SEND);
            obj.setsUUID(deviceID);
            obj.setGroupId(groupID);
            obj.setdUUID(destination);
            try {
                obj.setData(TOGETHER.JOIN.name().getBytes("US-ASCII"));
            } catch (UnsupportedEncodingException e) {
            }
            session.write(obj);
            //update google map
            if(changeListener!=null) {
                InfoObject info = new InfoObject();
                info.setUuid(destination);
                info.setLatitude(0d);
                info.setLongitude(0d);
                changeListener.onJoin(info);
            }
        }
    }

    private void requestAllGroupUUID(){
        SHCData tmp=new SHCData();
        tmp.setCommand(SHCProtocal.CONTROL_GET_GROUP_UUIDS);
        tmp.setGroupId(groupID);
        session.write(tmp);
    }

    private void locate(final String data){
        new Thread(new Runnable() {
            public void run() {
                for(byte[] destination:uuidList) {
                    SHCData obj = new SHCData();
                    obj.setCommand(SHCProtocal.CONTROL_SEND);
                    obj.setsUUID(deviceID);
                    obj.setGroupId(groupID);
                    obj.setdUUID(destination);
                    try {
                        obj.setData(data.getBytes("US-ASCII"));
                    } catch (UnsupportedEncodingException e) {
                    }
                    session.write(obj);
                }
            }
        }).start();
    }

    //public function
    public void leave(){
        //new Thread(new Runnable() {
            //public void run() {
                for(byte[] destination:uuidList) {
                    SHCData obj = new SHCData();
                    obj.setCommand(SHCProtocal.CONTROL_SEND);
                    obj.setsUUID(deviceID);
                    obj.setGroupId(groupID);
                    obj.setdUUID(destination);
                    try {
                        obj.setData(TOGETHER.LEAVE.name().getBytes("US-ASCII"));
                    } catch (UnsupportedEncodingException e) {
                    }
                    session.write(obj);
                }
            //}
        //}).start();

        Log.d(TAG,"CALL LEAVE");
    }

    public byte[] getDeviceID() {
        return deviceID;
    }

    public void addTogetherListener(ChangeListener listener){
        changeListener=listener;
    }

    public void removeTogetherListener(ChangeListener listener){
        changeListener=null;
    }

    //get set
    public void setDeviceID(byte[] deviceID) {
        this.deviceID = deviceID;
    }

    public byte[] getGroupID() {
        return groupID;
    }

    public void setGroupID(byte[] groupID) {
        this.groupID = groupID;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public CopyOnWriteArrayList<byte[]> getUuidList() {
        return uuidList;
    }

    public void setUuidList(CopyOnWriteArrayList<byte[]> uuidList) {
        this.uuidList = uuidList;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
