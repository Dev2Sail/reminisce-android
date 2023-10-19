package studio.hcmc.reminisce.ext.modal

import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

sealed interface FriendTagDetailContents

class FriendTagDetailHeaderContent(val title: String): FriendTagDetailContents

class FriendTagDetailSummaryContent(val friend: FriendVO, val location: LocationVO, val tag: TagVO?): FriendTagDetailContents

class FriendTagDetailDateDividerContent(val body: String): FriendTagDetailContents