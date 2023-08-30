package com.test.campingusproject_customer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_customer.repository.OrderDetailRepository

class OrderDetailViewModel : ViewModel() {
    val orderDeliveryReceiverPhone = MutableLiveData<String>()//배송받는사람 연락처
    val orderDeliveryReceiver = MutableLiveData<String>()//배송받는사람
    val orderUserAddress = MutableLiveData<String>()//주소
    val orderUserName = MutableLiveData<String>()//주문자 이름
    val orderUserPhone = MutableLiveData<String>()//주문자 폰번호
    val orderMeans = MutableLiveData<String>()//결제 정보
    val orderTotalPrice = MutableLiveData<String>()//전체 가격
    val orderStatus = MutableLiveData<String>()//배송상태

    //제품 리스트
    val orderProductList = MutableLiveData<MutableList<OrderProductModel>>()


    fun fetchOrderInfo(userId: String) {
        OrderDetailRepository.getOrderInfoByOrdeNum(userId) { info ->
            if (info.result.exists() == true) {
                for (c1 in info.result.children) {
                    orderDeliveryReceiverPhone.value=c1.child("orderDeliveryContact").value as String
                    orderDeliveryReceiver.value=c1.child("orderDeliveryRecipent").value as String
                    orderUserAddress.value=c1.child("orderDeliveryAddress").value as String
                    orderUserName.value=c1.child("orderCustomerUserName ").value as String
                    orderUserPhone.value=c1.child("orderCustomerUserPhone").value as String
                    orderMeans.value=c1.child("orderPayment").value as String
                    orderStatus.value=c1.child("orderStatus").value as String
                }
            }
        }
    }


    //주문 번호에 해당하는 제품 리스트를 패치하는 메서드
    fun fetchOrderedProductList(userId: String) {
        val tempList = mutableListOf<OrderProductModel>()
        var totalPrice=0
        OrderDetailRepository.getOrderedProductByOrderNum(userId) { product ->
            if (product.result.exists() == true) {
                for (c1 in product.result.children) {
                    val orderId=c1.child("orderId") as String
                    val orderProductName=c1.child("orderProductName") as String
                    val orderProductCount=c1.child("orderProductCount") as String
                    val orderProductPrice=c1.child("orderProductPrice") as String
                    val orderProductImage=c1.child("orderProductImage") as String

                    totalPrice += (orderProductCount.toInt()*orderProductPrice.toInt())
                    val orderProductModel=OrderProductModel(orderId,orderProductName,orderProductCount,orderProductPrice,orderProductImage)
                    tempList.add(orderProductModel)
                }

                orderProductList.value=tempList
                orderTotalPrice.value=totalPrice.toString()
            }
        }
    }


}