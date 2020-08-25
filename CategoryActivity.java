package com.dup.tdup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CategoryActivity extends AppCompatActivity {

    private ArrayList<ItemInfo> itemInfoArrayList = new ArrayList<>();
    private ItemInfo[] pickThreeItemInfoArray;
    // <희>
    private ArrayList<CategoryInfo> categoryInfoArrayList = new ArrayList<>();
    private ArrayList<String> topArrayList = new ArrayList<>();
    private ArrayList<String> bottomArrayList = new ArrayList<>();
    private ArrayList<String> outerArrayList = new ArrayList<>();
    private ArrayList<String> dressArrayList = new ArrayList<>();
    private ArrayList<String> accessoryArrayList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        TextView tempText = (TextView) findViewById(R.id.textView_temp);
        /* <희> 체감온도 test*/
        // <희> TextView feelText = (TextView) findViewById(R.id.textView_feel);
        Intent intent = getIntent();
        int temp = intent.getExtras().getInt("temp");
        tempText.setText(String.valueOf(temp));
        // <희> int feel = intent.getExtras().getInt("feel");
        // <희> feelText.setText(String.valueOf(feel));

        getImages();
        initRecyclerView();
    }

    private void getCategory(){
        Intent intent = getIntent();
        int temp = intent.getExtras().getInt("temp");
        int feel = intent.getExtras().getInt("feel");
        // <희> 테스트용으로 temp, feel 설정
        temp = 0;
        feel = 0;
        if (feel >= -3.2 && temp <= 4) {
            topArrayList.add("맨투맨");
            topArrayList.add("니트");
            bottomArrayList.add("청바지");
            outerArrayList.add("숏패딩");
            outerArrayList.add("롱패딩");
            outerArrayList.add("롱코트");
            accessoryArrayList.add("목도리");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }
    }

    private void getImages(){
        Thread imgThread = new Thread(new Runnable(){
            public void run() {
                try {
                    // <희> 카테고리 받아온 후 이 정보를 기반으로 크롤링
                    getCategory();

                    Document doc = Jsoup.connect("https://codibook.net/codi/7979997").get();
                    Elements contents;
                    contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                    String itemImgSrc;
                    String itemName;
                    String itemPrice;
                    String itemID;
                    String itemCategory;

                    for (Element el : contents) {
                        itemImgSrc = el.select("img[class=thumb item]").attr("src");
                        itemName = el.select("div[class=title_wrapper]").text();
                        itemPrice = el.select("div[class=price]>span").text();
                        itemID = el.select("a[class=item_link]").attr("href");
                        itemCategory = "top";

                        itemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        Collections.shuffle(itemInfoArrayList);
                        pickThreeItemInfoArray = new ItemInfo[3];
                        int size=0;
                        for(ItemInfo temp : itemInfoArrayList){
                            if(size == 3){
                                break;
                            }
                            pickThreeItemInfoArray[size++] = temp;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        imgThread.start();
        try{
            imgThread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.category_recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        CategoryAdapter adapter = new CategoryAdapter(this, pickThreeItemInfoArray);
        recyclerView.setAdapter(adapter);
    }
}
