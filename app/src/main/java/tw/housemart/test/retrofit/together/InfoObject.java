package tw.housemart.test.retrofit.together;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 2016/12/16.
 */

public class InfoObject {

    public enum LOCATE{longitude,latitude}
    private byte[] uuid;
    private Double longitude;
    private Double latitude;
    private String name;

    public static Map<String,Double> strToLocate(String data){
        Map<String,Double> returnValue=new HashMap<>();
        if(data.startsWith(TOGETHER.LOCATE.name())){
            String[] array=data.split(":");
            double lon=Double.parseDouble(array[1]);
            double lat=Double.parseDouble(array[2]);
            returnValue.put(LOCATE.longitude.name(),lon);
            returnValue.put(LOCATE.latitude.name(),lat);
        }
        return returnValue;
    }

    public static String locateToStr(Double longitude,Double latitude){
        String latStr=Double.toString(latitude);
        String lonStr=Double.toString(longitude);
        String locateStr= TOGETHER.LOCATE.name()+":"+lonStr+":"+latStr;
        return locateStr;
    }

    //get set
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }
}
