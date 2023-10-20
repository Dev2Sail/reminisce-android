package studio.hcmc.reminisce.ui.activity.tag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.Logger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class TagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagDetailBinding
    private lateinit var adapter: TagDetailAdapter
    private val tagId by lazy { intent.getIntExtra("tagId", -1) }
    private val tagBody by lazy { intent.getStringExtra("tagTitle") }

    private lateinit var tag: TagVO
    private lateinit var location: LocationVO
    private lateinit var friend: FriendVO

    private val contents = ArrayList<TagDetailAdapter.TagContents>()

    private lateinit var locations: List<LocationVO>
    private lateinit var tags: List<TagVO>
    private lateinit var friends: List<FriendVO>


    private val locationIds = ArrayList<Int>()
    private val users = HashMap<Int /* UserId */, UserVO>()
    private val dates = ArrayList<String>()

    private val tagDetailContents = ArrayList<TagDetailAdapter.TagContents>()

    private val tagItemContents = ArrayList<TagDetailAdapter.TagDetailTagItemContent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.tagDetailAppbar.appbarTitle.text = getText(R.string.header_view_holder_title)
        viewBinding.tagDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { finish() }

        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagDetailActivity)
        val result = runCatching { LocationIO.listByTagId(tagId) }
            .onSuccess {
                locations = it
                it.forEach {
                    tags = TagIO.listByLocationId(it.id)
                    friends = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                }

                for (friend in friends) {
                    if (friend.nickname == null) {
                        val opponent = UserIO.getById(friend.opponentId)
                        users[opponent.id] = opponent
                    }
                }
            }
    }

    private fun load() = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching {
            val user = UserExtension.getUser(this@TagDetailActivity)

            locations = LocationIO.listByTagId(tagId)
            locations.forEach {
                tags = TagIO.listByLocationId(it.id)
                friends = FriendIO.listByUserIdAndLocationId(user.id, it.id)
            }

            for (friend in friends) {
                if (friend.nickname == null) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }
            }
        }

        if (result.isSuccess) {

        } else {

        }
    }

    private fun prepareDateContents() {
        val prepareDates = locations.groupBy { it.createdAt.toString() }.entries.sortedByDescending { it.key }.distinctBy { it.key.substring(0, 7) }
        for (date in prepareDates) {
            dates.add(date.key)
        }
    }

    private fun prepareItemContents() {
        val tagBuilder = StringBuilder(tags.size)
        for (tag in tags.withIndex()) {
            tagBuilder.append(tag.value.body)
            if (tag.index < tags.size - 1) {
                tagBuilder.append(", ")
            }
        }

        val friendBuilder = StringBuilder(friends.size)
        for (friend in friends.withIndex()) {
            if (friend.value.nickname == null) friendBuilder.append(users[friend.value.opponentId]?.nickname!!) else {
                friendBuilder.append(friend.value.nickname)
                if (friend.index < friends.size - 1) {
                    friendBuilder.append(", ")
                }
            }
        }
    }


    private fun prepareTagContents() {
        // tagContents[0] = TagDetailHeaderContent
        contents.add(TagDetailAdapter.TagDetailHeaderContent(tagBody!!))
        // date divider (1)이되 월별로 삽입돼야 함
//        contents.add(TagDetailAdapter.TagDetailDateDividerContent(dates.))

        // content (2) location
        contents.addAll(locations.map(TagDetailAdapter::TagDetailContent))

    }
    /*
    location을 listByTagId로 조회 -> List<LocationVo> -> locationId로
    얻고자 하는 것 : tag 선택 시 해당 tag가 포함된 locations 받기, 선택된 tag 말고도 다른 tag가 존재할 수 있으며 해당 location에 location_friend가 등록됐을 수도 있는데 이거 다 가져와야 함
    1) tag 선택 시 해당 tagId로 List<LocationVO>
    2) locationId로 location_tag와 location_friend 조회해서 동일한 locationId로 등록된 List<LocationTagVO> 와 List<LocationFriendVO>를 받고 **

    3) List<LocationTagVO>의 tagId로 저장된 tag의 body가 필요하다
    3-1) List<TagVo> listByUserId로 미리 들고 있어라 그리고 id로 body 찾아오기
    4) List<LocationFriendVO>의 opponentId로 저장된 friend의 nickname이 필요하다
    4-1) List<FriendVo>의 nickname이 null이면 users에서 찾아오기(HomeActivity)처럼

    location

     */


    private fun onContentsReady() {
        viewBinding.tagDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = TagDetailAdapter(
            adapterDelegate = adapterDelegate,
            summaryDelegate = summaryDelegate
        )
        viewBinding.tagDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : TagDetailAdapter.Delegate {
        override fun getTag() = tag
        override fun getLocation() = location
        override fun getFriend() = friend
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : TagDetailSummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int) {
            prepareSummaryOnClick(locationId)
        }
    }

    private fun prepareSummaryOnClick(locationId: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                Logger.v("HCMC Logger", "===$it")
            }.onFailure {
                Logger.v(
                    "reminisce Logger",
                    "[reminisce > Tag Detail > prepareSummary] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}"
                )
            }
    }
}