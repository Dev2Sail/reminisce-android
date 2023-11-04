package studio.hcmc.reminisce.io.kakao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface KakaoCTRResponse

@Serializable
data class KCTRResponse(
    val meta: KCTRMeta,
    val documents: List<KCTRDocument>
): KakaoCTRResponse

@Serializable
data class KCTRMeta(
    @SerialName("total_count")
    val totalCount: Int
): KakaoCTRResponse

@Serializable
data class KCTRDocument(
    @SerialName("region_type")
    val regionType: String, // H(행정동) or B(법정동)
    @SerialName("address_name")
    val addressName: String, // 전체 지역 명칭
    @SerialName("region_1depth_name")
    val region1depthName: String?, // 지역 1Depth, 시도 단위 (바다 x)
    @SerialName("region_2depth_name")
    val region2depthName: String?, // 지역 2Depth, 구 단위 (바다 x)
    @SerialName("region_3depth_name")
    val region3depthName: String?, // 지역 3Depth, 동 단위 (바다 x)
    val code: String, // Region Code
    @SerialName("x")
    val longitude: Double,
    @SerialName("y")
    val latitude: Double
): KakaoCTRResponse