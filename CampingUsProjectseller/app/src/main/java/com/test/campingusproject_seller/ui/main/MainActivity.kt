package com.test.campingusproject_seller.ui.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.test.campingusproject_seller.R
import com.test.campingusproject_seller.databinding.ActivityMainBinding
import com.test.campingusproject_seller.ui.inquiry.InquiryDetailFragment
import com.test.campingusproject_seller.ui.inquiry.InquiryFragment
import com.test.campingusproject_seller.ui.product.ManageProductFragment
import com.test.campingusproject_seller.ui.product.ModifyProductFragment
import com.test.campingusproject_seller.ui.product.RegisterProductFragment

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    companion object{
        val MANAGE_PRODUCT_FRAGMENT = "ManageProductFragment"
        val MODIFY_PRODUCT_FRAGMENT = "ModifyProductFragment"
        val REGISTER_PRODUCT_FRAGMENT = "RegisterProductFragment"
        val INQUIRY_FRAGMENT = "InquiryFragment"
        val INQUIRY_DETAIL_FRAGMENT = "InquiryDetailFragment"
    }

    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        requestPermissions(permissionList, 0)

        activityMainBinding.run {

            bottomNavigationViewMain.setOnItemSelectedListener {
                when(it.itemId){
                    //제품관리 클릭
                    R.id.menuItemManageProduct->{
                        replaceFragment(MANAGE_PRODUCT_FRAGMENT, false, false, null)
                    }
                    //판매현황 클릭
                    R.id.menuItemSellStatus->{

                    }
                    //고객문의 클릭
                    R.id.menuItemCustomerAsk->{
                        replaceFragment(INQUIRY_FRAGMENT, false, false, null)
                    }
                    //내정보 클릭
                    R.id.menuItemMyInfo->{

                    }
                }
                false
            }
        }
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name:String, addToBackStack:Boolean, animate:Boolean, bundle:Bundle?){
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 새로운 Fragment를 담을 변수
        var newFragment = when(name){
            MANAGE_PRODUCT_FRAGMENT -> ManageProductFragment()
            MODIFY_PRODUCT_FRAGMENT -> ModifyProductFragment()
            REGISTER_PRODUCT_FRAGMENT -> RegisterProductFragment()
            INQUIRY_FRAGMENT -> InquiryFragment()
            INQUIRY_DETAIL_FRAGMENT -> InquiryDetailFragment()
            else -> Fragment()
        }

        newFragment.arguments = bundle

        if(newFragment != null) {
            // Fragment를 교체한다.
            fragmentTransaction.replace(R.id.fragmentContainerMain, newFragment)

            if (animate == true) {
                // 애니메이션을 설정한다.
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}