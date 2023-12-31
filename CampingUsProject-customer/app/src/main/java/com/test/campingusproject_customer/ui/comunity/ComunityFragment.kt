package com.test.campingusproject_customer.ui.comunity

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentComunityBinding
import com.test.campingusproject_customer.databinding.HeaderNavigationBinding
import com.test.campingusproject_customer.databinding.RowBoardBinding
import com.test.campingusproject_customer.repository.CustomerUserRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.PostViewModel

class ComunityFragment : Fragment() {
    lateinit var fragmentComunityBinding: FragmentComunityBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback
    lateinit var postViewModel: PostViewModel
    var postType = 1L

    //네비게이션 드로어에 나오는 아이템 메뉴
    val itemList = arrayOf(
        R.id.item_coumnity_all,
        R.id.item_coumnity_popular,
        R.id.item_coumnity_free,
        R.id.item_coumnity_camping,
        R.id.item_coumnity_Humor
    )

    //게시판 종류
    val boardTypeList = arrayOf(
        "전체게시판","인기게시판", "자유게시판", "캠핑게시판", "유머게시판"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentComunityBinding = FragmentComunityBinding.inflate(layoutInflater)

        val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
        val userId =  sharedPreferences.getString("customerUserId", null).toString()

        postViewModel = ViewModelProvider(mainActivity)[PostViewModel::class.java]
        postViewModel.run {
            postDataList.observe(mainActivity) {
                fragmentComunityBinding.recyclerViewComunity.adapter?.notifyDataSetChanged()
            }
        }

        postViewModel.postDataList.value?.clear()
        fragmentComunityBinding.navigationViewComunity.setCheckedItem(R.id.item_coumnity_popular)
        postViewModel.getPostPopularAll()

        postViewModel.resetImageList()

        //홈에서 인기 게시글 더보기 눌렀을 때
        var receiveBoardType:Long = 0L
        if (arguments?.getLong("moreShow") == 1L) {
            mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.menuItemComunity
            receiveBoardType = arguments?.getLong("moreShow")!!
            postType = 1L
            fragmentComunityBinding.navigationViewComunity.setCheckedItem(R.id.item_coumnity_popular)
            Log.d("aaaa","홈에서 인기 게시글 더보기")
        } else {
            receiveBoardType = 0L
        }


        fragmentComunityBinding.run {
        materialToolbarComunityFragment.run {
            if(receiveBoardType == 0L)
                textViewToolbarTitle.text = boardTypeList[postType.toInt()]
            else if(receiveBoardType == 1L){
                textViewToolbarTitle.text = boardTypeList[1]
            }
            setNavigationIcon(R.drawable.menu_24px)
            setNavigationOnClickListener {
                // 키보드가 열려있으면 내림
                val inputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                //검색창에 있는 포커스 지우기
                textInputEditTextComunitySearch.clearFocus()

                //네비게이션 드로어 오픈
                drawerLayoutComunity.open()
            }
        }

        floatingActionButtonComunityAddBoard.run {
            // 플로팅 액션 버튼 아이콘의 색상을 변경
            val fab: FloatingActionButton = findViewById(R.id.floatingActionButtonComunityAddBoard)
            fab.setColorFilter(ContextCompat.getColor(mainActivity, R.color.highLightColor), PorterDuff.Mode.SRC_IN)

            setOnClickListener {
                mainActivity.replaceFragment(MainActivity.POST_WRITE_FRAGMENT,true,true,null)
            }
        }

        //검색
        buttonComunitySearchButton.run {
            setOnClickListener {
                //검색하는 단어가 존재할 경우
                if(textInputEditTextComunitySearch.text?.length != 0) {
                    postViewModel.resetPostList()
                    postViewModel.getSearchPostList(textInputEditTextComunitySearch.text.toString())

                    // 키보드가 열려있으면 내림
                    val inputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                    textViewToolbarTitle.setText("검색결과")
                }
                //검색할 단어 없이 검색 버튼 눌렀을 떄
                else{

                }
            }
        }

        navigationViewComunity.run {
            // 헤더설정
            val headerBinding = HeaderNavigationBinding.inflate(inflater)
            headerBinding.textViewHeadrerName.text = userId
            addHeaderView(headerBinding.root)

            //커뮤니티 들어왔을 때는 전체 게시판을 보여줌
            if(postType == 0L)

            else{ // 게시글 보고 돌아왔을 때
                when(postType){
                    1L ->{
                        setNavigationIcon(itemList[1])
                    }
                    2L ->{
                        setNavigationIcon(itemList[2])
                    }
                    3L ->{
                        setNavigationIcon(itemList[3])
                    }
                    4L ->{
                        setNavigationIcon(itemList[4])
                    }
                }
            }

            //아이템 클릭 시
            setNavigationItemSelectedListener {
                when(it.itemId){
                    //전체 게시판
                    R.id.item_coumnity_all ->{
                        textViewToolbarTitle.text = "전체 게시판"
                        postType = 0L
                        setNavigationIcon(R.id.item_coumnity_all)
                        drawerLayoutComunity.close()
                        postViewModel.getPostAll(postType)
                    }
                    //인기 게시판
                    R.id.item_coumnity_popular ->{
                        textViewToolbarTitle.text = "인기 게시판"
                        postViewModel.getPostPopularAll()
                        postType = 1L
                        setNavigationIcon(R.id.item_coumnity_popular)
                        drawerLayoutComunity.close()
                    }
                    //자유 게시판
                    R.id.item_coumnity_free ->{
                        textViewToolbarTitle.text = "자유 게시판"
                        postType = 2L
                        setNavigationIcon(R.id.item_coumnity_free)
                        drawerLayoutComunity.close()
                        postViewModel.getPostAll(postType)
                    }
                    //캠핑 게시판
                    R.id.item_coumnity_camping ->{
                        textViewToolbarTitle.text = "캠핑 게시판"
                        postType = 3L
                        setNavigationIcon(R.id.item_coumnity_camping)
                        drawerLayoutComunity.close()
                        postViewModel.getPostAll(postType)
                    }
                    //유머 게시판
                    R.id.item_coumnity_Humor ->{
                        textViewToolbarTitle.text = "유머 게시판"
                        postType = 4L
                        setNavigationIcon(R.id.item_coumnity_Humor)
                        drawerLayoutComunity.close()
                        postViewModel.getPostAll(postType)
                    }
                }
                true
            }
        }
        recyclerViewComunity.run {
            adapter = ComunitydAdapter()
            layoutManager = LinearLayoutManager(mainActivity)

            //구분선 추가
            val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
            divider.run {
                setDividerColorResource(mainActivity, R.color.subColor)
                dividerInsetStart = 30
                dividerInsetEnd = 30
            }
            addItemDecoration(divider)
        }
    }

        return fragmentComunityBinding.root
    }

