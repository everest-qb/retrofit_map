package tw.housemart.test.retrofit.net.object;

import java.io.Serializable;
import java.util.List;

import tw.housemart.test.retrofit.net.util.DatatypeConverter;


public class SHCData implements Serializable {

	private byte[] command; //2
	
	private byte[] sUUID; //48
	private byte[] dUUID; //48
		
	private byte[] data; // 0~64
	
	private List<byte[]> uuidList; //byte[] 48 ,list.size 0~256
	
	private byte[] uuid;//48
	
	private byte[] groupId;//48
	
	public byte[] getDateLength(){//4
		byte[] returnValue;
		if(data==null){
			returnValue=new byte[4];
		}else{
			returnValue=IntToByteArray(data.length);
		}
		
		return returnValue;
	}
	
	public byte  getListSize(){//1
		byte returnValue;
		if(uuidList==null){
			returnValue=0;
		}else{
			returnValue=(byte) ((uuidList.size() & 0x000000FF) >> 0);
		}
		
		return returnValue;
	}
	
	
	private byte[] IntToByteArray( int data ) {

		byte[] result = new byte[4];

		result[0] = (byte) ((data & 0xFF000000) >> 24);
		result[1] = (byte) ((data & 0x00FF0000) >> 16);
		result[2] = (byte) ((data & 0x0000FF00) >> 8);
		result[3] = (byte) ((data & 0x000000FF) >> 0);

		return result;
	}

	
	
	public byte[] getCommand() {
		return command;
	}

	public void setCommand(byte[] command) {
		this.command = command;
	}

	public byte[] getsUUID() {
		return sUUID;
	}

	public void setsUUID(byte[] sUUID) {
		this.sUUID = sUUID;
	}

	public byte[] getdUUID() {
		return dUUID;
	}

	public void setdUUID(byte[] dUUID) {
		this.dUUID = dUUID;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public List<byte[]> getUuidList() {
		return uuidList;
	}

	public void setUuidList(List<byte[]> uuidList) {
		this.uuidList = uuidList;
	}

	public byte[] getUuid() {
		return uuid;
	}

	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}

	public byte[] getGroupId() {
		return groupId;
	}

	public void setGroupId(byte[] groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SHCData [");
		if (command != null) {
			builder.append("Command=");
			builder.append(DatatypeConverter.printHexBinary(command));
			builder.append(", ");
		}
		if (sUUID != null) {
			builder.append("Source UUID=");
			builder.append(DatatypeConverter.printHexBinary(sUUID));
			builder.append(", ");
		}
		if (dUUID != null) {
			builder.append("Target UUID=");
			builder.append(DatatypeConverter.printHexBinary(dUUID));
			builder.append(", ");
		}
		if (data != null) {
			builder.append("Data size=");
			builder.append(data.length);
			builder.append(", ");
		}
		if (uuidList != null) {
			builder.append("UUID List size=");
			builder.append(uuidList.size());
			builder.append(", ");
		}
		if (uuid != null) {
			builder.append("Register UUID=");
			builder.append(DatatypeConverter.printHexBinary(uuid));
			builder.append(", ");
		}
		if (groupId != null) {
			builder.append("Register GROUP=");
			builder.append(DatatypeConverter.printHexBinary(groupId));
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	public String toBigData() {
		StringBuilder builder = new StringBuilder();
		
		if (command != null) {
			builder.append("Command=");
			builder.append(DatatypeConverter.printHexBinary(command));			
		}
		if (sUUID != null) {
			builder.append(", ");
			builder.append("Source UUID=");
			builder.append(DatatypeConverter.printHexBinary(sUUID));			
		}
		if (dUUID != null) {
			builder.append(", ");
			builder.append("Target UUID=");
			builder.append(DatatypeConverter.printHexBinary(dUUID));			
		}
		if (data != null) {
			builder.append(" , ");
			builder.append("Data size=");
			builder.append(data.length);
			builder.append(", ");
			builder.append("Data = ");
			builder.append(DatatypeConverter.printHexBinary(data));			
		}
		if (uuidList != null) {
			builder.append(", ");
			builder.append("UUID List size=");
			builder.append(uuidList.size());			
		}
		if (uuid != null) {
			builder.append(", ");
			builder.append("Register UUID=");
			builder.append(DatatypeConverter.printHexBinary(uuid));			
		}
		if (groupId != null) {
			builder.append(", ");
			builder.append("Register GROUP=");
			builder.append(DatatypeConverter.printHexBinary(groupId));
		}
		
		
		return builder.toString();
	}
}
