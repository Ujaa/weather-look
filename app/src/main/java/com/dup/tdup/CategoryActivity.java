package com.dup.tdup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

    private RecyclerView rvCategory;
    private CategoryAdapter adapter;
    private ArrayList<Category> categories;

    private ArrayList<ItemInfo> TopitemInfoArrayList = new ArrayList<>();
    private ArrayList<ItemInfo> BottomitemInfoArrayList = new ArrayList<>();
    private ArrayList<ItemInfo> OuteritemInfoArrayList = new ArrayList<>();
    private ArrayList<ItemInfo> DressitemInfoArrayList = new ArrayList<>();
    private ArrayList<ItemInfo> AccessoryitemInfoArrayList = new ArrayList<>();

    private ArrayList<ItemInfo> pickItemTopInfoArray;
    private ArrayList<ItemInfo> pickItemBottomInfoArray;
    private ArrayList<ItemInfo> pickItemOuterInfoArray;
    private ArrayList<ItemInfo> pickItemDressInfoArray;
    private ArrayList<ItemInfo> pickItemAccessoryInfoArray;
    // <희>
    private ArrayList<CategoryInfo> categoryInfoArrayList = new ArrayList<>();
    private ArrayList<String> topArrayList = new ArrayList<>();
    private ArrayList<String> bottomArrayList = new ArrayList<>();
    private ArrayList<String> outerArrayList = new ArrayList<>();
    private ArrayList<String> dressArrayList = new ArrayList<>();
    private ArrayList<String> accessoryArrayList = new ArrayList<>();

    private ArrayList<String> urlTopList = new ArrayList<>();
    private ArrayList<String> urlBottomList = new ArrayList<>();
    private ArrayList<String> urlOuterList = new ArrayList<>();
    private ArrayList<String> urlDressList = new ArrayList<>();
    private ArrayList<String> urlAccessoryList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rvCategory = findViewById(R.id.rvCategory);

        categories = getCategories();

        adapter = new CategoryAdapter(CategoryActivity.this, categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryActivity.this);
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.setAdapter(adapter);
        //getImages();
        //initRecyclerView();
    }

    // 화면에 보여줄 정보 (상위 카테고리, 상품 정보)
    private ArrayList<Category> getCategories() {
        // 기온별 옷차림 받아오기
        CategoryInfo categoryNow = prepareCategory();

        // 어떤 옷을 추천해주는지 표시하는 텍스트
        TextView topText = (TextView) findViewById(R.id.tvTop);
        TextView bottomText = (TextView) findViewById(R.id.tvBottom);
        TextView outerText = (TextView) findViewById(R.id.tvOuter);
        TextView dressText = (TextView) findViewById(R.id.tvDress);
        TextView accessoryText = (TextView) findViewById(R.id.tvAccessory);

        // 옷 카테고리 아이콘
        ImageView topIV = (ImageView) findViewById(R.id.top_icon_imgview);
        ImageView bottomIV = (ImageView) findViewById(R.id.bottom_icon_imgview);
        ImageView outerIV = (ImageView) findViewById(R.id.outer_icon_imgview);
        ImageView dressIV = (ImageView) findViewById(R.id.dress_icon_imgview);
        ImageView accessoryIV = (ImageView) findViewById(R.id.acc_icon_imgview);

        // TextView 에 setText 하기 전, [중괄호]를 없애기 위한 임시 문자열
        String topString, bottomString, outerString, dressString, accessoryString;

        getImages();

        // 표시할 카테고리와 아이템 종류 갖고오기
        ArrayList<Category> categories = new ArrayList<Category>();
        if (!topArrayList.isEmpty()) {
            topIV.setImageResource(R.drawable.top_icon);
            Category top = new Category();
            top.categoryName = "상의";
            top.itemCategory = topArrayList;
            top.items = new ArrayList<ItemInfo>();

            // <채영> 상품 정보 크롤링 필요 - getImages() 활용?
            for(ItemInfo item :pickItemTopInfoArray){
                top.items.add(item);
            }
            categories.add(top);

            topString = ((categoryNow.getTop()).toString()).replaceAll("\\[","").replaceAll(", ","\n").replaceAll("\\]","");
            topText.setText(topString);
        }else{
            topIV.setImageResource(R.drawable.no_top_icon);
            topText.setText("없음");
        }
        if (!bottomArrayList.isEmpty()) {
            bottomIV.setImageResource(R.drawable.bottom_icon);
            Category bottom = new Category();
            bottom.categoryName = "하의";
            bottom.itemCategory = bottomArrayList;
            bottom.items = new ArrayList<ItemInfo>();

            for(ItemInfo item :pickItemBottomInfoArray){
                bottom.items.add(item);
            }
            categories.add(bottom);

            bottomString =  ((categoryNow.getBottom()).toString()).replaceAll("\\[","").replaceAll(", ","\n").replaceAll("\\]","");
            bottomText.setText(bottomString);
        }else{
            bottomIV.setImageResource(R.drawable.no_bottom_icon);
            bottomText.setText("없음");
        }
        if (!outerArrayList.isEmpty()) {
            outerIV.setImageResource(R.drawable.outer_icon);
            Category outer = new Category();
            outer.categoryName = "아우터";
            outer.itemCategory = outerArrayList;
            outer.items = new ArrayList<ItemInfo>();

            for(ItemInfo item :pickItemOuterInfoArray){
                outer.items.add(item);
            }
            categories.add(outer);

            outerString =  ((categoryNow.getOuter()).toString()).replaceAll("\\[","").replaceAll(", ","\n").replaceAll("\\]","");
            outerString = outerString.replace("청자켓\n가죽자켓\n면자켓", "자켓");
            outerText.setText(outerString);
        }else{
            outerIV.setImageResource(R.drawable.no_outer_icon);
            outerText.setText("없음");
        }
        if (!dressArrayList.isEmpty()) {
            dressIV.setImageResource(R.drawable.dress_icon);
            Category dress = new Category();
            dress.categoryName = "원피스";
            dress.itemCategory = dressArrayList;
            dress.items = new ArrayList<ItemInfo>();

            for(ItemInfo item :pickItemDressInfoArray){
                dress.items.add(item);
            }
            categories.add(dress);

            dressString =  ((categoryNow.getDress()).toString()).replaceAll("\\[","").replaceAll(", ","\n").replaceAll("\\]","");
            dressString = dressString.replace("나시원피스(롱)\n나시원피스(숏)", "나시원피스")
                    .replace("반팔원피스(롱)\n반팔원피스(숏)", "반팔원피스")
                    .replace("긴팔원피스(롱)\n긴팔원피스(숏)", "긴팔원피스");
            dressText.setText(dressString);
        }else{
            dressIV.setImageResource(R.drawable.no_dress_icon);
            dressText.setText("없음");
        }
        if (!accessoryArrayList.isEmpty()) {
            accessoryIV.setImageResource(R.drawable.acc_icon);
            Category accessory = new Category();
            accessory.categoryName = "액세서리";
            accessory.itemCategory = accessoryArrayList;
            accessory.items = new ArrayList<ItemInfo>();

            for(ItemInfo item :pickItemAccessoryInfoArray){
                accessory.items.add(item);
            }
            categories.add(accessory);

            accessoryString =  ((categoryNow.getAccessory()).toString()).replaceAll("\\[","").replaceAll(", ","\n").replaceAll("\\]","");
            accessoryText.setText(accessoryString);
        }else{
            accessoryIV.setImageResource(R.drawable.no_acc_icon);
            accessoryText.setText("없음");
        }

        return categories;
    }

    private void getImages(){
        Thread imgThread = new Thread(new Runnable(){
            public void run() {
                try {
                    // <희> 카테고리 받아온 후 이 정보를 기반으로 크롤링
                    String itemImgSrc;
                    String itemName;
                    String itemPrice;
                    String itemID;
                    String itemCategory;
                    int size;
                    Document doc;
                    Elements contents;
                    //상의
                    for(String cate :topArrayList){
                        if(cate=="반팔티") urlTopList.add("https://codibook.net/codi/7979674");
                        if(cate=="나시") urlTopList.add("https://codibook.net/codi/7979677");
                        if(cate=="반팔셔츠") urlTopList.add("https://codibook.net/codi/7979681");
                        if(cate=="블라우스") urlTopList.add("https://codibook.net/codi/7979964");
                        if(cate=="얇은 긴팔티") urlTopList.add("https://codibook.net/codi/7979973");
                        if(cate=="긴팔셔츠") urlTopList.add("https://codibook.net/codi/7979986");
                        if(cate=="두꺼운 긴팔니트") urlTopList.add("https://codibook.net/codi/7979992");
                        if(cate=="후드티") urlTopList.add("https://codibook.net/codi/7979415");
                        if(cate=="터틀넥") urlTopList.add("https://codibook.net/codi/7980354");
                        if(cate=="맨투맨") urlTopList.add("https://codibook.net/codi/7980360");
                    }
                    for(String url : urlTopList){
                        doc = Jsoup.connect(url).get();
                        contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                        for (Element el : contents) {
                            itemImgSrc = el.select("img[class=thumb item]").attr("src");
                            itemName = el.select("div[class=title_wrapper]").text();
                            itemPrice = el.select("div[class=price]>span").text();
                            itemID = el.select("a[class=item_link]").attr("href");
                            itemCategory = "top";

                            TopitemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        }
                        Collections.shuffle(TopitemInfoArrayList);
                        pickItemTopInfoArray = new ArrayList<ItemInfo>();
                        size=0;
                        for(ItemInfo temp : TopitemInfoArrayList){
                            if(size == 10){
                                break;
                            }
                            pickItemTopInfoArray .add(size++, temp);
                        }
                    }
                    //하의
                    for(String cate :bottomArrayList){
                        if(cate=="긴청바지") urlBottomList.add("https://codibook.net/codi/7979277");
                        if(cate=="슬랙스") urlBottomList.add("https://codibook.net/codi/7979788");
                        if(cate=="반바지") urlBottomList.add("https://codibook.net/codi/7978096");
                        if(cate=="숏스커트") urlBottomList.add("https://codibook.net/codi/7979281");
                        if(cate=="롱스커트") urlBottomList.add("https://codibook.net/codi/7979289");
                        if(cate=="와이드팬츠") urlBottomList.add("https://codibook.net/codi/7979785");
                    }
                    for(String url : urlBottomList){
                        doc = Jsoup.connect(url).get();
                        contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                        for (Element el : contents) {
                            itemImgSrc = el.select("img[class=thumb item]").attr("src");
                            itemName = el.select("div[class=title_wrapper]").text();
                            itemPrice = el.select("div[class=price]>span").text();
                            itemID = el.select("a[class=item_link]").attr("href");
                            itemCategory = "trousers";
                            if(url=="https://codibook.net/codi/7979281" || url=="https://codibook.net/codi/7978096") itemCategory = "shorts_n_skirts";

                            BottomitemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        }
                        Collections.shuffle(BottomitemInfoArrayList);
                        pickItemBottomInfoArray = new ArrayList<ItemInfo>();
                        size=0;
                        for(ItemInfo temp : BottomitemInfoArrayList){
                            if(size == 10){
                                break;
                            }
                            pickItemBottomInfoArray .add(size++, temp);
                        }
                    }
                    //아우터
                    for(String cate :outerArrayList){
                        if(cate=="얇은가디건") urlOuterList.add("https://codibook.net/codi/7979374");
                        if(cate=="두꺼운가디건") urlOuterList.add("https://codibook.net/codi/7979384");
                        if(cate=="청자켓") urlOuterList.add("https://codibook.net/codi/7979388");
                        if(cate=="면자켓") urlOuterList.add("https://codibook.net/codi/7979394");
                        if(cate=="가죽자켓") urlOuterList.add("https://codibook.net/codi/7979399");
                        if(cate=="털자켓") urlOuterList.add("https://codibook.net/codi/7979401");
                        if(cate=="롱코트") urlOuterList.add("https://codibook.net/codi/7979406");
                        if(cate=="숏코트") urlOuterList.add("https://codibook.net/codi/7979408");
                        if(cate=="숏패딩") urlOuterList.add("https://codibook.net/codi/7979409");
                        if(cate=="롱패딩") urlOuterList.add("https://codibook.net/codi/7979410");
                        if(cate=="후드집업") urlOuterList.add("https://codibook.net/codi/7979415");
                        if(cate=="트렌치코트") urlOuterList.add("https://codibook.net/codi/7979418");
                    }
                    for(String url : urlOuterList){
                        doc = Jsoup.connect(url).get();
                        contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                        for (Element el : contents) {
                            itemImgSrc = el.select("img[class=thumb item]").attr("src");
                            itemName = el.select("div[class=title_wrapper]").text();
                            itemPrice = el.select("div[class=price]>span").text();
                            itemID = el.select("a[class=item_link]").attr("href");
                            itemCategory = "top";
                            if(url=="https://codibook.net/codi/7979406" || url=="https://codibook.net/codi/7979410" || url=="https://codibook.net/codi/7979418") itemCategory = "long_wears";

                            OuteritemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        }
                        Collections.shuffle(OuteritemInfoArrayList);
                        pickItemOuterInfoArray = new ArrayList<ItemInfo>();
                        size=0;
                        for(ItemInfo temp : OuteritemInfoArrayList){
                            if(size == 10){
                                break;
                            }
                            pickItemOuterInfoArray .add(size++, temp);
                        }
                    }
                    //원피스
                    for(String cate :dressArrayList){
                        if(cate=="나시원피스(숏)") urlDressList.add("https://codibook.net/codi/7980589");
                        if(cate=="나시원피스(롱)") urlDressList.add("https://codibook.net/codi/7980593");
                        if(cate=="반팔원피스(숏)") urlDressList.add("https://codibook.net/codi/7980596");
                        if(cate=="반팔원피스(롱)") urlDressList.add("https://codibook.net/codi/7980598");
                        if(cate=="긴팔원피스(숏)") urlDressList.add("https://codibook.net/codi/7980607");
                        if(cate=="긴팔원피스(롱)") urlDressList.add("https://codibook.net/codi/7980603");
                    }
                    for(String url : urlDressList){
                        doc = Jsoup.connect(url).get();
                        contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                        for (Element el : contents) {
                            itemImgSrc = el.select("img[class=thumb item]").attr("src");
                            itemName = el.select("div[class=title_wrapper]").text();
                            itemPrice = el.select("div[class=price]>span").text();
                            itemID = el.select("a[class=item_link]").attr("href");
                            itemCategory = "long_wears";

                            DressitemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        }
                        Collections.shuffle(DressitemInfoArrayList);
                        pickItemDressInfoArray = new ArrayList<ItemInfo>();
                        size=0;
                        for(ItemInfo temp : DressitemInfoArrayList){
                            if(size == 10){
                                break;
                            }
                            pickItemDressInfoArray .add(size++, temp);
                        }
                    }
                    //악세사리
                    for(String cate :accessoryArrayList){
                        if(cate=="목도리") urlAccessoryList.add("https://codibook.net/codi/7983636");
                        if(cate=="여름모자") urlAccessoryList.add("https://codibook.net/codi/7983618");
                        if(cate=="겨울모자") urlAccessoryList.add("https://codibook.net/codi/7983629");
                    }
                    for(String url : urlAccessoryList){
                        doc = Jsoup.connect(url).get();
                        contents = doc.select("div[class=grid set_wrapper]>div[class=set_container] div[class=set item]");
                        for (Element el : contents) {
                            itemImgSrc = el.select("img[class=thumb item]").attr("src");
                            itemName = el.select("div[class=title_wrapper]").text();
                            itemPrice = el.select("div[class=price]>span").text();
                            itemID = el.select("a[class=item_link]").attr("href");
                            itemCategory = "hats";
                            if(url=="https://codibook.net/codi/7983636") itemCategory = "scarves";

                            AccessoryitemInfoArrayList.add(new ItemInfo(itemImgSrc, itemName, itemPrice, itemID, itemCategory));
                        }
                        Collections.shuffle(AccessoryitemInfoArrayList);
                        pickItemAccessoryInfoArray = new ArrayList<ItemInfo>();
                        size=0;
                        for(ItemInfo temp : AccessoryitemInfoArrayList){
                            if(size == 10){
                                break;
                            }
                            pickItemAccessoryInfoArray .add(size++, temp);
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
        RecyclerView recyclerView = findViewById(R.id.rvCategory);
        CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this, categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    // 기온별 옷차림
    private CategoryInfo prepareCategory(){
        Intent intent = getIntent();
        int temp = intent.getExtras().getInt("temp");
        int feel = intent.getExtras().getInt("feel");
        int humidity = intent.getExtras().getInt("humidity");
        // <희> 테스트용으로 temp, feel, humidity 설정
//        temp = 33;
//        feel = 33;
//        humidity = 10;
        if (feel < -3){//매우 추운 날씨
            topArrayList.add("터틀넥");
            topArrayList.add("후드티");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            outerArrayList.add("롱패딩");
            accessoryArrayList.add("목도리");
            accessoryArrayList.add("겨울모자");
            //accessoryArrayList.add("귀마개");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }else if (feel >= -3 && temp <= 4) {
            topArrayList.add("맨투맨");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            outerArrayList.add("숏패딩");
            outerArrayList.add("롱패딩");
            outerArrayList.add("롱코트");
            accessoryArrayList.add("목도리");
            accessoryArrayList.add("겨울모자");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
            //CategoryInfo zero = categoryInfoArrayList.get(0);
            //zero.getTop();
            //System.out.println("0도!"+categoryInfoArrayList.get(categoryInfoArrayList.size()-1).getTop());
            //return categoryInfoArrayList.get(categoryInfoArrayList.size()-1);
        }else if (5 <= temp && temp <= 8) {//약간 추운 날씨
            topArrayList.add("맨투맨");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            outerArrayList.add("숏패딩");
            outerArrayList.add("롱코트");
            accessoryArrayList.add("목도리");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }else if (9 <= temp && temp <= 11) {//겨울인데 따뜻할 때
            topArrayList.add("맨투맨");
            topArrayList.add("후드티");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            outerArrayList.add("숏코트");
            outerArrayList.add("털자켓");
            outerArrayList.add("후드집업");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, null));
        }else if (12 <= temp && temp <= 17) {//봄, 가을
            topArrayList.add("맨투맨");
            topArrayList.add("긴팔셔츠");
            topArrayList.add("블라우스");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            outerArrayList.add("두꺼운가디건");
            outerArrayList.add("청자켓");
            outerArrayList.add("가죽자켓");
            outerArrayList.add("면자켓");
            outerArrayList.add("트렌치코트");
            dressArrayList.add("긴팔원피스(롱)");
            dressArrayList.add("긴팔원피스(숏)");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, dressArrayList, null));
        }else if (18 <= temp && feel < 23) {//여름인데 시원할 때
            topArrayList.add("얇은 긴팔티");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
            outerArrayList.add("얇은가디건");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, null));
        }else if (23 <= feel && feel < 27) {//약간 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("긴팔셔츠");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, null, null));
        }else if(feel >= 25 && humidity > 50){//덥고 습한 날씨
            topArrayList.add("반팔티");
            topArrayList.add("반팔셔츠");
            bottomArrayList.add("반바지");
            bottomArrayList.add("와이드팬츠");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, null, null));
        } else if (27 <= feel && feel < 32) {//적당히 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("반팔셔츠");
            bottomArrayList.add("와이드팬츠");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
            bottomArrayList.add("숏스커트");
            dressArrayList.add("반팔원피스(숏)");
            dressArrayList.add("반팔원피스(롱)");
            accessoryArrayList.add("여름모자");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, dressArrayList, accessoryArrayList));
        } else if (feel >= 32) {//매우 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("나시");
            bottomArrayList.add("반바지");
            dressArrayList.add("나시원피스(숏)");
            dressArrayList.add("반팔원피스(숏)");
            dressArrayList.add("나시원피스(롱)");
            dressArrayList.add("반팔원피스(롱)");
            accessoryArrayList.add("여름모자");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, dressArrayList, accessoryArrayList));
        }
        return categoryInfoArrayList.get(categoryInfoArrayList.size()-1);
    }
}
