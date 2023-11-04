package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface KakaoCTAResponse

@Serializable
data class KCTAResponse(
    val meta: KCTAMeta,
    val documents: List<KCTADocument>
): KakaoCTAResponse

@Serializable
data class KCTAMeta(
    @SerialName("total_count")
    val totalCount: Int
): KakaoCTAResponse

@Serializable
data class KCTADocument(
    val address: KCTAAddress,
    @SerialName("road_address")
    val roadAddress: KCTARoadAddress
): KakaoCTAResponse

@Serializable
data class KCTAAddress(
    @SerialName("address_name")
    val addressName: String, // 전체 지번 주소
    @SerialName("region_1depth_name")
    val region1depthName: String, // 지역 1Depth, 시도 단위
    @SerialName("region_2depth_name")
    val region2depthName: String, // 지역 2Depth, 구 단위
    @SerialName("region_3depth_name")
    val region3depthName: String, // 지역 3Depth, 동 단위
    @SerialName("mountain_yn")
    val mountainYn: String, // 산 여부, Y or N
    @SerialName("main_address_no")
    val mainAddressNo: String, // 지번 주 번지
    @SerialName("sub_address_no")
    val subAddressNo: String? // 지번 부 번지, 없다면 빈 문자열("")
): KakaoCTAResponse

@Serializable
data class KCTARoadAddress(
    @SerialName("address_name")
    val addressName: String?, // 전체 도로명 주소
    @SerialName("region_1depth_name")
    val region1depthName: String, // 지역 1Depth, 시도 단위
    @SerialName("region_2depth_name")
    val region2depthName: String, // 지역 2Depth, 구 단위
    @SerialName("region_3depth_name")
    val region3depthName: String, // 지역 3Depth, 동 단위
    @SerialName("road_name")
    val roadName: String, // 도로명
    @SerialName("underground_yn")
    val undergroundYn: String, // 지하 여부 Y or N
    @SerialName("main_building_no")
    val mainBuildingNo: String?, // 건물 본번
    @SerialName("sub_building_no")
    val subBuildingNo: String?, // 건물 부번, 없다면 빈 문자열("")
    @SerialName("building_name")
    val buildingName: String, // 건물명
    @SerialName("zone_no")
    val zoneNo: String // 우편번호(5자리)
): KakaoCTAResponse