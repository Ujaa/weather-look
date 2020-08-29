package org.sookmyung.weatherlook;

import java.util.ArrayList;

// <희> - 기온별로 달라지는 하위 카테고리 정보 저장
public class CategoryInfo {
    ArrayList<String> top;
    ArrayList<String> bottom;
    ArrayList<String> outer;
    ArrayList<String> dress;
    ArrayList<String> accessory;

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
