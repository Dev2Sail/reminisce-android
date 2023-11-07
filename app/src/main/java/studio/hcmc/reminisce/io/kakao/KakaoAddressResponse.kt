package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.Serializable

sealed interface KakaoAddressResponse

@Serializable
data class KAResponse(
    val meta: KAMeta,
    val documents: List<KADocument>
): KakaoAddressResponse

@Serializable
data class KAMeta(
    val total_count: Int, // 검색어에 검색된 문서 수
    val pageable_count: Int, // total_count 중 노출 가능 문서 수
    val is_end: Boolean // 현재 페이지가 마지막 페이지인지 여부
): KakaoAddressResponse

@Serializable
data class KADocument(
    val address_name: String, // 전체 지번 주소 or 전체 도로명 주소 -> 입력따라 결정
    val address_type: AddressType,
    val x: String, // longitude
    val y: String, // latitude
    val address: KAAddress, // 지번 주소 상세 정보
    val road_address: KARoadAddress // 도로명 주소 상세 정보
): KakaoAddressResponse


enum class AddressType { REGION, ROAD, REGION_ADDR, ROAD_ADDR }

@Serializable
data class KAAddress(
    val address_name: String, // 전체 지번 주소
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val region_3depth_h_name: String, // 지역 3Depth, 행정동 명칭
    val h_code: String, // 행정 코드
    val b_code: String, // 법정 코드
    val mountain_yn: String, // 산 여부, Y or N
    val main_address_no: String, // 지번 주번지
    val sub_address_no: String, // 지번 부번지, 없을 경우 ("")
    val x: String, // longitude
    val y: String // latitude
): KakaoAddressResponse

@Serializable
data class KARoadAddress(
    val address_name: String, // 전체 도로명 주소
    val region_1depth_name: String, // 지역명 1
    val region_2depth_name: String, // 지역명 2
    val region_3depth_name: String, // 지역명 3
    val road_name: String, // 도로명
    val underground_yn: String, // 지하 여부 Y or N
    val main_building_no: String, // 건물 본번
    val building_name: String, // 건물명
    val x: String,
    val y: String
): KakaoAddressResponse