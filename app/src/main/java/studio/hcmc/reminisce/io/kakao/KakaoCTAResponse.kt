package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.Serializable

sealed interface KakaoCTAResponse

@Serializable
data class KCTAResponse(
    val meta: KCTAMeta,
    val documents: List<KCTADocument>
): KakaoCTAResponse

@Serializable
data class KCTAMeta(
    val total_count: Int
): KakaoCTAResponse

@Serializable
data class KCTADocument(
    val address: KCTAAddress,
    val road_address: KCTARoadAddress
): KakaoCTAResponse

@Serializable
data class KCTAAddress(
    val address_name: String, // 전체 지번 주소
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val main_address_no: String, // 지번 주 번지
): KakaoCTAResponse

@Serializable
data class KCTARoadAddress(
    val address_name: String, // 전체 도로명 주소
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val roadName: String, // 도로명
    val main_building_no: String, // 건물 본번
    val building_name: String, // 건물명
): KakaoCTAResponse