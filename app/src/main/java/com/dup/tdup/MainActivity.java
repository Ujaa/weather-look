package com.dup.tdup;
/*
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity
{
    //Load libs
    static
    {System.loadLibrary("native-lib");
    OpenCVLoader.initDebug();}

    private Button btn_fit_outfit;
    private Button btn_add_outfit;
    private Button btn_img_download;
    private TextView textView_add_outfit;
    private TextView textView_fit_outfit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.requestPerms();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_fit_outfit = (Button) findViewById(R.id.button_fit_outfit);
        btn_add_outfit = (Button) findViewById(R.id.button_add_outfit);
        btn_img_download = (Button) findViewById(R.id.button_image_download);
        textView_add_outfit = (TextView) findViewById(R.id.textview_add_outfit);
        textView_fit_outfit = (TextView) findViewById(R.id.text_view_fit_outfit);

        btn_fit_outfit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v == btn_fit_outfit) //start gallery activity
                {
                    Intent intent = new Intent(MainActivity.this, SelectOutfitActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });//end btn_fit_outfit onClick

        btn_add_outfit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v == btn_add_outfit) //start gallery activity
                {
                    Intent intent = new Intent(MainActivity.this, AddOutfitActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });//end btn_add_outfit onClick

        btn_img_download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v ==  btn_img_download) //start gallery activity
                {
                    Intent intent = new Intent(MainActivity.this, ImageDownloadActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });//end btn_add_outfit onClick

    }//End onCreate
}//End activity


 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    //Load libs
    static
    {System.loadLibrary("native-lib");
        OpenCVLoader.initDebug();}

    String [] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    Button gpsButton,showFashionButton;
    TextView result,loc,tempText,time,comment;
    ImageView weatherimg;

    LocationManager locationManager;

    Double lat,lon;
    int temp, feel, humid;

    class Weather extends AsyncTask<String,Void,String> {//First String means URL is in String, Void mean nothing, Third String means Return type will be String

        @Override
        protected String doInBackground(String... address) {//위에서 첫번째 string이므로 string 반환값 :  세번째 string
            //String... means multiple address can be send. It acts as array
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //접속
                connection.connect();

                //서버와 연결되어 있는 스트림을 추출한다
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Retrieve data and return it as String
                int data =isr.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch=(char) data;
                    content= content + ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.requestPerms();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0);
        }else{
            getMyLocation();
        }
    }

    @Override
    protected void onStart() {//액티비티간 데이터 주고받기
        super.onStart();
        showFashionButton=(Button)findViewById(R.id.fashion_btn);
        showFashionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CategoryActivity.class);
                intent.putExtra("temp", temp);
                intent.putExtra("feel", feel);
                intent.putExtra("humidity",humid);
                startActivity(intent);
            }
        });
    }


    public void search2(View view){//위치를 받아서 날씨 찾기
        result=findViewById(R.id.textView);
        gpsButton = findViewById(R.id.gpsButton);
        tempText = findViewById(R.id.temp);
        weatherimg = findViewById(R.id.icon);
        time = findViewById(R.id.time);
        comment=findViewById(R.id.textView3);
        getMyLocation();
        String content;
        Weather weather = new Weather();

        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=4a1b15c4aacf678efc30be1d8b411e1d").get();
            //First we will check data is retrieve successfully or not

            //JSon
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //this main is not part of weather array, it's seperate variablelike weather
//            Log.i("weatherData",weatherData);
            //weather data is in Array
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String temperature = "";
            String humidity = "";
            String feels = "";

            for(int i=0;i<array.length();i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
            }

            switch (main){
                case "Thunderstom" :
                    main = "천둥";
                    weatherimg.setImageResource(R.drawable.thunder);
                    break;
                case "Drizzle" :
                    main = "이슬비";
                    weatherimg.setImageResource(R.drawable.drizzle);
                    break;
                case "Rain" :
                    main = "비";
                    weatherimg.setImageResource(R.drawable.rain);
                    break;
                case "Snow" :
                    main = "눈";
                    weatherimg.setImageResource(R.drawable.snow);
                    break;
                case "Clear" :
                    main = "맑음";
                    weatherimg.setImageResource(R.drawable.clear);
                    break;
                case "Cloud" :
                    main = "구름";
                    weatherimg.setImageResource(R.drawable.cloud);
                    break;
                case "Dust":
                    main = "먼지";
                    weatherimg.setImageResource(R.drawable.other);
                    break;
                case "Tornado" :
                    main = "태풍";
                    weatherimg.setImageResource(R.drawable.other);
                    break;
                default:
                    main = "안개";
                    weatherimg.setImageResource(R.drawable.other);
                    break;
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");
            humidity = mainPart.getString("humidity");
            feels = mainPart.getString("feels_like");


            temp = (int) Double.parseDouble(temperature);
            temp = temp - 273; //절대온도를 섭씨온도로 바꿔주는 작업

            feel = (int) Double.parseDouble(feels);
            feel = feel - 273; // 절대온도를 섭씨온도로 바꿔주는 작업

            humid = (int) Double.parseDouble(humidity);

            String resultText="날씨: "+main+"\n습도: "+humidity+"%";

            result.setText(resultText);
            tempText.setText(temp+"°");

            //How we will show this result on screen


        } catch (Exception e) {
            e.printStackTrace();
        }

        //현재 날짜 구하기
        Date currentTime = Calendar.getInstance().getTime();
        String data_text = new SimpleDateFormat("MM월 dd일 EE요일", Locale.getDefault()).format(currentTime);
        time.setText(data_text);

        if(humid>=0&&humid<40) {
            if(feel < -3) {
                comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
            }
            else if(feel >= -3 && temp <= 4) {
                comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
            }
            else if(5 <= temp && temp <= 8) {
                comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
            }
            else if(9 <= temp && temp <= 11) {
                comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
            }
            else if(12 <= temp && temp <= 17) {
                comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
            }
            else if(18 <= temp && feel < 23) {
                comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요! ");
            }
            else if(23 <= feel && feel < 27) {
                comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
            }
            else {
                comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
            }
        }
        else if(humid>=40&&humid<60) {
            if(feel < -3) {
                comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
            }
            else if(feel >= -3 && temp <= 4) {
                comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
            }
            else if(5 <= temp && temp <= 8) {
                comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
            }
            else if(9 <= temp && temp <= 11) {
                comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
            }
            else if(12 <= temp && temp <= 17) {
                comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
            }
            else if(18 <= temp && feel < 23) {
                comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
            }
            else if(23 <= feel && feel < 27) {
                comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
            }
            else {
                comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
            }
        }
        else {
            if(feel < -3) {
                comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
            }
            else if(feel >= -3 && temp <= 4) {
                comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
            }
            else if(5 <= temp && temp <= 8) {
                comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
            }
            else if(9 <= temp && temp <= 11) {
                comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
            }
            else if(12 <= temp && temp <= 17) {
                comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
            }
            else if(18 <= temp && feel < 23) {
                comment.setText("오늘은 약간 덥고 습하다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
            }
            else if(23 <= feel && feel < 27) {
                comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출을 한다면 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
            }
            else {
                comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원하면서 몸에 많이 달라붙지 않는 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
            }
            //계절이나 날씨(눈,비,안개 관련된 사항에 관해 상의)
        }
    }



    public void getMyLocation(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //권한 확인 작업
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                return;
            }
        }
        //이전에 측정했던 값을 가져온다.
        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location1 !=null) {
            setMyLocation(location1);
        }else{
            if(location2 != null){
                setMyLocation(location2);
            }
        }
        //새롭게 측정한다.
        GetMyLocationListener listener = new GetMyLocationListener();
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10f,listener);
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)==true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10f,listener);
        }
    }
    public void setMyLocation(Location location){ //위도경도로 주소찾기
        lat=location.getLatitude();
        lon=location.getLongitude();
        loc=findViewById(R.id.textView2);

        Geocoder g = new Geocoder(this);
        List<Address> address=null;

        try{
            address =g.getFromLocation(lat,lon,10);
        }catch (IOException e){
            e.printStackTrace();
            Log.d("test","입출력오류");
        }
        if(address!=null){
            if(address.size()==0){
                loc.setText("주소찾기 오류");
            }else{
                Log.d("찾은주소",address.get(0).toString());
                loc.setText(address.get(0).getAddressLine(0));
            }
        }
    }


    //현재 위치 측정이 성공하면 반응하는 리스너
    class GetMyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            setMyLocation(location);
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}
