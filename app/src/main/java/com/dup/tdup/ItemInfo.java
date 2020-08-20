package com.dup.tdup;

public class ItemInfo { // 아이템
    String imgSrc;      // 아이템 이미지
    String name;        // 아이템 이름
    String price;       // 아이템 가격
    String itemID;      //아이템 상세 페이지 주소 관련 아이디
    String category;    //아이템 카테고리

    public ItemInfo(String _imgSrc, String _name, String _price, String _itemID, String _category) {
        super();
        this.imgSrc = _imgSrc;
        this.name = _name;
        this.price = _price;
        this.itemID = _itemID;
        this.category = _category;
    }
}