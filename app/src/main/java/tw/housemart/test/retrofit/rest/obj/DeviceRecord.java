package tw.housemart.test.retrofit.rest.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the device_record database table.
 * 
 */

public class DeviceRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private Date createTime;

	private int dHeartRate;

	private float dTemperature;

	private String deviceId;

	private Station station;

	private int stationId;
	
	public DeviceRecord() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getDHeartRate() {
		return this.dHeartRate;
	}

	public void setDHeartRate(int dHeartRate) {
		this.dHeartRate = dHeartRate;
	}

	public float getDTemperature() {
		return this.dTemperature;
	}

	public void setDTemperature(float dTemperature) {
		this.dTemperature = dTemperature;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Station getStation() {
		return this.station;
	}

	public void setStation(Station station) {		
		this.station = station;		
	}

	public int getStationId() {
		return this.stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

}