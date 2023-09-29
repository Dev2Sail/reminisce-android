package studio.hcmc.reminisce

// * Map
// TODO 기본 Home으로 이동 (아이콘 및 Nav 수정)
// TODO 주소 검색 : kakao(주소) -> 좌표 받아 naver 변환

// * Folder
// TODO categoryDetail contents 필요 (adapter, viewHolder)
// TODO categoryDetail 월별 구분해서 separator 삽입 .. -> 몽땅 조회해와서 백엔드에서 날짜 구분
// TODO categoryDetail 전체 선택 및 해제 (편집 화면)
// TODO category에 해당하는 location 이 존재하지 않을 경우 header는 카테고리 이름 출력 && '아직 저장된 추억이 없어요' textView 출력
// TODO 태그 클릭 시 location list로 이동
// TODO 단일 location 화면 제작

// * Folder > Write > WriteOptions
// TODO Select category 완성 (recyclerView 단일 선택 && currentCategryId가 기본 선택돼있어야 함)


// * Report
// TODO 일년전 오늘
// TODO 가장 많이 태그된 친구

// * Setting
// TODO 회원탈퇴 시 모든 activity 종료 후 launch 화면으로 이동
// TODO FriendSettingActivity에 recyclerView

// * Common
// TODO RecyclerView Notify()
// TODO activity 이동 시 activityResult 써서 완전히 종료 (appbarBack)
// TODO logger 통일 :
//  Log.v("reminisce Logger", "[reminisce > Account Setting > Prepare user] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
// TODO error handling

// * BackEnd
// TODO locationService 중 put과 patch 개별 작성
// TODO category마다 저장된 location 수 조회


/*

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