    //게시판 리싸이클러뷰 어댑터
    inner class ComunitydAdapter : RecyclerView.Adapter<ComunitydAdapter.ComunityViewHolder>(){
        inner class ComunityViewHolder(rowComunityBinding: RowBoardBinding) : RecyclerView.ViewHolder(rowComunityBinding.root) {
            val imageViewRowBoardWriterImage : ImageView // 작성자 프로필 사진
            val textViewRowBoardTitle : TextView // 게시글 제목
            val textViewRowBoardWriter : TextView // 게시글 작성자
            val textViewRowBoardLike : TextView // 좋아요 수
            val textVewRowBoardWriteDate : TextView // 글 작성 시간
            val textViewRowBoardComment : TextView // 댓글 수

            init {
                imageViewRowBoardWriterImage = rowComunityBinding.imageViewRowBoardWriterImage
                textViewRowBoardTitle = rowComunityBinding.textViewRowBoardTitle
                textViewRowBoardWriter = rowComunityBinding.textViewRowBoardWriter
                textViewRowBoardLike = rowComunityBinding.textViewRowBoardLike
                textVewRowBoardWriteDate = rowComunityBinding.textViewRowBoardWriteDate
                textViewRowBoardComment = rowComunityBinding.textViewRowBoardComment

                rowComunityBinding.root.setOnClickListener {
                    val readPostIdx = postViewModel.postDataList.value?.get(adapterPosition)?.postIdx
                    val newBundle = Bundle()
                    newBundle.putLong("PostIdx", readPostIdx!!)
                    mainActivity.replaceFragment(MainActivity.POST_READ_FRAGMENT,true,true,newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComunityViewHolder {
            val rowPopularboardBinding = RowBoardBinding.inflate(layoutInflater)

            rowPopularboardBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ComunityViewHolder(rowPopularboardBinding)
        }

        override fun getItemCount(): Int {
            return postViewModel.postDataList.value?.size!!
        }

        override fun onBindViewHolder(holder: ComunityViewHolder, position: Int) {
            // holder.imageViewRowPopularBoardWriterImage =
            if(postViewModel.postDataList.value?.get(position)?.profileImagePath.toString() != "null") {
                CustomerUserRepository.getUserProfileImage(postViewModel.postDataList.value?.get(position)?.profileImagePath!!) {
                    Glide.with(mainActivity)
                        .load(it.result)
                        .into(holder.imageViewRowBoardWriterImage)
                }
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.profileImagePath}")
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.postSubject}")
            }else {
                holder.imageViewRowBoardWriterImage.setImageResource(R.drawable.account_circle_24px)
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.profileImagePath}")
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.postSubject}")
                }

            holder.textViewRowBoardTitle.text = postViewModel.postDataList.value?.get(position)?.postSubject
            holder.textViewRowBoardWriter.text = postViewModel.postDataList.value?.get(position)?.postUserId
            holder.textViewRowBoardLike.text = postViewModel.postDataList.value?.get(position)?.postLiked.toString()
            holder.textVewRowBoardWriteDate.text = postViewModel.postDataList.value?.get(position)?.postWriteDate
            holder.textViewRowBoardComment.text = postViewModel.postDataList.value?.get(position)?.postCommentCount.toString()
        }
    }

    //뒤로가기 버튼 눌렀을 때 동작할 코드 onDetech까지
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.removeFragment(MainActivity.COMUNITY_FRAGMENT)
                mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.menuItemHome
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    //선택된 카테고리에 아이콘 추가
    fun setNavigationIcon(item:Int){
        for(i in itemList){
            if(item == i){
                fragmentComunityBinding.navigationViewComunity.menu.findItem(i).setIcon(R.drawable.circle_20px)
            }
            else{
                fragmentComunityBinding.navigationViewComunity.menu.findItem(i).setIcon(null)
            }
        }
    }
}
