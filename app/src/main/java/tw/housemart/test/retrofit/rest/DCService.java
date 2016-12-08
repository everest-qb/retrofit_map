package tw.housemart.test.retrofit.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tw.housemart.test.retrofit.rest.obj.DeviceRecord;
import tw.housemart.test.retrofit.rest.obj.Rank;
import tw.housemart.test.retrofit.rest.obj.Station;

/**
 * Created by user on 2016/12/6.
 */

public interface DCService {

        @GET("station/all")
        public Call<List<Station>> findAllStation();

        @GET("station/one")
        public Call<Station> findStation(@Query("id") int stationId);

        @GET("data/sta")
        public Call<List<DeviceRecord>> findRecordByStation(@Query("id") int stationId);

        @GET("data/dev")
        public Call<List<DeviceRecord>> findRecordByDevice(@Query("id") String devId);

        @GET("data/top")
        public Call<List<Rank>> findRank(@Query("limit") int limit);

}
