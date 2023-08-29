package com.test.campingusproject_customer.dataclassmodel

import android.os.Parcel
import android.os.Parcelable

data class CartModel(
    var cartUserId: String,         // 유저 ID
    var cartProductId: Long,        // 상품 ID
    var cartProductCount: Long      // 상품 개수
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartUserId)
        parcel.writeLong(cartProductId)
        parcel.writeLong(cartProductCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel> {
        override fun createFromParcel(parcel: Parcel): CartModel {
            return CartModel(parcel)
        }

        override fun newArray(size: Int): Array<CartModel?> {
            return arrayOfNulls(size)
        }
    }

}

data class CartProductModel(
    var productName: String,
    var productPrice: Long,
    var productImage: String,
    var productInfo: String,
)