package studio.hcmc.reminisce

// * Map
// TODO 주소 검색 : kakao(주소) -> 좌표 받아 naver 변환
// TODO ~시 ~구로 조회

// * Writer
// TODO WriteDetail recyclerView (writeDetailDate + writeDetailLocation + writeDetailMemo + writeDetailOptions)

// * Folder

// TODO 단일 location 화면 제작

// * Folder > Write > WriteOptions
// TODO Select category 완성 (recyclerView 단일 선택 && currentCategryId가 기본 선택돼있어야 함)


// * Report
// TODO 일년전 오늘
// TODO 가장 많이 태그된 친구
// TODO 이번 달에 저장된 추억의 수

// * Setting
// TODO 회원탈퇴 시 모든 activity 종료 후 launch 화면으로 이동

// * Common
// TODO RecyclerView Notify()
// TODO activity 이동 시 activityResult 써서 완전히 종료 (appbarBack)

// TODO logger 통일 :

// TODO error handling

// TODO network error 판별


// 요청 경로, method, protocol, query결과
// 1) 클라이언트에서 요청
// 2) tomcat에서 filter에서 catch 후 readByte를 wrapper로 감싸고
// 3) filter -> interceptor 에서 logger 호출
// 4) 최종적으로 들어온 요청을 interceptor에서 controller 호출



// TODO XML make standard
// card_common_header -> activity header
// card_common_detail_header -> detail activity header -> tag activity or Friend activity에서 사용 (편집 불가능한 헤더)
// card_category_detail_editable_header -> detail activity header -> category Detail에서만 사용 (편집 가능한 헤더)
// card_summary -> 모든 summary 통일
// card_checkable_summary -> 모든 activity 편집 화면에서 사용
// layout_summary_item -> 모든 summary 내 아이템


// add location -> locationId
// tag가 여러 개일 경우
// tag가 저장될 때 location_tag에도 한 트랜잭션에서 동시에 저장


// location_friend -> friend list ->
// location_tag


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
이전에 쓰던 seed : <color name="seed">#FFD52E</color>


textField 레퍼런스는 activity_friend_add를 볼
 */

/*
public int update(
            int id,
            String password,
            String nickname,
            DatabaseProcessor databaseProcessor
    ) {
        final var builder = new StringBuilder("UPDATE user SET ");
        final var arguments = new ArrayList<>(2);
        if (password != null) {
            builder.append(" password=?");
            arguments.add(password);
        }
        if (nickname != null) {
            if (!arguments.isEmpty()) {
                builder.append(",");
            }
            builder.append(" nickname=?");
            arguments.add(nickname);
        }
        if (arguments.isEmpty()) {
            return 0;
        }
        builder.append(" WHERE _id=").append(id);

        return databaseProcessor.runParameterizedUpdate(builder.toString(), arguments.toArray());
    }
 */

/*
SELECT *
FROM location AS l
JOIN location_tag AS lt ON
	l._id = lt.location_id AND
    l._id = 2
JOIN location_friend AS lf ON
	l._id = lf.location_id
WHERE l.is_deleted = 0
ORDER BY l.created_at DESC
LIMIT 10;
 */

/*
tagId와 locationIds들로 삭제
한 트랜잭션 안에서
1) location_tag에서 삭제
2) location에서 삭제
 */