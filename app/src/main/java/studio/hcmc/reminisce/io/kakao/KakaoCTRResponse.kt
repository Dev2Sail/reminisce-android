package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.Serializable

sealed interface KakaoCTRResponse

@Serializable
data class KCTRResponse(
    val meta: KCTRMeta,
    val documents: List<KCTRDocument>
): KakaoCTRResponse

@Serializable
data class KCTRMeta(
    val total_count: Int
): KakaoCTRResponse

@Serializable
data class KCTRDocument(
    val region_type: String, // H(행정동) or B(법정동)
    val addressName: String, // 전체 지역 명칭
    val region_1depth_name: String, // 지역 1Depth, 시도 단위 (바다 x)
    val region_2depth_name: String, // 지역 2Depth, 구 단위 (바다 x)
    val region_3depth_name: String, // 지역 3Depth, 동 단위 (바다 x)
    val code: String, // Region Code
    val x: Double, // longitude
    val y: Double // latitude
): KakaoCTRResponse