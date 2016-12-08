package tw.housemart.test.retrofit.net.client;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.housemart.test.retrofit.net.object.SHCData;
import tw.housemart.test.retrofit.net.util.SHCProtocal;


public class CswEncoder implements ProtocolEncoder {
	Logger log = LoggerFactory.getLogger(CswEncoder.class);

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		SHCData obj=(SHCData)message;
		if(Arrays.equals(SHCProtocal.CONTROL_REGISTER_UUID,obj.getCommand()) ||
				Arrays.equals(SHCProtocal.CONTROL_REGISTER_GROUP_UUID,obj.getCommand())){
			int size=0;
			if(Arrays.equals(SHCProtocal.CONTROL_REGISTER_GROUP_UUID,obj.getCommand())){
				size=98;
			}else{
				size=50;
			}
			IoBuffer buffer =IoBuffer.allocate(size);
			buffer.put(obj.getCommand());
			buffer.put(obj.getUuid());		
			if(Arrays.equals(SHCProtocal.CONTROL_REGISTER_GROUP_UUID,obj.getCommand()))
				buffer.put(obj.getGroupId());	
			buffer.flip();
			out.write(buffer);			
			log.debug("SEND REGISTER_UUID:{}",obj.toString());
		}else if(Arrays.equals(SHCProtocal.CONTROL_GET_UUIDS,obj.getCommand())||
				Arrays.equals(SHCProtocal.CONTROL_GET_GROUP_UUIDS,obj.getCommand())){
			int size=0;
			if(Arrays.equals(SHCProtocal.CONTROL_GET_GROUP_UUIDS,obj.getCommand())){
				size=50;
			}else{
				size=2;
			}
			IoBuffer buffer =IoBuffer.allocate(size);
			buffer.put(obj.getCommand());
			if(Arrays.equals(SHCProtocal.CONTROL_GET_GROUP_UUIDS,obj.getCommand()))
				buffer.put(obj.getGroupId());	
			buffer.flip();
			out.write(buffer);			
			log.debug("SEND GET_UUIDS:{}",obj.toString());
		}if(Arrays.equals(SHCProtocal.CONTROL_SEND,obj.getCommand())){
			IoBuffer buffer =IoBuffer.allocate(102+obj.getData().length);
			buffer.put(obj.getCommand());
			buffer.put(obj.getsUUID());
			buffer.put(obj.getdUUID());
			buffer.put(obj.getDateLength());
			buffer.put(obj.getData());
			buffer.flip();
			out.write(buffer);			
			log.debug("SEND DATA:{}",obj.toString());
		}if(Arrays.equals(SHCProtocal.CONTROL_RESPONSE_SUCESS,obj.getCommand()) 
				|| Arrays.equals(SHCProtocal.CONTROL_RESPONSE_ERROR,obj.getCommand())){
			IoBuffer buffer =IoBuffer.allocate(98);
			buffer.put(obj.getCommand());
			buffer.put(obj.getsUUID());
			buffer.put(obj.getdUUID());
			buffer.flip();
			out.write(buffer);				
			log.debug("SEND RESPONSE:{}",obj.toString());
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
		
	}

}
