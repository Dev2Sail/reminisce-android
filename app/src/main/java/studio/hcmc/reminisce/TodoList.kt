package studio.hcmc.reminisce

// TODO
/*
-- 0827
Home / setting / report / map
>> Error Handling

Log.v("reminisce Logger", "[reminisce > setting > signOut] : msg - ${it.message} ::  localMsg - ${it.localizedMessage} :: cause - ${it.cause}")


* home recyclerview 내용 변경 시 알림 diff 때마다 업데이트
* category에 저장된 location 수 조회 후 출력

* write_viewer 완성
* activity 이동 시 activityResult 써서 완전히 종료
* write_options : select category dialog
* AddTagActivity 에서 userId와 tag Body 필요, 버튼 클릭 시 tagIO 요ㅇ

 location 저장 시
tag 작성 시 이미 저장돼있던 태그를 선택하면 id만 추가,
          새롭게 저장할 태그라면 태그의 내용만 추가

location insert 할 때 dto에 두 개 항목 포함시켜서 넘김
tag 중복 검사는 백엔드에서

아 글에서 태그 삭제는 어칼
location_tag 도 삭제,
tag 도 삭제
- 한 트랜잭션 안에서 해야 하는데


오늘추억

* categoryDetail contents 필요
* categoryDetail 월별 구분해서 separator 삽입 .. -> 몽땅 조회해와서 백엔드에서 날짜 구분
* summary item
* category에 해당하는 location 이 존재하지 않을 경우 header는 카테고리 이름 출력 && '아직 저장된 추억이 없어요' textView 출력
 */


/*
서
reminisce Logger 통일 / reminisce > 현재 카테고리
 */

/*
userId, locationId, tagId
tagService add
locationService add
location_tag add 한 트랜잭셩~~~
 */

/*
이전에 쓰던 seed : <color name="seed">#FFD52E</color>


textField 레퍼런스는 activity_friend_add를 볼
 */

// android:paddingVertical="@dimen/margin_regular"
// app:cardElevation="@dimen/card_elevation_standard"
/*
textView.apply {
            writeSelectFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
            root.setOnClickListener {
                if (!checkFlag) {
                    writeSelectFriendIcon.isVisible = true
                    selectedFriendIds.add(friend.opponentId)
                    selectedFriendNicknames.add(writeSelectFriendTitle.text.toString())
                    checkFlag = true
                } else {
                    writeSelectFriendIcon.isVisible = false
                    selectedFriendIds.remove(friend.opponentId)
                    selectedFriendNicknames.remove(writeSelectFriendTitle.text.toString())
                    checkFlag = false
                }
            }
        }
 */