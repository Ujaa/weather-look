package com.dup.tdup;

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
import android.widget.Toast;

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

    Button gpsButton,showFashionButton;
    TextView result,loc,tempText,time,comment;
    ImageView weatherimg;

    String [] gpspermission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    LocationManager locationManager;

    Double lat,lon;
    int temp=-100, feel, humid;
    String main = "";

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
        //permissionManager.requestPerms();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestPermissions(permission_list, 0);
            permissionManager.requestPerms();
        }else{
            getMyLocation();
        }
        search2(gpsButton);
    }

    @Override
    protected void onStart() {//액티비티간 데이터 주고받기
        super.onStart();
        showFashionButton=(Button)findViewById(R.id.fashion_btn);
        showFashionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(temp==-100){
                    Toast.makeText(getApplicationContext(), "GPS아이콘을 눌러주세요", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("temp", temp);
                    intent.putExtra("feel", feel);
                    intent.putExtra("humidity", humid);
                    startActivity(intent);
                }
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


            String temperature = "";
            String humidity = "";
            String feels = "";

            JSONObject weatherPart = array.getJSONObject(0);
            main = weatherPart.getString("main");


            switch (main){
                case "Thunderstorm" :
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
                case "Clouds" :
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

        if(temp!=-100) {
            if (humid >= 0 && humid < 40) {
                if (feel < -3) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 이슬비가 내리고 있으니 나갈 때 우산을 꼭 챙기세요!");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 하늘에서 비가 내리고 있어요. 우산이나 우비를 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 눈이 내리고 있어요! 미끄러지지 않는 신발을 신는게 좋겠네요.:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n하늘은 맑으니 춥더라도 자외선 차단에 신경쓰면 좋겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n오늘은 하늘에 구름이 있는 날이네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 공기중에 먼지가 많으니 마스크를 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금 태풍이 지나가고 있어요! 외출을 자제하고 외출하더라도 바람과 비에 유의하세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 안개가 많이 낀 상황이니 시야가 좁아지는 것에 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n특별히 많이 건조하니 정전기에 유의하세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                } else if (feel >= -3 && temp <= 4) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n천둥이 치고 있으니 놀라지 마세요:)");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 이슬비도 살짝 내리고 있어요! 외출할 때 우산을 챙기세요.");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 비가 오고 있으니 나갈 때 우산이나 우비를 꼭꼭 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n또 하늘에서 눈이 내리고 있어요! 미끄러지지 않도록 조심하세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 화창하고 맑은 하늘을 볼 수 있겠네요:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 하늘에 구름이 있는 날이에요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 공기가 먼지로 탁해요! 마스크를 챙기는 것이 좋을거 같네요.");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n밖에는 태풍이 지나가고 있어요! 외출할 때 비와 바람에 주의하세요.");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n그리고 뿌연 안개가 당신의 시야를 가릴 수도 있으니 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 꽤 춥고 건조해요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
                    }
                } else if (5 <= temp && temp <= 8) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n밖에는 천둥이 치고 있어요! 깜짝 놀랄 수도 있으니 조심하세요.");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n비가 내리고 있으니 우산이나 우비는 필수템입니다!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘은 하늘에 구름이 많이 없이 기분 좋은 맑은 날씨네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘 하늘에는 구름이 있는 몽실몽실한 하늘입니다!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n특별히 오늘은 먼지가 많은 날이니 조심하세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n태풍이 지나가고 있으니 거센 바람과 비에 대비하는 것이 좋겠어요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n하늘에는 안개가 껴있는 날입니다! 앞이 잘 보이지 않을 수 있으니 외출할 때 주의하세요!");
                    }
                    else{
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
                    }
                } else if (9 <= temp && temp <= 11) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 천둥이 치고 있으니 조심하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 비가 내리니 물에 금방 마르는 신발을 신는 것이 어떨까요?");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘 하늘은 맑음이니 다른 날보다는 조금 더 따뜻한 하루이겠네요!:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘은 구름이 있는 하늘이네요:)");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n공기 중에는 먼지가 꽤 있으니 마스크를 꼭꼭 챙기세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n특별히 밖은 태풍이 몰아치고 있으니 조심하는 것이 좋겠네요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n또 안개도 꽤 많이 있으니 어두울 때는 특히 더 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
                    }
                } else if (12 <= temp && temp <= 17) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 시원한 날씨네요.\n외출할 때 얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 천둥이 내리치고 있으니 외출할 때 주의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")) {
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 비가 내리고 있으니 우산이나 우비를 잊지 마세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n맑은 하늘이니 외출이나 드라이브하기도 좋겠네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n좋은 날씨지만 공기 중의 먼지가 있으니 마스크는 필수입니다!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n태풍이 몰아치고 있으니 강한 바람과 비에 조심해주세요!");
                    }
                    else if(main.equals("안개")) {
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n안개가 꽤 있으니 시야확보에 신경써주세요!");
                    }
                } else if (18 <= temp && feel < 23) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n특별히 밖에는 천둥이 치고 있으니 소리에 놀라지 않도록 조심하세요!:)");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n하늘에서 비가 오니 우산, 우비 잊지 말아주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n오늘은 맑은 날을 보낼 수 있는 하늘이네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n공기 중에는 먼지가 많은 날이니 마스크를 챙겨주세요.:)");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n또한 태풍이 지나가고 있으니까 외출할 때는 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n안개도 많이 있으니 외출때 유의해주세요.");
                    }
                    else{
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                } else if (23 <= feel && feel < 27) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n지금 밖에는 천둥이 치고 있어요! 소리에 유의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n그리고 비도 오니 우산은 꼭 챙겨주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n햇빛이 많은 날씨이니 자외선을 차단해 피부건강을 지켜주세요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n공기 안의 먼지가 많은 날이니 마스크는 필수로 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n태풍이 지나가고 있어요! 태풍에 대한 대비를 하는 것을 추천드려요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else{
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                }
                else {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n지금은 천둥이 많이 치니 주의해주세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n비가 내리니 우산을 꼭꼭 챙겨주세요!:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n태양이 내리쬐는 하루이니 자외선 차단제는 필수이겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n공기 중에 먼지가 많으니 답답하더라도 마스크는 꼭 착용해주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n날씨도 많이 덥지만 태풍이 지나가고 있으니 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!");
                    }
                    else{
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
                    }
                }
            } else if (humid >= 40 && humid < 60) {
                if (feel < -3) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 이슬비가 내리고 있으니 나갈 때 우산을 꼭 챙기세요!");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 하늘에서 비가 내리고 있어요. 우산이나 우비를 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 눈이 내리고 있어요! 미끄러지지 않는 신발을 신는 것이 좋겠네요.:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n하늘은 맑으니 춥더라도 자외선 차단에 신경쓰면 좋겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n오늘은 하늘에 구름이 있는 날이네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 공기중에 먼지가 많으니 마스크도 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금 태풍이 지나가고 있어요! 외출을 자제하고 외출하더라도 바람과 비에 유의하세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 안개가 많이 낀 상황이니 시야가 좁아지는 것에 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                } else if (feel >= -3 && temp <= 4) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n천둥이 치고 있으니 놀라지 마세요:)");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 이슬비도 살짝 내리고 있어요! 외출할 때 우산을 챙기세요.");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 비가 오고 있으니 나갈 때 우산이나 우비를 꼭꼭 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n또 하늘에서 눈이 내리고 있어요! 미끄러지지 않도록 조심하세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 화창하고 맑은 하늘을 볼 수 있겠네요:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 하늘에 구름이 있는 날이에요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 공기가 먼지로 탁해요! 마스크를 챙기는 것이 좋을거 같네요.");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n밖에는 태풍이 지나가고 있어요! 외출할 때 비와 바람에 주의하세요.");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n그리고 뿌연 안개가 당신의 시야를 가릴 수도 있으니 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
                    }
                } else if (5 <= temp && temp <= 8) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n밖에는 천둥이 치고 있어요! 깜짝 놀랄 수도 있으니 조심하세요.");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n비가 내리고 있으니 우산이나 우비는 필수템입니다!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘은 하늘에 구름이 많이 없이 기분 좋은 맑은 날씨네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘 하늘에는 구름이 있는 몽실몽실한 하늘입니다!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n특별히 오늘은 먼지가 많은 날이니 조심하세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n태풍이 지나가고 있으니 거센 바람과 비에 대비하는 것이 좋겠어요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n하늘에는 안개가 껴있는 날입니다! 앞이 잘 보이지 않을 수 있으니 외출할 때 주의하세요!");
                    }
                    else {
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
                    }
                } else if (9 <= temp && temp <= 11) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 천둥이 치고 있으니 조심하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 비가 내리니 물에 금방 마르는 신발을 신는 것이 어떨까요?");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘 하늘은 맑음이니 다른 날보다는 조금 더 따뜻한 하루이겠네요!:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘은 구름이 있는 하늘이네요:)");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n공기 중에는 먼지가 꽤 있으니 마스크를 꼭꼭 챙기세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n특별히 밖은 태풍이 몰아치고 있으니 조심하는 것이 좋겠네요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n또 안개도 꽤 많이 있으니 어두울 때는 특히 더 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
                    }
                } else if (12 <= temp && temp <= 17) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 시원한 날씨네요.\n외출할 때 얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 천둥이 내리치고 있으니 외출할 때 주의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")) {
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 비가 내리고 있으니 우산이나 우비를 잊지 마세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n맑은 하늘이니 외출이나 드라이브하기도 좋겠네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n좋은 날씨지만 공기 중의 먼지가 있으니 마스크는 필수입니다!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n태풍이 몰아치고 있으니 강한 바람과 비에 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n안개가 꽤 있으니 시야확보에 신경써주세요!");
                    }
                } else if (18 <= temp && feel < 23) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n특별히 밖에는 천둥이 치고 있으니 소리에 놀라지 않도록 조심하세요!:)");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n하늘에서 비가 오니 우산, 우비 잊지 말아주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n오늘은 맑은 날을 보낼 수 있는 하늘이네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n공기 중에는 먼지가 많은 날이니 마스크를 챙겨주세요.:)");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n또한 태풍이 지나가고 있으니까 외출할 때는 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n안개도 많이 있으니 외출때 유의해주세요.");
                    }
                    else{
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                } else if (23 <= feel && feel < 27) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n지금 밖에는 천둥이 치고 있어요! 소리에 유의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n그리고 비도 오니 우산은 꼭 챙겨주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n햇빛이 많은 날씨이니 자외선을 차단해 피부건강을 지켜주세요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n공기 안의 먼지가 많은 날이니 마스크는 필수로 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n태풍이 지나가고 있어요! 태풍에 대한 대비를 하는 것을 추천드려요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else{
                        comment.setText("오늘은 꽤 더운 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                } else {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n지금은 천둥이 많이 치니 주의해주세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n비가 내리니 우산을 꼭꼭 챙겨주세요!:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n태양이 내리쬐는 하루이니 자외선 차단제는 필수이겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n공기 중에 먼지가 많으니 답답하더라도 마스크는 꼭 착용해주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n날씨도 많이 덥지만 태풍이 지나가고 있으니 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!");
                    }
                    else{
                        comment.setText("오늘은 매우 매우 더운 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
                    }
                }
            } else {
                if (feel < -3) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 이슬비가 내리고 있으니 나갈 때 우산을 꼭 챙기세요!");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 하늘에서 비가 내리고 있어요. 우산이나 우비를 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 눈이 내리고 있어요! 미끄러지지 않는 신발을 신는게 좋겠네요.:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n하늘은 맑으니 춥더라도 자외선 차단에 신경쓰면 좋겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n오늘은 하늘에 구름이 있는 날이네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 공기중에 먼지가 많으니 마스크를 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금 태풍이 지나가고 있어요! 외출을 자제하고 외출하더라도 바람과 비에 유의하세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?\n지금은 안개가 많이 낀 상황이니 시야가 좁아지는 것에 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 많이 추우니 두껍고 따뜻한 겉옷을 꼭 챙기세요!\n그리고 목도리나 모자로 추위를 이겨내보는건 어떤가요?");
                    }
                } else if (feel >= -3 && temp <= 4) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n천둥이 치고 있으니 놀라지 마세요:)");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 이슬비도 살짝 내리고 있어요! 외출할 때 우산을 챙기세요.");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 비가 오고 있으니 나갈 때 우산이나 우비를 꼭꼭 챙겨주세요!");
                    }
                    else if(main.equals("눈")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n또 하늘에서 눈이 내리고 있어요! 미끄러지지 않도록 조심하세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 화창하고 맑은 하늘을 볼 수 있겠네요:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n오늘은 하늘에 구름이 있는 날이에요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n지금은 공기가 먼지로 탁해요! 마스크를 챙기는 것이 좋을거 같네요.");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n밖에는 태풍이 지나가고 있어요! 외출할 때 비와 바람에 주의하세요.");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!\n그리고 뿌연 안개가 당신의 시야를 가릴 수도 있으니 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 날씨가 꽤 추워요.\n감기에 걸리지 않도록 두꺼운 옷을 입거나 얇은 옷을 여러겹 입는 것이 좋겠네요!");
                    }
                } else if (5 <= temp && temp <= 8) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n밖에는 천둥이 치고 있어요! 깜짝 놀랄 수도 있으니 조심하세요.");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n비가 내리고 있으니 우산이나 우비는 필수템입니다!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘은 하늘에 구름이 많이 없이 기분 좋은 맑은 날씨네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n오늘 하늘에는 구름이 있는 몽실몽실한 하늘입니다!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n특별히 오늘은 먼지가 많은 날이니 조심하세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n태풍이 지나가고 있으니 거센 바람과 비에 대비하는 것이 좋겠어요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!\n하늘에는 안개가 껴있는 날입니다! 앞이 잘 보이지 않을 수 있으니 외출할 때 주의하세요!");
                    }
                    else{
                        comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
                    }
                    comment.setText("오늘은 많이 쌀쌀해요.\n겉옷을 챙기시고 든든하게 챙겨 입는걸 추천합니다!");
                } else if (9 <= temp && temp <= 11) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 천둥이 치고 있으니 조심하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n하늘에서는 비가 내리니 물에 금방 마르는 신발을 신는 것이 어떨까요?");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘 하늘은 맑음이니 다른 날보다는 조금 더 따뜻한 하루이겠네요!:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n오늘은 구름이 있는 하늘이네요:)");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n공기 중에는 먼지가 꽤 있으니 마스크를 꼭꼭 챙기세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n특별히 밖은 태풍이 몰아치고 있으니 조심하는 것이 좋겠네요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!\n또 안개도 꽤 많이 있으니 어두울 때는 특히 더 조심하세요!");
                    }
                    else{
                        comment.setText("오늘은 햇빛이 있으면 조금 따스하겠지만 그래도 꽤 쌀쌀해요.\n이런 날씨에 감기가 잘 걸리니 주의하세요!");
                    }
                } else if (12 <= temp && temp <= 17) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 시원한 날씨네요.\n외출할 때 얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 천둥이 내리치고 있으니 외출할 때 주의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")) {
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n밖에는 비가 내리고 있으니 우산이나 우비를 잊지 마세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n맑은 하늘이니 외출이나 드라이브하기도 좋겠네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n좋은 날씨지만 공기 중의 먼지가 있으니 마스크는 필수입니다!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n태풍이 몰아치고 있으니 강한 바람과 비에 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!\n안개가 꽤 있으니 시야확보에 신경써주세요!");
                    }
                    else{
                        comment.setText("오늘은 시원하고 약간은 따스하다고 느낄 수도 있는 나들이 가기 좋은 날씨네요.\n얇고 적당한 겉옷 하나를 챙기면 좋을거 같아요!");
                    }
                } else if (18 <= temp && feel < 23) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n특별히 밖에는 천둥이 치고 있으니 소리에 놀라지 않도록 조심하세요!:)");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n하늘에서 비가 오니 우산, 우비 잊지 말아주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n오늘은 맑은 날을 보낼 수 있는 하늘이네요.:)");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n공기 중에는 먼지가 많은 날이니 마스크를 챙겨주세요.:)");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n또한 태풍이 지나가고 있으니까 외출할 때는 조심해주세요!");
                    }
                    else if(main.equals("Mist")){
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!\n안개도 많이 있으니 외출때 유의해주세요.");
                    }
                    else{
                        comment.setText("오늘은 약간 덥다고 느껴질 수 있는 날씨에요.\n더위를 많이 타시는 분이라면 얇은 옷을 입는 것을 추천드려요!");
                    }
                } else if (23 <= feel && feel < 27) {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n지금 밖에는 천둥이 치고 있어요! 소리에 유의하세요!");
                    }
                    else if(main.equals("이슬비")||main.equals("비")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n그리고 비도 오니 우산은 꼭 챙겨주세요!");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n햇빛이 많은 날씨이니 자외선을 차단해 피부건강을 지켜주세요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n공기 안의 먼지가 많은 날이니 마스크는 필수로 챙겨주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?\n태풍이 지나가고 있어요! 태풍에 대한 대비를 하는 것을 추천드려요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                    else{
                        comment.setText("오늘은 꽤 후덥찌근 날씨입니다.\n외출할 때 부채나 휴대용 선풍기를 챙기는 것이 어떨까요?");
                    }
                } else {
                    if(main.equals("천둥")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n지금은 천둥이 많이 치니 주의해주세요!");
                    }
                    else if(main.equals("이슬비")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n비가 내리니 우산을 꼭꼭 챙겨주세요!:)");
                    }
                    else if(main.equals("비")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n비가 내리니 우산을 꼭꼭 챙겨주세요!:)");
                    }
                    else if(main.equals("맑음")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n태양이 내리쬐는 하루이니 자외선 차단제는 필수이겠네요!");
                    }
                    else if(main.equals("구름")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!");
                    }
                    else if(main.equals("먼지")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!\n공기 중에 먼지가 많으니 답답하더라도 마스크는 꼭 착용해주세요!");
                    }
                    else if(main.equals("태풍")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다..\n얇고 시원한 옷을 입고 모자를 써도 좋을거 같네요!\n날씨도 많이 덥지만 태풍이 지나가고 있으니 조심해주세요!");
                    }
                    else if(main.equals("안개")){
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
                    }
                    else{
                        comment.setText("오늘은 매우 매우 덥고 습한 날씨입니다.\n얇고 시원한 옷을 입고 태양을 피할 수 있는 모자를 써도 좋을거 같네요!");
                    }
                }
                //계절이나 날씨(눈,비,안개 관련된 사항에 관해 상의)
            }
        }
    }



    public void getMyLocation(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //권한 확인 작업
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "설정가서 위치허용을 해주세요", Toast.LENGTH_SHORT).show();
                requestPermissions(gpspermission_list, 0);
                return;
            }else if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(getApplicationContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show();
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