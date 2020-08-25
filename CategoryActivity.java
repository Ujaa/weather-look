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

    private RecyclerView rvCategory;
    private CategoryAdapter adapter;
    private ArrayList<Category> categories;

    private ArrayList<ItemInfo> itemInfoArrayList = new ArrayList<>();
    private ArrayList<ItemInfo> pickThreeItemInfoArray;
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

        rvCategory = findViewById(R.id.rvCategory);

        categories = getCategories();

        adapter = new CategoryAdapter(CategoryActivity.this, categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryActivity.this);
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.setAdapter(adapter);
        //getImages();
        //initRecyclerView();
    }

    private ArrayList<Category> getCategories() {
        // 기온별 옷차림 받아오기
        prepareCategory();

        // 표시할 카테고리와 아이템 종류 갖고오기
        ArrayList<Category> categories = new ArrayList<Category>();
        if (!topArrayList.isEmpty()) {
            Category top = new Category();
            top.categoryName = "상의";
            top.itemCategory = topArrayList;
            top.items = new ArrayList<ItemInfo>();

            // <채영> 상품 정보 크롤링 필요 - getImages() 활용?
            ItemInfo item1 = new ItemInfo("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQrXEonOLDAaP2ZK-tDZaOicLO1qEyfL7NTQg&usqp=CAU",
                    "맨투맨1", "5000", "1", "맨투맨");
            ItemInfo item2 = new ItemInfo("data:image/jpeg;base64,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",
                    "두꺼운 긴팔니트1", "6000", "2", "니트");
            top.items.add(item1);
            top.items.add(item2);
            categories.add(top);
            //System.out.println("top!"+topArrayList);
        }
        if (!bottomArrayList.isEmpty()) {
            Category bottom = new Category();
            bottom.categoryName = "하의";
            bottom.itemCategory = bottomArrayList;
            bottom.items = new ArrayList<ItemInfo>();

            ItemInfo item1 = new ItemInfo("data:image/jpeg;base64,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",
                    "청바지1", "7000", "3", "긴청바지");
            bottom.items.add(item1);
            //System.out.println("하의:"+bottom.items);
            categories.add(bottom);
            //System.out.println("bottom!"+bottomArrayList);
        }
        if (!outerArrayList.isEmpty()) {
            Category outer = new Category();
            outer.categoryName = "아우터";
            outer.itemCategory = outerArrayList;
            outer.items = new ArrayList<ItemInfo>();

            ItemInfo item1 = new ItemInfo("data:image/jpeg;base64,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",
                    "롱코트1", "10000", "4", "롱코트");
            ItemInfo item2 = new ItemInfo("data:image/jpeg;base64,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",
                    "숏패딩1", "12000", "5", "숏패딩");
            ItemInfo item3 = new ItemInfo("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAHcAdwMBIgACEQEDEQH/xAAcAAEAAQUBAQAAAAAAAAAAAAAACAEDBAUHBgL/xAA6EAABAwIEAwUFBwIHAAAAAAABAAIDBBEFBhIhBzFBE1FhgZEUMlJxoSIzQmKSscEksggVI3Ki0eH/xAAUAQEAAAAAAAAAAAAAAAAAAAAA/8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAwDAQACEQMRAD8A7iiIgIiICtzzxU8L5p5GRxRjU973WDR3kr7PJR94k8Rn49K+gw9r2YbC/cE2Mzh+J3h3DzO9rB0LGuKuE0kjo8Mp5a5w27S/Zx+RO59FoDxexAvGnC6TTfkZHX9Vx04hVuNw1tvBq+48Sqmuu+JrreSCR+XuIWEYq1rKl3sNQfwyn7BPg/l62Xr2kOFxuCoq02LQhou7sndz/wDvkuqcKczzyVrcGqJA+mkYXU5LvccN9I8CL7eCDq6IiAiIgIiICIiAiIgt1E0dNBJPO8RxRNL3vdya0C5JUSKiKnNfVl0gkHbv036jUbE/PmpT5kxiDAsFqsRqbFsTPssP43HZrfMqLdbqqp5HyEB8jjI8tHUm5+t0Gtqp9Elo7D5dVkYZJHIXOmFze17qxPSajZouehAvZXYKQsFmAm/O6DMxGWF1PpA57DT+6+8v4liGW8Thr6R+gDlIY9YYCCCdPIkAnbvVDTl8LmE2uFWndojGw5WeHdCglfSVEVVSw1FO8SQyxtex45OaRcFXly/hHmaqqnHB6+Uva2EGkDgLtDdiy/UWsR8j5dQQEREBERAREQEREHheMtXFT5Mkhk066meOOO/Qg6ifRpHmuA00rXRkm2q5BK6JxsxZmIY7Hh7HEx4ewhwB2MjrE+gDR6rmNIxhjlO99Ztv8kGW1rWtvbd3XuHNXYtIAXw5tgSO6yCnidY2d+ooL+uw2WPUz2ewbAu2v12V0UsVtjJ+tYeKwaI2yxknQRcF19kHo8BxaXDMRpa+DeSnkD7fEOo8xcKSdDVQ11HBVUz9UM8bZGHvBFwokUkzwRpeCO6y79wYxQVmW5qF0mqShmIAJuQx27frrHkg6AiIgIiICIiAtJm/H4suYJNXSaXS+5Txk+/IeQ+XU+AK3L3BjC5xAaBck8gFH/iTmk5hxj+nI9gpbsgsff8Aif52HkAg8VjVRJU1M9XM8unmkdI9/K5cbn91raCUkabEgv5reZfwOqzZj9NhNFdvaHVNJb7qIe876gDxIWw4hUNJhGc6rDcOj7OmpmQxtb3Wib6k8795QaQuNtJDhvbcCy+PaWRktcbW5gk7Kkhe2xebjmN1ktf2jQQ4Xt+oILbKth5EnyWzyfHBiebcLoqxgfTVMpilY4c2uaR/K18hIaNYsQdi0cwt7w9m058wFrxcGpIG35HWQebxDDpcExqswurfeajndEbj3gDs4fMWPmui8FK/sM2yU2oBlXSubY83OaQ5v01+q1HG6nFLxFfIGN/qqSGU7e8RqZf/AIfRU4Y1gps7YSdLbSSOjPhqYQPrZBI5ERAREQEREHM+LecIqSlly/QvD6mVoFW5rvumHfSfFw6dx8QuHTucLMhYXOc6wY0XJPQDx8FJHOWQcGzUwy1EZpcQAsytpwA8eDujh4HyIXl8g8KpsDxoYnj1ZBWOp3H2WONptqvs91+tuQ6E8zYIPQcL8njKuCB9U1v+aVgD6p3wfDGPAX9SfBcW4lv7biRjLh7vbtb6RMH7hSc5BRVznK6bPOMufsfbpRYi1rOI/hBrLPIGouuD9kAr4Y6driNEhseYCrIS17mnm13TdXI5T0lHy3QXNT3s+1G+5HdyW6yKXMzjgjiHW9tjG5ta5t/K04fss/LM+jNODm/Kuh/vCD2v+IimEeLYDWBg/wBSKWIn/aWkf3FeJyrUimzDhFSHWDK2Ak9w1gH6ErqH+IinLst4XVBv3NdpLu4OY7+QFxShma2WN5dYMe1x37iEEw0VGm7QR1CqgIiICIiAiIgKImLz+1Y/W1DyXvlrJXk873eVLo7BQ3qy9tVM8iw7VxAv43QZsugCzmCx5FfAhc7dszQ35XTfcdoeXIjYqrXOb7xBHgNv/EH12LxuJuX5Vcwu8GKUVQNZMdRG8uJ22cCrEkptzsO/qvmCUulaS6+k87oJbYnhlDi1N7NidHBV0+oO7Kdge245GxWvgyflmncHwZfwpjhuHCjjuPOy3LDqY094X0goBYWCqiICIiAiIgIiILVU7TTSu7mE/RQ1dIZGNceovbxKmc4BzS0i4OxXBM+cIKihq46jLhdJh1RM2OSJ27qTU4DV+Zgvv1A8LkBzoG7GyCzmkb+aBrHDUQbHpewW+zzQxYVnHFaGBmmKCVoYL76Sxp/lea7UNA70GQ4RM3DBfx3VqaY9i9rXafsnkPBWnONtxYd7tv3W4wvKOYsXcBh+DVsoP4+z0M/W6zfqglRhkvb4dSy/HCx3q0FZSwMAgnpcDw6nq2BlRFSxMlaHXAcGgEX673WegIiICIiAiIgIiICoQiIOe5o4V0WZcwVGLVWKVUBmDAYoGN20tA5m/crmGcIsqUTWdvDU1sjfxTzuF/JlgiIPVYXlvA8J3w3CaKmd8ccLQ715raWREFUREBERAREQf//Z",
                    "롱패딩1", "13000", "6", "롱패딩");
            outer.items.add(item1);
            outer.items.add(item2);
            outer.items.add(item3);
            categories.add(outer);
            System.out.println("outer!");
        }
        if (!dressArrayList.isEmpty()) {
            Category dress = new Category();
            dress.categoryName = "원피스";
            dress.itemCategory = dressArrayList;
            dress.items = new ArrayList<ItemInfo>();

            categories.add(dress);
            System.out.println("dress!");
        }
        if (!accessoryArrayList.isEmpty()) {
            Category accessory = new Category();
            accessory.categoryName = "액세서리";
            accessory.itemCategory = accessoryArrayList;
            accessory.items = new ArrayList<ItemInfo>();

            ItemInfo item1 = new ItemInfo("data:image/jpeg;base64,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",
                    "목도리1", "3000", "7", "목도리");
            accessory.items.add(item1);
            categories.add(accessory);
            System.out.println("accessory");
        }

        System.out.println("Categories size: "+categories.size());
        return categories;
    }

    private void getImages(){
        Thread imgThread = new Thread(new Runnable(){
            public void run() {
                try {
                    // <희> 카테고리 받아온 후 이 정보를 기반으로 크롤링

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
                        pickThreeItemInfoArray = new ArrayList<ItemInfo>(3);
                        int size=0;
                        for(ItemInfo temp : itemInfoArrayList){
                            if(size == 3){
                                break;
                            }
                            pickThreeItemInfoArray.add(size++, temp);
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


    private void prepareCategory(){
        Intent intent = getIntent();
        int temp = intent.getExtras().getInt("temp");
        int feel = intent.getExtras().getInt("feel");
        int humidity = intent.getExtras().getInt("humidity");
        // <희> 테스트용으로 temp, feel, humidity 설정
        temp = 0;
        feel = 0;
        humidity = 0;
        if (feel < -3.2){//매우 추운 날씨
            topArrayList.add("터틀넥");
            topArrayList.add("후드티");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            outerArrayList.add("롱패딩");
            accessoryArrayList.add("목도리");
            //accessoryArrayList.add("귀마개");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }
        if (feel >= -3.2 && temp <= 4) {
            topArrayList.add("맨투맨");
            topArrayList.add("니트");
            bottomArrayList.add("청바지");
            outerArrayList.add("숏패딩");
            outerArrayList.add("롱패딩");
            outerArrayList.add("롱코트");
            accessoryArrayList.add("목도리");
            System.out.println("0도!");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }
        if (5 <= temp && temp <= 8) {//약간 추운 날씨
            topArrayList.add("맨투맨");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            outerArrayList.add("숏패딩");
            outerArrayList.add("롱코트");
            accessoryArrayList.add("목도리");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, accessoryArrayList));
        }
        if (9 <= temp && temp <= 11) {//겨울인데 따뜻할 때
            topArrayList.add("맨투맨");
            topArrayList.add("후드티");
            topArrayList.add("두꺼운 긴팔니트");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            outerArrayList.add("숏코트");
            outerArrayList.add("털자켓");
            outerArrayList.add("후드집업");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, null));
        }
        if (12 <= temp && temp <= 17) {//봄, 가을
            topArrayList.add("맨투맨");
            topArrayList.add("긴팔 셔츠");
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
        }
        if (18 <= temp && feel < 23) {//여름인데 시원할 때
            topArrayList.add("얇은 긴팔티");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
            outerArrayList.add("얇은가디건");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, outerArrayList, null, null));
        }
        if (23 <= feel && feel < 25) {//약간 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("긴팔 셔츠");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
            System.out.println("23도!");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, null, null));
        }
        if(feel >= 25 && humidity > 50){//덥고 습한 날씨
            topArrayList.add("반팔티");
            topArrayList.add("셔츠");
            bottomArrayList.add("반바지");
            bottomArrayList.add("와이드팬츠");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, null, null));
        } else if(feel < 27){//약간 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("긴팔셔츠");
            bottomArrayList.add("긴청바지");
            bottomArrayList.add("슬랙스");
            bottomArrayList.add("롱스커트");
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
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, dressArrayList, null));
        } else if (feel >= 32) {//매우 더운 날씨
            topArrayList.add("반팔티");
            topArrayList.add("나시");
            bottomArrayList.add("반바지");
            dressArrayList.add("나시원피스(숏)");
            dressArrayList.add("반팔원피스(숏)");
            dressArrayList.add("나시원피스(롱)");
            dressArrayList.add("반팔원피스(롱)");
            categoryInfoArrayList.add(new CategoryInfo(topArrayList, bottomArrayList, null, dressArrayList, null));
        }
    }
}
