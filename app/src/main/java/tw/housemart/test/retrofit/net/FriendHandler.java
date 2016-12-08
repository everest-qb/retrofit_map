package tw.housemart.test.retrofit.net;

import android.util.Log;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Arrays;
import java.util.List;

import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;

/**
 * Created by user on 2016/12/8.
 */

public class FriendHandler extends IoHandlerAdapter {
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
        STATUS="session created";
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        STATUS="session opened";
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.registered=false;
        this.session=null;
        STATUS="session closed";
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
        Log.i("QB", DatatypeConverter.printHexBinary(obj.getCommand()));
        if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_SUCESS,obj.getCommand())){
            if("sesion opened".equals(STATUS)){
                registered=true;
                STATUS="registed";
            }else{
                STATUS="ok";
            }
        }else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_ERROR,obj.getCommand())){
            if(!registered){
                STATUS="no registed";
            }else{
                STATUS="error";
            }
        }else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_UUIDS,obj.getCommand())){
            uuidList=obj.getUuidList();
            Log.i("QB","SIZE:"+uuidList.size());
        }else if(Arrays.equals(SHCProtocal.CONTROL_SEND,obj.getCommand())){

        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }


    public byte[] getDeviceID() {
        return deviceID;
    }

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
