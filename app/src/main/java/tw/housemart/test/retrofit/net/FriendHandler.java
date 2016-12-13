package tw.housemart.test.retrofit.net;

import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;

/**
 * Created by user on 2016/12/8.
 */

public class FriendHandler extends IoHandlerAdapter {
    public enum NET {SESSION_CREATED,SESSION_OPENED
        ,SESSON_CLOSED,REGISTED,NOT_REGISTED
    ,OK,ERROR}
    public enum TOGETHER {JOIN,LEAVE,LOCATE}
    private static final String TAG="QB";
    private byte[] deviceID;
    private byte[]  groupID;
    private List<byte[]> uuidList;
    private IoSession session;
    private String STATUS;
    private boolean registered;

    public FriendHandler(){

    }

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
        //this.uuidList.clear();
        //this.uuidList=null;
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
        Log.i(TAG,"CMD:"+ DatatypeConverter.printHexBinary(obj.getCommand()));
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
                Log.d(TAG,STATUS);
            }
        }else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_UUIDS,obj.getCommand())){
            uuidList=obj.getUuidList();
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
                    if(uuidList!=null)
                        uuidList.add(obj.getsUUID());
                    Log.d(TAG,"JON:"+uuidList.size());
                }else if(TOGETHER.LEAVE.name().equals(str)){
                    if(uuidList!=null)
                        uuidList.remove(obj.getsUUID());
                    Log.d(TAG,"LEAVE:"+uuidList.size());
                }else if(str.startsWith(TOGETHER.LOCATE.name())){
                    Log.d(TAG,"LOCATE");
                }
            }
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.d(TAG,"messageSent");
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
        }

    }

    private void requestAllGroupUUID(){
        SHCData tmp=new SHCData();
        tmp.setCommand(SHCProtocal.CONTROL_GET_GROUP_UUIDS);
        tmp.setGroupId(groupID);
        session.write(tmp);
    }


    public void leave(){
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
        Log.d(TAG,"CALL LEAVE");
    }


    public byte[] getDeviceID() {
        return deviceID;
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

    public List<byte[]> getUuidList() {
        return uuidList;
    }

    public void setUuidList(List<byte[]> uuidList) {
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
