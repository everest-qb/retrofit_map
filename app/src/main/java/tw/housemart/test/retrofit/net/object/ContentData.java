package tw.housemart.test.retrofit.net.object;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class ContentData implements Serializable{
	
	private InetSocketAddress ip;
	private long sessionId;
	private String stringUuid;
	private byte[] uuid;
	private byte[] groupId;
	
	public InetSocketAddress getIp() {
		return ip;
	}
	public void setIp(InetSocketAddress ip) {
		this.ip = ip;
	}
	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public String getStringUuid() {
		return stringUuid;
	}
	public void setStringUuid(String stringUuid) {
		this.stringUuid = stringUuid;
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
	
}
