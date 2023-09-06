package com.test.campingusproject_customer.ui.comunity

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentPostReadBinding
import com.test.campingusproject_customer.databinding.RowPostReadBinding
import com.test.campingusproject_customer.databinding.RowReadPostImageListBinding
import com.test.campingusproject_customer.dataclassmodel.CommentsModel
import com.test.campingusproject_customer.repository.CommentsRepository
import com.test.campingusproject_customer.repository.PostRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.CommentsViewModel
import com.test.campingusproject_customer.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostReadFragment : Fragment() {
    lateinit var fragmentPostReadBinding: FragmentPostReadBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback
    lateinit var postViewModel: PostViewModel
    lateinit var commentsViewModel : CommentsViewModel
    var imageList = mutableListOf<Uri?>()
    var commentsShowList = mutableListOf<CommentsModel>()

    //게시판 종류
    val boardTypeList = arrayOf(
        "전체게시판","인기게시판", "자유게시판", "캠핑게시판", "유머게시판"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentPostReadBinding = FragmentPostReadBinding.inflate(layoutInflater)

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
        val userId =  sharedPreferences.getString("customerUserId", null).toString()

        postViewModel = ViewModelProvider(mainActivity)[PostViewModel::class.java]
        postViewModel.run {
            postSubject.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadPostTitle.setText(it)
            }
            postText.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadPostContents.setText(it)
            }
            postUserId.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadUserName.setText(it)
            }
            postLiked.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadPostLike.setText(it.toString())
            }
            postCommentCount.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadPostComent.setText(it.toString())
            }
            postWriteDate.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadWriteDate.setText(it)
            }
            postType.observe(mainActivity){
                fragmentPostReadBinding.textViewPostReadToolbarTitle.text = boardTypeList[it.toInt()]
            }
            postImageList.observe(mainActivity){
                imageList = it
                fragmentPostReadBinding.recyclerViewPostReadImage.adapter?.notifyDataSetChanged()
            }
        }
        commentsViewModel = ViewModelProvider(mainActivity)[CommentsViewModel::class.java]
        commentsViewModel.run {
            commentsList.observe(mainActivity){
                commentsShowList = it
                fragmentPostReadBinding.recyclerViewComments.adapter?.notifyDataSetChanged()
            }
        }

        //번들로 게시글 번호 가져오기
        val postIdx = arguments?.getLong("PostIdx")
        //해당 게시글 내용 가져오기
        postViewModel.getOnePostReadData(postIdx?.toDouble()!!)
        //게시글 댓글 가져오기
        commentsViewModel.getCommentsList(postIdx)

        fragmentPostReadBinding.run {
            materialToolbarPostRead.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    // 키보드가 열려있으면 내림
                    val inputMethodManager =
                        mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                    mainActivity.removeFragment(MainActivity.POST_READ_FRAGMENT)
                }
            }
            recyclerViewComments.run {
                adapter = PostReadAdapter()
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
            recyclerViewPostReadImage.run {
                adapter = PhotoAdapter()
                layoutManager = LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false)
            }

            //댓글
            textInputEditTextPostReadInputComments.addTextChangedListener {
                val currentTextLength = it?.length ?: 0
                val maxLengthResId = R.integer.textInputEditText_max_length // 여기에 리소스 ID를 넣어주세요
                val maxLength = resources.getInteger(maxLengthResId)

                // 현재 글자 수와 최대 글자 수를 표시
                fragmentPostReadBinding.textViewPostReadComentTextCount.text =
                    "($currentTextLength / $maxLength)"
            }
            //댓글 저장 버튼
            buttonPostReadSaveButton.setOnClickListener {
                val inputMethodManager =
                    mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                //댓글 내용
                val postCommentsContents = textInputEditTextPostReadInputComments.text.toString()

                // 작성일
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val commentsWriteDate = sdf.format(Date(System.currentTimeMillis())).toString()

                //게시글 번호
                val postCommentsPostIdx = postIdx

                //댓글 남긴 유저 Id
                val postCommentsUserId = userId

                //댓글 Idx
                var postCommentsCommentsIdx:Long = 0L

                //db 인덱스 증가
                CommentsRepository.getCommentsIdx {
                    postCommentsCommentsIdx = it.result.value as Long
                    postCommentsCommentsIdx++

                    //객체 생성
                    val commentsModel = CommentsModel(postCommentsPostIdx,postCommentsCommentsIdx,postCommentsUserId,postCommentsContents,commentsWriteDate)

                    //댓글 저장
                    CommentsRepository.addComments(commentsModel){
                        //Idx 증가
                        CommentsRepository.setCommentsIdx(postCommentsCommentsIdx){
                            //댓글 입력한 거 비우기
                            textInputEditTextPostReadInputComments.setText("")
                            //댓글 리스트 불러오기
                            commentsViewModel.getCommentsList(postCommentsPostIdx)

                            //게시글 db에 저장된 댓글 수 변경
                            var commentsCount = postViewModel.postCommentCount.value!!
                            PostRepository.modifyPostCommentsCount(postIdx,commentsCount){
                                postViewModel.postCommentCount.value = commentsCount+1L
                            }
                        }
                    }
                }
            }
            textViewPostReadPostLike.run {
                isClickable = true
                setOnClickListener {
                    var likedCount = postViewModel.postLiked.value!!
                    PostRepository.modifyPostLikedCount(postIdx,likedCount){
                        postViewModel.postLiked.value = likedCount+1
                    }
                }
            }
        }
        return fragmentPostReadBinding.root
    }

    //이미지 리사이클러뷰
    inner class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>(){
        inner class PhotoViewHolder(rowReadPostImageListBinding: RowReadPostImageListBinding) : RecyclerView.ViewHolder(rowReadPostImageListBinding.root) {
            val imageViewRowPostImage : ImageView // 게시판 사진
            init {
                imageViewRowPostImage = rowReadPostImageListBinding.imageViewReadPostImage
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val rowReadPostImageListBinding = RowReadPostImageListBinding.inflate(layoutInflater)

            return PhotoViewHolder(rowReadPostImageListBinding)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            //글라이드 라이브러리로 recycler view에 이미지 출력
            Glide.with(mainActivity).load(imageList[position])
                .override(500, 500)
                .into(holder.imageViewRowPostImage)
        }
    }

    //댓글 리사이클러 뷰
    inner class PostReadAdapter : RecyclerView.Adapter<PostReadFragment.PostReadAdapter.PostReadViewHolder>(){
        inner class PostReadViewHolder(rowPostReadBinding: RowPostReadBinding) : RecyclerView.ViewHolder(rowPostReadBinding.root) {
            val textViewRowCommentUserName : TextView // 유저 이름
            val textViewRowCommentWriteDate : TextView // 댓글 작성 날짜
            val textViewRowCommentContents : TextView // 댓글 내용

            init {
                textViewRowCommentUserName = rowPostReadBinding.textViewRowCommentUserName
                textViewRowCommentWriteDate = rowPostReadBinding.textViewRowCommentWriteDate
                textViewRowCommentContents = rowPostReadBinding.textViewRowCommentContents
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostReadViewHolder {
            val rowPostReadBinding = RowPostReadBinding.inflate(layoutInflater)

            rowPostReadBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PostReadViewHolder(rowPostReadBinding)
        }

        override fun getItemCount(): Int {
            return commentsShowList.size
        }

        override fun onBindViewHolder(holder: PostReadViewHolder, position: Int) {
            holder.textViewRowCommentUserName.text = commentsShowList[position].userId
            holder.textViewRowCommentWriteDate.text = commentsShowList[position].writeDate
            holder.textViewRowCommentContents.text = commentsShowList[position].contents
        }
    }


    //뒤로가기 버튼 눌렀을 때 동작할 코드 onDetech까지
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.removeFragment(MainActivity.POST_READ_FRAGMENT)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}