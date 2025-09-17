package com.seniorway.seniorway.service.touristSpot;

import com.seniorway.seniorway.dto.touristSpot.TouristSpotSaveRequestDto;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.FoodDetailEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.LeisureSportsDetailEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.PerformanceExhibitionDetailEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.ShoppingDetailEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.TouristAttractionDetailEntity;
import com.seniorway.seniorway.repository.touristSpot.TouristSpotRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.FoodDetailRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.LeisureSportsDetailRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.PerformanceExhibitionDetailRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.ShoppingDetailRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.TouristAttractionDetailRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.WheelchairAccessRepository;
import com.seniorway.seniorway.repository.touristSpotDetail.PetFriendlyInfoRepository;
import com.seniorway.seniorway.entity.touristSpotDetail.WheelchairAccessEntity;
import com.seniorway.seniorway.entity.touristSpotDetail.PetFriendlyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class TouristSpotService {

    private final TouristSpotRepository touristSpotRepository;
    private final FoodDetailRepository foodDetailRepository;
    private final LeisureSportsDetailRepository leisureSportsDetailRepository;
    private final PerformanceExhibitionDetailRepository performanceExhibitionDetailRepository;
    private final ShoppingDetailRepository shoppingDetailRepository;
    private final TouristAttractionDetailRepository touristAttractionDetailRepository;
    private final WheelchairAccessRepository wheelchairAccessRepository;
    private final PetFriendlyInfoRepository petFriendlyInfoRepository;
    private final Logger logger = LoggerFactory.getLogger(TouristSpotService.class);

    @Value("${KTO.KTO_TOUR_INFO_API_KEY}")
    private String apiKey;

    @Value("${KTO.KTO_PET_TOUR_INFO_API_KEY}")
    private String petApiKey;

    public void fetchAndSaveTouristSpots() {
        int pageNo = 1;
        int numOfRows = 100;
        int totalCount = 0;
        boolean hasMore = true;

        while (hasMore) {
            try {
                //TODO: 나중에 urlBuilder로 변경 고려
                String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
                String urlStr = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                        + "?serviceKey=" + encodedApiKey
                        + "&numOfRows=" + numOfRows
                        + "&pageNo=" + pageNo
                        + "&MobileOS=WEB"
                        + "&MobileApp=SeniorWay"
                        + "&areaCode=6"
                        + "&_type=json";
                // 필요시 추가 파라미터 주석 참고

                logger.info("[TouristSpotService] API 호출 URL: {}", urlStr);

                BufferedReader br = null;
                StringBuilder result = new StringBuilder();
                java.net.HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        logger.info("[TouristSpotService] API 응답 수신 성공 (pageNo={})", pageNo);
                    } else {
                        logger.error("[TouristSpotService] API 호출 실패: HTTP {}", responseCode);
                        throw new RuntimeException("API 호출 실패: HTTP " + responseCode);
                    }
                } catch (Exception e) {
                    logger.error("[TouristSpotService] API 호출 실패: url={}, error={}", urlStr, e.getMessage(), e);
                    throw e;
                } finally {
                    if (br != null) try { br.close(); } catch (Exception ignore) {}
                    if (conn != null) conn.disconnect();
                }

                String response = result.toString();
                if (response.isEmpty()) {
                    logger.warn("[TouristSpotService] API 응답이 비어있음. pageNo={}", pageNo);
                    break;
                }
                logger.debug("[TouristSpotService] API 원본 응답: {}", response);

                JSONObject json;
                try {
                    json = new JSONObject(response);
                } catch (Exception e) {
                    logger.error("[TouristSpotService] 응답 JSON 파싱 실패: error={}, response={}", e.getMessage(), response, e);
                    throw e;
                }

                JSONObject body;
                try {
                    body = json.getJSONObject("response").getJSONObject("body");
                } catch (Exception e) {
                    logger.error("[TouristSpotService] body 파싱 실패: error={}, json={}", e.getMessage(), json, e);
                    throw e;
                }

                if (pageNo == 1) {
                    try {
                        totalCount = body.getInt("totalCount");
                        logger.info("[TouristSpotService] 전체 데이터 개수: {}", totalCount);
                    } catch (Exception e) {
                        logger.warn("[TouristSpotService] totalCount 파싱 실패: error={}", e.getMessage());
                    }
                }

                JSONArray items = null;
                try {
                    items = body.getJSONObject("items").optJSONArray("item");
                    if (items == null) {
                        JSONObject singleItem = body.getJSONObject("items").optJSONObject("item");
                        items = new JSONArray();
                        if (singleItem != null) items.put(singleItem);
                    }
                } catch (Exception e) {
                    logger.error("[TouristSpotService] items 파싱 실패: error={}, body={}", e.getMessage(), body, e);
                    throw e;
                }

                logger.info("[TouristSpotService] 수집된 item 개수: {}", items.length());

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    TouristSpotSaveRequestDto dto = TouristSpotSaveRequestDto.builder()
                            .contentId(item.optString("contentid"))
                            .contentTypeId(item.optString("contenttypeid"))
                            .title(item.optString("title"))
                            .tel(item.optString("tel"))
                            .zipcode(item.optString("zipcode"))
                            .addr1(item.optString("addr1"))
                            .addr2(item.optString("addr2"))
                            .areacode(item.optString("areacode"))
                            .sigungucode(item.optString("sigungucode"))
                            .mapx(item.optString("mapx"))
                            .mapy(item.optString("mapy"))
                            .mlevel(item.optString("mlevel"))
                            .firstimage(item.optString("firstimage"))
                            .firstimage2(item.optString("firstimage2"))
                            .cat1(item.optString("cat1"))
                            .cat2(item.optString("cat2"))
                            .cat3(item.optString("cat3"))
                            .lclsSystm1(item.optString("lclsSystm1"))
                            .lclsSystm2(item.optString("lclsSystm2"))
                            .lclsSystm3(item.optString("lclsSystm3"))
                            .lDongRegnCd(item.optString("lDongRegnCd"))
                            .lDongSignguCd(item.optString("lDongSignguCd"))
                            .cpyrhtDivCd(item.optString("cpyrhtDivCd"))
                            .build();

                    TouristSpotEntity spot = dto.toEntity();
                    try {
                        touristSpotRepository.save(spot);
                        logger.debug("[TouristSpotService] 관광지 저장 성공: contentId={}, title={}", dto.getContentId(), dto.getTitle());
                    } catch (Exception e) {
                        logger.error("[TouristSpotService] DB 저장 오류: contentId={}, title={}, error={}", dto.getContentId(), dto.getTitle(), e.getMessage(), e);
                    }
                }

                int currentCount = pageNo * numOfRows;
                logger.info("[TouristSpotService] 현재까지 처리된 데이터: {}/{}", currentCount, totalCount);

                if (currentCount >= totalCount || items.length() == 0) {
                    logger.info("[TouristSpotService] 데이터 수집 완료 (pageNo={})", pageNo);
                    hasMore = false;
                } else {
                    pageNo++;
                }
            } catch (Exception e) {
                logger.error("[TouristSpotService] API 데이터 수집 오류: pageNo={}, error={}", pageNo, e.getMessage(), e);
                break;
            }
        }
    }

    public void fetchAndSaveTouristSpotDetails() {
        var spots = touristSpotRepository.findAll();
        logger.info("[TouristSpotService] 관광지 상세정보 저장 시작. 대상 관광지 수: {}", spots.size());
        int maxApiCalls = 1000;
        int apiCallCount = 0;
        for (TouristSpotEntity spot : spots) {
            if (apiCallCount >= maxApiCalls) break;
            String contentId = spot.getContentId();
            String contentTypeId = spot.getContentTypeId();

            // 이미 상세정보가 저장되어 있으면 건너뜀
            boolean alreadyExists = false;
            switch (contentTypeId) {
                case "12":
                    alreadyExists = touristAttractionDetailRepository.existsByContentId(contentId);
                    break;
                case "14":
                    alreadyExists = performanceExhibitionDetailRepository.existsByContentId(contentId);
                    break;
                case "28":
                    alreadyExists = leisureSportsDetailRepository.existsByContentId(contentId);
                    break;
                case "38":
                    alreadyExists = shoppingDetailRepository.existsByContentId(contentId);
                    break;
                case "39":
                    alreadyExists = foodDetailRepository.existsByContentId(contentId);
                    break;
                default:
                    alreadyExists = true; // 상세 저장 대상이 아니면 건너뜀
            }
            if (alreadyExists) {
                logger.info("[TouristSpotService] 이미 상세정보가 저장된 contentId={}, contentTypeId={}", contentId, contentTypeId);
                continue;
            }

            logger.debug("[TouristSpotService] 상세정보 저장 시도: contentId={}, contentTypeId={}", contentId, contentTypeId);
            try {
                String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
                String urlStr = "https://apis.data.go.kr/B551011/KorService2/detailIntro2"
                        + "?serviceKey=" + encodedApiKey
                        + "&MobileOS=WEB"
                        + "&MobileApp=SeniorWay"
                        + "&contentId=" + contentId
                        + "&contentTypeId=" + contentTypeId
                        + "&_type=json";
                logger.info("[TouristSpotService] 상세 API 호출: {}", urlStr);

                BufferedReader br = null;
                StringBuilder result = new StringBuilder();
                java.net.HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        logger.debug("[TouristSpotService] 상세 API 응답 수신 성공: contentId={}, contentTypeId={}", contentId, contentTypeId);
                    } else {
                        logger.error("[TouristSpotService] 상세 API 호출 실패: HTTP {}", responseCode);
                        throw new RuntimeException("상세 API 호출 실패: HTTP " + responseCode);
                    }
                } finally {
                    if (br != null) try { br.close(); } catch (Exception ignore) {}
                    if (conn != null) conn.disconnect();
                }

                apiCallCount++; // 실제 API 호출이 성공적으로 끝난 경우에만 증가

                String response = result.toString();
                JSONObject json = new JSONObject(response);

                // body가 없을 경우 예외 처리
                if (!json.has("body") && json.has("response")) {
                    JSONObject responseObj = json.getJSONObject("response");
                    if (!responseObj.has("body")) {
                        logger.warn("[TouristSpotService] 상세정보 응답에 body 없음: contentId={}, contentTypeId={}", contentId, contentTypeId);
                        continue;
                    }
                    json = responseObj;
                }
                if (!json.has("body")) {
                    logger.warn("[TouristSpotService] 상세정보 응답에 body 없음(최종): contentId={}, contentTypeId={}", contentId, contentTypeId);
                    continue;
                }

                JSONObject body = json.getJSONObject("body");
                if (!body.has("items")) {
                    logger.warn("[TouristSpotService] 상세정보 응답에 items 없음: contentId={}, contentTypeId={}", contentId, contentTypeId);
                    continue;
                }
                JSONObject items = body.getJSONObject("items");
                if (!items.has("item")) {
                    logger.warn("[TouristSpotService] 상세정보 응답에 item 없음: contentId={}, contentTypeId={}", contentId, contentTypeId);
                    continue;
                }
                JSONObject detailItem;
                if (items.optJSONArray("item") != null) {
                    detailItem = items.getJSONArray("item").getJSONObject(0);
                } else {
                    detailItem = items.getJSONObject("item");
                }
                if (detailItem == null) {
                    logger.warn("[TouristSpotService] 상세정보 없음: contentId={}, contentTypeId={}", contentId, contentTypeId);
                    continue;
                }

                switch (contentTypeId) {
                    case "12": // TouristAttraction
                        logger.debug("[TouristSpotService] TouristAttractionDetailEntity 저장 시작: contentId={}", contentId);
                        TouristAttractionDetailEntity ta = TouristAttractionDetailEntity.builder()
                                .contentId(contentId)
                                .contentTypeId(contentTypeId)
                                .touristSpot(spot)
                                .heritage1(detailItem.optString("heritage1", null))
                                .heritage2(detailItem.optString("heritage2", null))
                                .heritage3(detailItem.optString("heritage3", null))
                                .infoCenter(detailItem.optString("infocenter", null))
                                .openDate(detailItem.optString("opendate", null))
                                .restDate(detailItem.optString("restdate", null))
                                .expGuide(detailItem.optString("expguide", null))
                                .expAgeRange(detailItem.optString("expagerange", null))
                                .accomCount(detailItem.optString("accomcount", null))
                                .useSeason(detailItem.optString("useseason", null))
                                .useTime(detailItem.optString("usetime", null))
                                .parkingAvailable(detailItem.optString("parking", null))
                                .chkBabyCarriage(detailItem.optString("chkbabycarriage", null))
                                .chkPet(detailItem.optString("chkpet", null))
                                .chkCreditCard(detailItem.optString("chkcreditcard", null))
                                .build();
                        touristAttractionDetailRepository.save(ta);
                        logger.info("[TouristSpotService] TouristAttractionDetailEntity 저장 완료: contentId={}", contentId);
                        break;
                    case "14": // PerformanceExhibition
                        logger.debug("[TouristSpotService] PerformanceExhibitionDetailEntity 저장 시작: contentId={}", contentId);
                        PerformanceExhibitionDetailEntity pe = PerformanceExhibitionDetailEntity.builder()
                                .contentId(contentId)
                                .contentTypeId(contentTypeId)
                                .touristSpot(spot)
                                .scale(detailItem.optString("scale", null))
                                .useFee(detailItem.optString("usefee", null))
                                .discountInfo(detailItem.optString("discountinfo", null))
                                .spendTime(detailItem.optString("spendtime", null))
                                .parkingFee(detailItem.optString("parkingfee", null))
                                .infoCenter(detailItem.optString("infocenterculture", null))
                                .accomCount(detailItem.optString("accomcountculture", null))
                                .useTime(detailItem.optString("usetimeculture", null))
                                .restDate(detailItem.optString("restdateculture", null))
                                .parkingAvailable(detailItem.optString("parkingculture", null))
                                .chkBabyCarriage(detailItem.optString("chkbabycarriageculture", null))
                                .chkPet(detailItem.optString("chkpetculture", null))
                                .chkCreditCard(detailItem.optString("chkcreditcardculture", null))
                                .build();
                        performanceExhibitionDetailRepository.save(pe);
                        logger.info("[TouristSpotService] PerformanceExhibitionDetailEntity 저장 완료: contentId={}", contentId);
                        break;
                    case "28": // LeisureSports
                        logger.debug("[TouristSpotService] LeisureSportsDetailEntity 저장 시작: contentId={}", contentId);
                        LeisureSportsDetailEntity ls = LeisureSportsDetailEntity.builder()
                                .contentId(contentId)
                                .contentTypeId(contentTypeId)
                                .touristSpot(spot)
                                .openPeriod(detailItem.optString("openperiod", null))
                                .reservationInfo(detailItem.optString("reservation", null))
                                .infoCenter(detailItem.optString("infocenterleports", null))
                                .scale(detailItem.optString("scaleleports", null))
                                .accomCount(detailItem.optString("accomcountleports", null))
                                .restDate(detailItem.optString("restdateleports", null))
                                .useTime(detailItem.optString("usetimeleports", null))
                                .useFee(detailItem.optString("usefeeleports", null))
                                .expAgeRange(detailItem.optString("expagerangeleports", null))
                                .parkingAvailable(detailItem.optString("parkingleports", null))
                                .parkingFee(detailItem.optString("parkingfeeleports", null))
                                .chkBabyCarriage(detailItem.optString("chkbabycarriageleports", null))
                                .chkPet(detailItem.optString("chkpetleports", null))
                                .chkCreditCard(detailItem.optString("chkcreditcardleports", null))
                                .build();
                        leisureSportsDetailRepository.save(ls);
                        logger.info("[TouristSpotService] LeisureSportsDetailEntity 저장 완료: contentId={}", contentId);
                        break;
                    case "38": // Shopping
                        logger.debug("[TouristSpotService] ShoppingDetailEntity 저장 시작: contentId={}", contentId);
                        ShoppingDetailEntity sh = ShoppingDetailEntity.builder()
                                .contentId(contentId)
                                .contentTypeId(contentTypeId)
                                .touristSpot(spot)
                                .saleItem(detailItem.optString("saleitem", null))
                                .saleItemCost(detailItem.optString("saleitemcost", null))
                                .fairDay(detailItem.optString("fairday", null))
                                .openDate(detailItem.optString("opendateshopping", null))
                                .shopGuide(detailItem.optString("shopguide", null))
                                .cultureCenter(detailItem.optString("culturecenter", null))
                                .restroomAvailable(detailItem.optString("restroom", null))
                                .infoCenter(detailItem.optString("infocentershopping", null))
                                .scale(detailItem.optString("scaleshopping", null))
                                .restDate(detailItem.optString("restdateshopping", null))
                                .parkingAvailable(detailItem.optString("parkingshopping", null))
                                .chkBabyCarriage(detailItem.optString("chkbabycarriageshopping", null))
                                .chkPet(detailItem.optString("chkpetshopping", null))
                                .chkCreditCard(detailItem.optString("chkcreditcardshopping", null))
                                .openTime(detailItem.optString("opentime", null))
                                .build();
                        shoppingDetailRepository.save(sh);
                        logger.info("[TouristSpotService] ShoppingDetailEntity 저장 완료: contentId={}", contentId);
                        break;
                    case "39": // Food
                        logger.debug("[TouristSpotService] FoodDetailEntity 저장 시작: contentId={}", contentId);
                        FoodDetailEntity fd = FoodDetailEntity.builder()
                                .contentId(contentId)
                                .contentTypeId(contentTypeId)
                                .touristSpot(spot)
                                .seatInfo(detailItem.optString("seat", null))
                                .kidsFacility(detailItem.optString("kidsfacility", null))
                                .firstMenu(detailItem.optString("firstmenu", null))
                                .treatMenu(detailItem.optString("treatmenu", null))
                                .smokingAllowed(detailItem.optString("smoking", null))
                                .packingAvailable(detailItem.optString("packing", null))
                                .infoCenter(detailItem.optString("infocenterfood", null))
                                .scale(detailItem.optString("scalefood", null))
                                .parkingAvailable(detailItem.optString("parkingfood", null))
                                .build();
                        foodDetailRepository.save(fd);
                        logger.info("[TouristSpotService] FoodDetailEntity 저장 완료: contentId={}", contentId);
                        break;
                    default:
                        logger.info("[TouristSpotService] 상세정보 저장 대상 아님: contentTypeId={}", contentTypeId);
                }
            } catch (Exception e) {
                logger.error("[TouristSpotService] 상세정보 저장 실패: contentId={}, contentTypeId={}, error={}", contentId, contentTypeId, e.getMessage(), e);
            }
        }
        logger.info("[TouristSpotService] 관광지 상세정보 저장 작업 완료");
    }

    /**
     * 무장애 여행정보 저장 (contentTypeId=12,28,38,39)
     */
    public void fetchAndSaveWheelchairAccessInfo() {
        var spots = touristSpotRepository.findAll();
        logger.info("[TouristSpotService] 무장애 여행정보 저장 시작. 대상 관광지 수: {}", spots.size());
        int savedCount = 0;
        for (TouristSpotEntity spot : spots) {
            String contentTypeId = spot.getContentTypeId();
            if (!("12".equals(contentTypeId) || "28".equals(contentTypeId) || "38".equals(contentTypeId) || "39".equals(contentTypeId))) continue;
            String contentId = spot.getContentId();
            if (wheelchairAccessRepository.existsByContentId(contentId)) {
                WheelchairAccessEntity entity = wheelchairAccessRepository.findByContentId(contentId);
                int isBarrierFree = (entity.getExitInfo() != null && !entity.getExitInfo().isBlank()) ? 1 : 0;
                if ((entity.getBarrierFree() == null ? 0 : (entity.getBarrierFree() ? 1 : 0)) != isBarrierFree) {
                    entity.setBarrierFree(isBarrierFree == 1);
                    wheelchairAccessRepository.save(entity);
                    logger.info("[TouristSpotService] 무장애 정보 barrierFree 컬럼만 수정: contentId={}, barrierFree={}", contentId, isBarrierFree);
                } else {
                    logger.info("[TouristSpotService] 이미 무장애 정보가 저장되어 있고 barrierFree도 일치: contentId={}", contentId);
                }
                continue;
            }
            try {
                String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
                String urlStr = "https://apis.data.go.kr/B551011/KorWithService2/detailWithTour2"
                        + "?serviceKey=" + encodedApiKey
                        + "&MobileOS=WEB"
                        + "&MobileApp=SeniorWay"
                        + "&contentId=" + contentId
                        + "&_type=json";
                logger.info("[TouristSpotService] 무장애 API 호출: {}", urlStr);

                BufferedReader br = null;
                StringBuilder result = new StringBuilder();
                java.net.HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        logger.debug("[TouristSpotService] 무장애 API 응답 수신 성공: contentId={}", contentId);
                    } else {
                        logger.error("[TouristSpotService] 무장애 API 호출 실패: HTTP {}", responseCode);
                        continue;
                    }
                } finally {
                    if (br != null) try { br.close(); } catch (Exception ignore) {}
                    if (conn != null) conn.disconnect();
                }

                String response = result.toString();
                JSONObject json = new JSONObject(response);
                JSONObject body = json.optJSONObject("response") != null ? json.getJSONObject("response").optJSONObject("body") : null;
                if (body == null || !body.has("items")) {
                    logger.warn("[TouristSpotService] 무장애 응답에 body/items 없음: contentId={}", contentId);
                    continue;
                }
                JSONObject items = body.getJSONObject("items");
                if (!items.has("item")) {
                    logger.warn("[TouristSpotService] 무장애 응답에 item 없음: contentId={}", contentId);
                    continue;
                }
                JSONObject item;
                if (items.optJSONArray("item") != null) {
                    item = items.getJSONArray("item").getJSONObject(0);
                } else {
                    item = items.getJSONObject("item");
                }
                if (item == null) continue;

                WheelchairAccessEntity entity = new WheelchairAccessEntity();
                entity.setContentId(contentId);
                entity.setParking(item.optString("parking", null));
                entity.setRoute(item.optString("route", null));
                String exitInfo = item.optString("exit", null);
                entity.setExitInfo(exitInfo);
                entity.setElevator(item.optString("elevator", null));
                entity.setRestroom(item.optString("restroom", null));
                entity.setBarrierFree(exitInfo != null && !exitInfo.isBlank() ? true : false); // 1(true) or 0(false)
                wheelchairAccessRepository.save(entity);
                savedCount++;
                logger.info("[TouristSpotService] 무장애 정보 저장 완료: contentId={}, barrierFree={}", contentId, entity.getBarrierFree() ? 1 : 0);
            } catch (Exception e) {
                logger.error("[TouristSpotService] 무장애 정보 저장 실패: contentId={}, error={}", spot.getContentId(), e.getMessage(), e);
            }
        }
        logger.info("[TouristSpotService] 무장애 여행정보 저장 완료. 저장 건수: {}", savedCount);
    }

    /**
     * 반려동물 여행정보 저장 (contentTypeId=12,28,38,39)
     */
    public void fetchAndSavePetFriendlyInfo() {
        var spots = touristSpotRepository.findAll();
        logger.info("[TouristSpotService] 반려동물 여행정보 저장 시작. 대상 관광지 수: {}", spots.size());
        int savedCount = 0;
        for (TouristSpotEntity spot : spots) {
            String contentTypeId = spot.getContentTypeId();
            if (!("12".equals(contentTypeId) || "28".equals(contentTypeId) || "38".equals(contentTypeId) || "39".equals(contentTypeId))) continue;
            String contentId = spot.getContentId();
            if (petFriendlyInfoRepository.existsByContentId(contentId)) {
                logger.info("[TouristSpotService] 이미 반려동물 정보가 저장된 contentId={}", contentId);
                continue;
            }
            try {
                String encodedApiKey = URLEncoder.encode(petApiKey, StandardCharsets.UTF_8);
                String urlStr = "https://apis.data.go.kr/B551011/KorPetTourService/detailPetTour"
                        + "?serviceKey=" + encodedApiKey
                        + "&MobileOS=WEB"
                        + "&MobileApp=SeniorWay"
                        + "&contentId=" + contentId
                        + "&_type=json";
                logger.info("[TouristSpotService] 반려동물 API 호출: {}", urlStr);

                BufferedReader br = null;
                StringBuilder result = new StringBuilder();
                java.net.HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        logger.debug("[TouristSpotService] 반려동물 API 응답 수신 성공: contentId={}", contentId);
                    } else {
                        logger.error("[TouristSpotService] 반려동물 API 호출 실패: HTTP {}", responseCode);
                        continue;
                    }
                } finally {
                    if (br != null) try { br.close(); } catch (Exception ignore) {}
                    if (conn != null) conn.disconnect();
                }

                String response = result.toString();
                JSONObject json = new JSONObject(response);
                JSONObject body = json.optJSONObject("response") != null ? json.getJSONObject("response").optJSONObject("body") : null;
                if (body == null || !body.has("items")) {
                    logger.warn("[TouristSpotService] 반려동물 응답에 body/items 없음: contentId={}", contentId);
                    continue;
                }
                JSONObject items = body.getJSONObject("items");
                if (!items.has("item")) {
                    logger.warn("[TouristSpotService] 반려동물 응답에 item 없음: contentId={}", contentId);
                    continue;
                }
                JSONObject item;
                if (items.optJSONArray("item") != null) {
                    item = items.getJSONArray("item").getJSONObject(0);
                } else {
                    item = items.getJSONObject("item");
                }
                if (item == null) continue;

                PetFriendlyEntity entity = new PetFriendlyEntity();
                entity.setContentId(contentId);
                entity.setAcmpyNeedMtr(item.optString("acmpyNeedMtr", null));
                entity.setRelaAcdntRiskMtr(item.optString("relaAcdntRiskMtr", null));
                entity.setAcmpyTypeCd(item.optString("acmpyTypeCd", null));
                entity.setRelaPosesFclty(item.optString("relaPosesFclty", null));
                entity.setRelaFrnshPrdlst(item.optString("relaFrnshPrdlst", null));
                entity.setEtcAcmpyInfo(item.optString("etcAcmpyInfo", null));
                entity.setRelaPurcPrdlst(item.optString("relaPurcPrdlst", null));
                entity.setAcmpyPsblCpam(item.optString("acmpyPsblCpam", null));
                entity.setRelaRntlPrdlst(item.optString("relaRntlPrdlst", null));
                petFriendlyInfoRepository.save(entity);
                savedCount++;
                logger.info("[TouristSpotService] 반려동물 정보 저장 완료: contentId={}", contentId);
            } catch (Exception e) {
                logger.error("[TouristSpotService] 반려동물 정보 저장 실패: contentId={}, error={}", spot.getContentId(), e.getMessage(), e);
            }
        }
        logger.info("[TouristSpotService] 반려동물 여행정보 저장 완료. 저장 건수: {}", savedCount);
    }

    public TouristSpotEntity findTouristSpotByContentId(String contentId) {
        return touristSpotRepository.findByContentId(contentId);
    }

    public Object getTouristSpotDetailDto(String contentId, String contentTypeId) {
        TouristSpotEntity spot = touristSpotRepository.findByContentId(contentId);
        if (spot == null) return null;

        JSONObject result = new JSONObject();
        result.put("spot", spot);

        Object detail = null;
        switch (contentTypeId) {
            case "12":
                var ta = touristAttractionDetailRepository.findByContentId(contentId);
                if (ta != null) {
                    JSONObject detailJson = new JSONObject(ta);
                    detailJson.remove("touristSpot");
                    detail = detailJson.toMap();
                }
                break;
            case "14":
                var pe = performanceExhibitionDetailRepository.findByContentId(contentId);
                if (pe != null) {
                    JSONObject detailJson = new JSONObject(pe);
                    detailJson.remove("touristSpot");
                    detail = detailJson.toMap();
                }
                break;
            case "28":
                var ls = leisureSportsDetailRepository.findByContentId(contentId);
                if (ls != null) {
                    JSONObject detailJson = new JSONObject(ls);
                    detailJson.remove("touristSpot");
                    detail = detailJson.toMap();
                }
                break;
            case "38":
                var sh = shoppingDetailRepository.findByContentId(contentId);
                if (sh != null) {
                    JSONObject detailJson = new JSONObject(sh);
                    detailJson.remove("touristSpot");
                    detail = detailJson.toMap();
                }
                break;
            case "39":
                var fd = foodDetailRepository.findByContentId(contentId);
                if (fd != null) {
                    JSONObject detailJson = new JSONObject(fd);
                    detailJson.remove("touristSpot");
                    detail = detailJson.toMap();
                }
                break;
            default:
                break;
        }
        if (detail != null) result.put("detail", detail);

        WheelchairAccessEntity wheelchair = wheelchairAccessRepository.findByContentId(contentId);
        if (wheelchair != null && Boolean.TRUE.equals(wheelchair.getBarrierFree())) {
            result.put("wheelchairAccess", wheelchair);
        }

        // 반려동물 정보
        PetFriendlyEntity pet = petFriendlyInfoRepository.existsByContentId(contentId)
                ? petFriendlyInfoRepository.findByContentId(contentId)
                : null;
        if (pet != null) {
            result.put("petFriendly", pet);
        }

        return result.toMap();
    }
}
