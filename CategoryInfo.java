package com.dup.tdup;

import java.util.ArrayList;

// <희>
public class CategoryInfo { // 카테고리
    ArrayList<String> top;      // 아이템 이미지
    ArrayList<String> bottom;        // 아이템 이름
    ArrayList<String> outer;       // 아이템 가격
    ArrayList<String> dress;      //아이템 상세 페이지 주소 관련 아이디
    ArrayList<String> accessory;    //아이템 카테고리

    public CategoryInfo(ArrayList<String> _top, ArrayList<String> _bottom, ArrayList<String> _outer, ArrayList<String> _dress, ArrayList<String> _accessory) {
        super();
        this.top = _top;
        this.bottom = _bottom;
        this.outer = _outer;
        this.dress = _dress;
        this.accessory = _accessory;
    }

    public ArrayList<String> getTop() {return top;}
    public ArrayList<String> getBottom() {return bottom;}
    public ArrayList<String> getOuter() {return outer;}
    public ArrayList<String> getDress() {return dress;}
    public ArrayList<String> getAccessory() {return accessory;}
}
