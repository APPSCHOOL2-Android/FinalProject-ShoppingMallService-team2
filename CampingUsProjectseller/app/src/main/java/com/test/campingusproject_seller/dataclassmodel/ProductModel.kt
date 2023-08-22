package com.test.campingusproject_seller.dataclassmodel

data class ProductModel (
    var productId : Long,                   //상품 ID
    var productSellerId : String,           //상품 판매자 ID
    var productName : String,               //상품 이름
    var productPrice : Long,                //상품 가격
    var productImage : String?,              //상품 이미지
    var productInfo : String,               //상품 정보
    var productCount : Long,                //상품 수량
    var productSellingStatus : Boolean,     //상품 판매상태
    var productDiscountRate : Long,         //상품 할인율
    var productRecommendationCount : Long)  //상품 추천 수