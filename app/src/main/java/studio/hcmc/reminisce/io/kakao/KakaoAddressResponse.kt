package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface KakaoAddressResponse

@Serializable
data class KAResponse(
    val meta: KAMeta,
    val documents: List<KADocument>
): KakaoAddressResponse

@Serializable
data class KAMeta(
    @SerialName("total_count")
    val totalCount: Int, // 검색어에 검색된 문서 수
    @SerialName("pageable_count")
    val pageableCount: Int, // total_count 중 노출 가능 문서 수
    @SerialName("is_end")
    val isEnd: Boolean // 현재 페이지가 마지막 페이지인지 여부
): KakaoAddressResponse

@Serializable
data class KADocument(
    @SerialName("address_name")
    val addressName: String, // 전체 지번 주소 or 전체 도로명 주소 -> 입력따라 결정
    @SerialName("address_type")
    val addressType: AddressType,
    @SerialName("x")
    val longitude: String,
    @SerialName("y")
    val latitude: String,
    val address: KAAddress, // 지번 주소 상세 정보
    @SerialName("road_address")
    val roadAddress: KARoadAddress // 도로명 주소 상세 정보
): KakaoAddressResponse

@Serializable
enum class AddressType { REGION, ROAD, REGION_ADDR, ROAD_ADDR }

@Serializable
data class KAAddress(
    @SerialName("address_name")
    val addressName: String, // 전체 지번 주소
    @SerialName("region_1depth_name")
    val region1depthName: String, // 지역 1Depth, 시도 단위
    @SerialName("region_2depth_name")
    val region2depthName: String, // 지역 2Depth, 구 단위
    @SerialName("region_3depth_name")
    val region3depthName: String, // 지역 3Depth, 동 단위
    @SerialName("region_3depth_h_name")
    val region3depthHName: String, // 지역 3Depth, 행정동 명칭
    @SerialName("h_code")
    val hCode: String, // 행정 코드
    @SerialName("b_code")
    val bCode: String, // 법정 코드
    @SerialName("mountain_yn")
    val mountainYn: String, // 산 여부, Y or N
    @SerialName("main_address_no")
    val mainAddressNo: String, // 지번 주번지
    @SerialName("sub_address_no")
    val subAddressNo: String, // 지번 부번지, 없을 경우 ("")
    @SerialName("x")
    val longitude: String,
    @SerialName("y")
    val latitude: String
): KakaoAddressResponse

@Serializable
data class KARoadAddress(
    @SerialName("address_name")
    val addressName: String, // 전체 도로명 주소
    @SerialName("region_1depth_name")
    val region1depthName: String, // 지역명 1
    @SerialName("region_2depth_name")
    val region2depthName: String, // 지역명 2
    @SerialName("region_3depth_name")
    val region3depthName: String, // 지역명 3
    @SerialName("road_name")
    val roadName: String, // 도로명
    @SerialName("underground_yn")
    val undergroundYn: String, // 지하 여부 Y or N
    @SerialName("main_building_no")
    val mainBuildingNo: String, // 건물 본번
    @SerialName("building_name")
    val buildingName: String, // 건물명
    @SerialName("x")
    val longitude: String,
    @SerialName("y")
    val latitude: String
): KakaoAddressResponse