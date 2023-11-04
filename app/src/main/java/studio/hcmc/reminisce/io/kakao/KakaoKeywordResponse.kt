package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface KakaoKeywordResponse

@Serializable
data class KKResponse(
    val meta: KKMeta,
    val documents: List<KKPlaceInfo>
): KakaoKeywordResponse

@Serializable
data class KKMeta(
    @SerialName("total_count")
    val totalCount: Int, // 검색어에 검색된 문서 수
    @SerialName("pageable_count")
    val pageableCount: Int, // total_count 중 노출 가능 문서 수 (max: 45)
    @SerialName("is_end")
    val isEnd: Boolean, // 현재 페이지가 마지막 페이지인지 여부 값이 false면 다음 요청 시 page 값 증가시켜 다음 페이지 요청 가능
    @SerialName("same_name")
    val sameName: KKSameName // 질의어의 지역 및 키워드 분석 정보
): KakaoKeywordResponse

@Serializable
data class KKSameName(
    val region: List<String>, // 질의어에서 인식된 지역 리스트
    val keyword: String, // 질의어에서 지역 정보 제외한 키워드
    @SerialName("selected_region")
    val selectedRegion: String // 인식된 지역 리스트 중 현재 검색에 사용된 지역 정보
): KakaoKeywordResponse

@Serializable
data class KKPlaceInfo(
    val id: String, // 장소 ID
    @SerialName("place_name")
    val placeName: String, // 장소, 업체명
    @SerialName("category_name")
    val categoryName: String, // 카테고리 이름
    @SerialName("category_group_code")
    val categoryGroupCode: String, // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    @SerialName("category_group_name")
    val categoryGroupName: String, // 중요 카테고리만 그룹핑한 카테고리 그룹명
    val phone: String, // 전화번호
    @SerialName("address_name")
    val addressName: String, // 전체 지번 주소
    @SerialName("road_address_name")
    val roadAddressName: String, // 전체 도로명 주소
    @SerialName("x")
    val longitude: String,
    @SerialName("y")
    val latitude: String,
    val distance: String // 중심좌표까지 거리, 단위 meter
): KakaoKeywordResponse