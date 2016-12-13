package tw.housemart.test.retrofit.net.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class SHCProtocal {
	
	//Ver 0.2
	public static final byte[] CONTROL_SEND=new byte[] {Byte.decode("-6"),Byte.decode("0")};
	
	public static final byte[] CONTROL_RESPONSE_SUCESS=new byte[] {Byte.decode("-6"),Byte.decode("1")};
	public static final byte[] CONTROL_RESPONSE_ERROR=new byte[] {Byte.decode("-6"),Byte.decode("2")};
	public static final byte[] CONTROL_RESPONSE_UUIDS=new byte[] {Byte.decode("-6"),Byte.decode("3")};
	
	public static final byte[] CONTROL_REGISTER_UUID=new byte[] {Byte.decode("-4"),Byte.decode("0")};
	public static final byte[] CONTROL_GET_UUIDS=new byte[] {Byte.decode("-4"),Byte.decode("1")};

	//Ver 0.3
	public static final byte[] CONTROL_REGISTER_GROUP_UUID=new byte[] {Byte.decode("-4"),Byte.decode("2")};
	public static final byte[] CONTROL_GET_GROUP_UUIDS=new byte[] {Byte.decode("-4"),Byte.decode("3")};
	
	//special variable
	public static final String SESSION_KEY_UUID="everest.session.uuid";
	public static final String SEARCH_UUIDS_RULE_INTERNET="public";
	public static final String SEARCH_UUIDS_RULE_INTRANET="private";


	public static byte[] genUUID(){
		byte[] returnValue=new byte[48];
		Random r=new Random(System.currentTimeMillis());
		r.nextBytes(returnValue);
		for(int i=0;i<16;i++){
			if(i<4) {
				returnValue[i]=(byte)0xff;
			}else if(i>=8 && i<12){
				returnValue[i]=(byte)0xff;
			}else{
				returnValue[i] = 0;
			}
		}
		return returnValue;
	}

	public static byte[] genGroupID(){
		byte[] returnValue=new byte[48];
		Random r=new Random(System.currentTimeMillis());
		r.nextBytes(returnValue);
		return returnValue;
	}

	public static byte[] genGroupID(String input){
		byte[] returnValue=new byte[48];
		byte[] in;
		try {
			in=input.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			in=new byte[]{32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,
					32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,48,49,50,51,52,53,54,55,56,57};
		}
		for(int i=0;i<48;i++){
			if(i<in.length) {
				returnValue[i] = in[i];
			}else{
				returnValue[i]=32;
			}
		}
		return returnValue;
	}
}
