package studio.hcmc.reminisce.io.kakao

sealed interface KakaoCTAResponse

data class KCTAResponse(
    val meta: KCTAMeta,
    val documents: List<KCTADocument>
): KakaoCTAResponse
data class KCTAMeta(val total_count: Int): KakaoCTAResponse

data class KCTADocument(
    val address: KCTAAddress,
    val road_address: KCTARoadAddress
): KakaoCTAResponse

data class KCTAAddress(
    val address_name: String, // 전체 지번 주소
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val mountain_yn: String, // 산 여부, Y or N
    val main_address_no: String, // 지번 주 번지
    val sub_address_no: String // 지번 부 번지, 없다면 빈 문자열("")
): KakaoCTAResponse

data class KCTARoadAddress(
    val address_name: String, // 전체 도로명 주소
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val road_name: String, // 도로명
    val underground_yn: String, // 지하 여부 Y or N
    val main_building_no: String, // 건물 본번
    val sub_building_no: String, // 건물 부번, 없다면 빈 문자열("")
    val building_name: String, // 건물명
    val zone_no: String // 우편번호(5자리)
): KakaoCTAResponse