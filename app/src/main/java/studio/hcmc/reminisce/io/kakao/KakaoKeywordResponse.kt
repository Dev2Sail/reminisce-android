package studio.hcmc.reminisce.io.kakao

sealed interface KakaoKeywordResponse

data class KKResponse(
    val meta: KKMeta,
    val documents: List<KKPlaceInfo>
): KakaoKeywordResponse

data class KKMeta(
    val total_count: Int, // 검색어에 검색된 문서 수
    val pageable_count: Int, // total_count 중 노출 가능 문서 수 (max: 45)
    val is_end: Boolean, // 현재 페이지가 마지막 페이지인지 여부 값이 false면 다음 요청 시 page 값 증가시켜 다음 페이지 요청 가능
    val same_name: KKSameName // 질의어의 지역 및 키워드 분석 정보
): KakaoKeywordResponse

data class KKSameName(
    val region: List<String>, // 질의어에서 인식된 지역 리스트
    val keyword: String, // 질의어에서 지역 정보 제외한 키워드
    val selected_region: String // 인식된 지역 리스트 중 현재 검색에 사용된 지역 정보
): KakaoKeywordResponse

data class KKPlaceInfo(
    val id: String, // 장소 ID
    val place_name: String, // 장소, 업체명
    val category_name: String, // 카테고리 이름
    val category_group_code: String, // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    val category_group_name: String, // 중요 카테고리만 그룹핑한 카테고리 그룹명
    val phone: String, // 전화번호
    val address_name: String, // 전체 지번 주소
    val road_address_name: String, // 전체 도로명 주소
    val x: String, // longitude
    val y: String, // latitude
    val distance: String // 중심좌표까지 거리, 단위 meter
): KakaoKeywordResponse