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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        TextView tempText = (TextView) findViewById(R.id.textView_temp);
        Intent intent = getIntent();
        int temp = intent.getExtras().getInt("temp");
        tempText.setText(String.valueOf(temp));

        getImages();
        initRecyclerView();
    }

    private void getImages(){
        Thread imgThread = new Thread(new Runnable(){
            public void run() {
                try {
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
