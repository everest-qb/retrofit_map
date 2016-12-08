package tw.housemart.test.retrofit.net.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.DatatypeConverter;
import tw.housemart.test.retrofit.net.util.SHCProtocal;


public class CwsDecoder extends CumulativeProtocolDecoder {
	Logger log = LoggerFactory.getLogger(CwsDecoder.class);

	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput out) throws Exception {
		boolean returnValue=false;		
		if(buffer.capacity()<2) return returnValue;
		if(buffer.limit()-buffer.position()<2) {log.error("leaft size small than 2!"); return returnValue;}
		
		byte[] control=new byte[2];
		try{
			buffer.get(control);
		}catch(Exception ex){
			if (buffer != null){
				log.error("Capaticy:{}", buffer.capacity());
				log.error("Postion:{}", buffer.position());
				log.error("Limit:{}", buffer.limit());				
			}
			
			throw ex;
		}
		log.debug("Command:{}", DatatypeConverter.printHexBinary(control));
		SHCData obj=new SHCData();
		if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_UUIDS,control)){
			obj.setCommand(control);
			int length=(buffer.get()) & 0xFF;
			if(buffer.limit()-buffer.position()>=48*length){
				List<byte[]> uuids=new ArrayList<byte[]>();
				for(int i=0;i<length;i++){					
					byte[] tmp =new byte[48];
					buffer.get(tmp);
					uuids.add(tmp);
				}
				obj.setUuidList(uuids);
				returnValue=true;
				out.write(obj);
				
				log.debug("GET RESPONSE_UUIDS");
			}
		}else if(Arrays.equals(SHCProtocal.CONTROL_SEND,control)){
			obj.setCommand(control);			
			byte[] source=new byte[48];
			byte[] destine=new byte[48];			
			if(buffer.limit()-buffer.position()>=48){
				buffer.get(source);
				if(buffer.limit()-buffer.position()>=48){
					buffer.get(destine);
					if(buffer.limit()-buffer.position()>=4){						
						long size=buffer.getUnsignedInt();						
						if(size<=65535 && size>0){
							if (buffer.limit() - buffer.position() >= size) {
								byte[] data = new byte[(int)size];
								buffer.get(data);
								obj.setData(data);
								obj.setdUUID(destine);
								obj.setsUUID(source);
								returnValue = true;								
								out.write(obj);
								log.debug("GET OTHER SEND DATA");
							}
						}else{
							throw new Exception("Send data size over:"+(int)size);
						}
					}
				}				
			}
			
			
		}else if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_SUCESS,control)
				|| Arrays.equals(SHCProtocal.CONTROL_RESPONSE_ERROR,control)){
			obj.setCommand(control);
			byte[] source=new byte[48];
			byte[] destine=new byte[48];
			if(buffer.limit()-buffer.position()>=96){
				buffer.get(source);
				if(buffer.limit()-buffer.position()>=48){
					buffer.get(destine);
					obj.setsUUID(source);
					obj.setdUUID(destine);
					returnValue=true;
					out.write(obj);
					log.debug("GET RESPONSE");
				}
			}
		}else{
			throw new Exception("Command error!");
		}
		
		if(returnValue==false){
			buffer.rewind();
		}
		return returnValue;
	}

}
