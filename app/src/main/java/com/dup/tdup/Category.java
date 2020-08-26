package com.dup.tdup;

import java.util.ArrayList;

// <희> - 상위 카테고리 정보
public class Category {
    public String categoryName;             // 상위 카테고리 이름 (상의, 하의, 아우터, 원피스, 액세서리)
    public ArrayList<String> itemCategory;  // 하위 카테고리 이름 (맨투맨, 니트...)
    public ArrayList<ItemInfo> items;       // 하위 카테고리에 포함된 상품 정보
}
