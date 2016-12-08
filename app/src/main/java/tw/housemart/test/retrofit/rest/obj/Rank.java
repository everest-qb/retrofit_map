package tw.housemart.test.retrofit.rest.obj;

import java.io.Serializable;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the rank database table.
 * 
 */

public class Rank implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date createTime;

	private String deviceId;

	private BigInteger id;

	private int stationId;

	public Rank() {
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public BigInteger getId() {
		return this.id;
	}

	public int getStationId() {
		return this.stationId;
	}



}