package tw.housemart.test.retrofit.rest.obj;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the station database table.
 * 
 */

public class Station implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String description;

	private String ip;

	private String name;

	private Date changeTime;

	private List<DeviceRecord> deviceRecords;

	private double longitude;
	
	private double latitude;
	
	public Station() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public List<DeviceRecord> getDeviceRecords() {
		return this.deviceRecords;
	}

	public void setDeviceRecords(List<DeviceRecord> deviceRecords) {
		this.deviceRecords = deviceRecords;
	}

	public DeviceRecord addDeviceRecord(DeviceRecord deviceRecord) {
		getDeviceRecords().add(deviceRecord);
		deviceRecord.setStation(this);

		return deviceRecord;
	}

	public DeviceRecord removeDeviceRecord(DeviceRecord deviceRecord) {
		getDeviceRecords().remove(deviceRecord);
		deviceRecord.setStation(null);

		return deviceRecord;
	}

}