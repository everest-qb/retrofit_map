package tw.housemart.test.retrofit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import tw.housemart.test.retrofit.rest.DCService;
import tw.housemart.test.retrofit.rest.obj.Station;

public class MainActivity extends AppCompatActivity {

    private DCService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://dev02.localdomain:8080/DcWar/rest/")
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .client(client.build())
                .build();
        service =retrofit.create(DCService.class);
        Button button = (Button)findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button2);
        button.setOnClickListener(mBtListener);
        button2.setOnClickListener(mBt2Listener);

    }


    private View.OnClickListener mBtListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            service.findAllStation().enqueue(new Callback<List<Station>>(){
                @Override
                public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                    Log.i("MAIN","SIZE:"+response.body().size());
                    for(Station s:response.body()){
                        Log.i("MAIN",s.getName());
                        Log.i("MAIN",s.getDescription());
                        Log.i("MAIN",s.getIp());
                        Log.i("MAIN",s.getChangeTime().toString());
                        Log.i("MAIN",""+s.getId());
                        Log.i("MAIN",""+s.getLatitude());
                        Log.i("MAIN",""+s.getLongitude());
                    }
                }

                @Override
                public void onFailure(Call<List<Station>> call, Throwable t) {
                    Log.w("MAIN",t);
                }
            });

            Uri gmmIntentUri = Uri.parse("geo:24.1983066,120.6605483");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    };

    private View.OnClickListener mBt2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            service.findStation(1).enqueue(new Callback<Station>(){
                @Override
                public void onResponse(Call<Station> call, Response<Station> response) {
                    Log.i("MAIN",response.body().getName());
                    Log.i("MAIN",call.request().toString());
                }

                @Override
                public void onFailure(Call<Station> call, Throwable t) {
                    Log.w("MAIN",t);
                }
            });
        }
    };

    private OkHttpClient.Builder client= new OkHttpClient.Builder()
            .authenticator(new Authenticator(){
                @Override
                public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                    String credential = Credentials.basic("USER01", "123456789");
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            });

    private GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    });

}
