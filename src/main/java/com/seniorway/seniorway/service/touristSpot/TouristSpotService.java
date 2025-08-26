package com.seniorway.seniorway.service.touristSpot;

import com.seniorway.seniorway.dto.touristSpot.TouristSpotSaveRequestDto;
import com.seniorway.seniorway.entity.touristSpot.TouristSpotEntity;
import com.seniorway.seniorway.repository.touristSpot.TouristSpotRepository;
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
    private final Logger logger = LoggerFactory.getLogger(TouristSpotService.class);

    @Value("${KTO.KTO_TOUR_INFO_API_KEY}")
    private String apiKey;

    public void fetchAndSaveTouristSpots() {
        int pageNo = 1;
        int numOfRows = 100;
        int totalCount = 0;
        boolean hasMore = true;

        while (hasMore) {
            try {
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
                try {
                    URL url = new URL(urlStr);
                    br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    logger.info("[TouristSpotService] API 응답 수신 성공 (pageNo={})", pageNo);
                } catch (Exception e) {
                    logger.error("[TouristSpotService] API 호출 실패: url={}, error={}", urlStr, e.getMessage(), e);
                    throw e;
                } finally {
                    if (br != null) try { br.close(); } catch (Exception ignore) {}
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
}
