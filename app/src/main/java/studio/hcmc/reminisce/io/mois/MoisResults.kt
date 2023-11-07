package studio.hcmc.reminisce.io.mois

import kotlinx.serialization.Serializable
sealed interface MoisJusoResponse


@Serializable
data class MoisResponse(
    val results: MoisResults
): MoisJusoResponse

@Serializable
data class MoisResults(
    val common: MoisCommon,
    val juso: List<MoisJuso>
): MoisJusoResponse

@Serializable
data class MoisCommon(
    val totalCount: String, // 총 검색 데이터 수
    val currentPage: Int, // 페이지 번호
    val countPerPage: Int, // 페이지당 출력할 결과 Row 수
    val errorCode: String,
    val errorMessage: String
): MoisJusoResponse

@Serializable
data class MoisJuso(
    val roadAddr: String, // 전체 도로명 주소
    val roadAddrPart1: String, // 도로명주소(참고항목 제외)
    val roadAddrPart2: String?, // 도로명주소 참고항목 nullable
    val jibunAddr: String, // 지번주소
    val admCd: String, // 행정구역코드
    val rnMgtSn: String, // 도로명코드
    val siNm: String, // 시도명
    val sggNm: String, // 시군구명
    val emdNm: String, // 읍면동명
    val liNm: String, // 법정리명
    val rn: String // 도로명
): MoisJusoResponse
