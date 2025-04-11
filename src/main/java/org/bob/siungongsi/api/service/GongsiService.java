package org.bob.siungongsi.api.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.api.client.clientinterface.KoreanInvestmentClient;
import org.bob.siungongsi.api.controller.dto.CompanyResponse;
import org.bob.siungongsi.api.controller.dto.GongsiResponse;
import org.bob.siungongsi.api.controller.dto.PaginationResponse;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.GongsiEntity;
import org.bob.siungongsi.common.domain.GongsiViewHistoryEntity;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.bob.siungongsi.common.repository.GongsiRepository;
import org.bob.siungongsi.common.repository.GongsiViewHistoryRepository;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GongsiService {

  private static final Logger logger = LoggerFactory.getLogger(GongsiService.class);

  private final GongsiRepository gongsiRepository;
  private final CompanyRepository companyRepository;
  private final GongsiViewHistoryRepository gongsiViewHistoryRepository;
  private final NotificationRepository notificationRepository;
  private final KoreanInvestmentClient koreanInvestmentClient;

  public GongsiService(
      GongsiRepository gongsiRepository,
      CompanyRepository companyRepository,
      GongsiViewHistoryRepository gongsiViewHistoryRepository,
      NotificationRepository notificationRepository,
      KoreanInvestmentClient koreanInvestmentClient) {
    this.gongsiRepository = gongsiRepository;
    this.companyRepository = companyRepository;
    this.gongsiViewHistoryRepository = gongsiViewHistoryRepository;
    this.notificationRepository = notificationRepository;
    this.koreanInvestmentClient = koreanInvestmentClient;
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
      try {
        return Long.valueOf(authentication.getName());
      } catch (Exception e) {
        logger.error("Error extracting user ID: {}", e.getMessage());
      }
    }
    return null; // 익명의 유저면 null 반환
  }

  public Long getCompanyIdByGongsiId(Long gongsiId) {
    GongsiEntity gongsi =
        gongsiRepository
            .findById(gongsiId)
            .orElseThrow(
                () ->
                    new CustomException(
                        ApiResponseCode.GONGSI_NOT_FOUND, "Gongsi not found with ID: " + gongsiId));
    return Long.valueOf(gongsi.getCompany().getId());
  }

  public GongsiResponse.GongsiListResponse getGongsiList(
      Long companyId,
      String sort,
      Boolean includeContent,
      Integer page,
      Integer size,
      String startDate,
      String endDate) {

    LocalDate parsedStartDate = null;
    LocalDate parsedEndDate = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    try {
      if (startDate != null && !startDate.isEmpty()) {
        parsedStartDate = LocalDate.parse(startDate, formatter);
      }

      if (endDate != null && !endDate.isEmpty()) {
        parsedEndDate = LocalDate.parse(endDate, formatter);
      }
    } catch (DateTimeParseException e) {
      throw new CustomException(
          ApiResponseCode.GONGSI_INVALID_DATE_PAIR, "Invalid date format. Use yyyy-MM-dd");
    }

    Page<GongsiEntity> gongsiPage;
    Pageable pageable = PageRequest.of(page - 1, size);

    if ("views".equals(sort)) {
      if (companyId != null) {
        CompanyEntity company =
            companyRepository
                .findById(companyId)
                .orElseThrow(
                    () ->
                        new CustomException(
                            ApiResponseCode.GONGSI_COMPANY_NOT_FOUND,
                            "Company not found with ID: " + companyId));

        gongsiPage =
            gongsiRepository.findByCompanyAndDateRangeOrderByViewCount(
                company.getId(), parsedStartDate, parsedEndDate, pageable);
      } else {
        gongsiPage =
            gongsiRepository.findByDateRangeOrderByViewCount(
                parsedStartDate, parsedEndDate, pageable);
      }
    } else {
      Sort.Direction direction;
      String sortProperty;

      if ("latest".equals(sort)) {
        direction = Sort.Direction.DESC;
        sortProperty = "createdDt";
      } else if ("oldest".equals(sort)) {
        direction = Sort.Direction.ASC;
        sortProperty = "createdDt";
      } else {
        throw new CustomException(
            ApiResponseCode.GONGSI_INVALID_SORT_TYPE, "Invalid sort parameter: " + sort);
      }

      pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortProperty));

      if (companyId != null) {
        CompanyEntity company =
            companyRepository
                .findById(companyId)
                .orElseThrow(
                    () ->
                        new CustomException(
                            ApiResponseCode.GONGSI_COMPANY_NOT_FOUND,
                            "Company not found with ID: " + companyId));

        gongsiPage =
            gongsiRepository.findByCompanyAndDateRange(
                company, parsedStartDate, parsedEndDate, pageable);
      } else {
        gongsiPage = gongsiRepository.findByDateRange(parsedStartDate, parsedEndDate, pageable);
      }
    }

    List<GongsiResponse.GongsiItem> gongsiItems =
        gongsiPage.getContent().stream()
            .map(
                entity -> {
                  int viewCount =
                      gongsiViewHistoryRepository.countUniqueViewsByGongsiId(entity.getId());
                  return mapToGongsiItem(entity, includeContent, viewCount);
                })
            .collect(Collectors.toList());

    PaginationResponse pagination =
        PaginationResponse.of(page, gongsiPage.getTotalPages(), gongsiPage.getTotalElements());

    return GongsiResponse.GongsiListResponse.of(gongsiItems, gongsiItems.size(), pagination);
  }

  @Transactional
  public GongsiResponse.GongsiDetailResponse getGongsiDetail(Long gongsiId, String ipAddress) {
    GongsiEntity gongsi =
        gongsiRepository
            .findById(gongsiId)
            .orElseThrow(
                () ->
                    new CustomException(
                        ApiResponseCode.GONGSI_NOT_FOUND, "Gongsi not found with ID: " + gongsiId));

    if (!gongsiViewHistoryRepository.existsByGongsiIdAndIpAddress(gongsiId, ipAddress)) {
      GongsiViewHistoryEntity viewHistory = new GongsiViewHistoryEntity(gongsiId, ipAddress);
      gongsiViewHistoryRepository.save(viewHistory);
    }

    int viewCount = gongsiViewHistoryRepository.countUniqueViewsByGongsiId(gongsiId);

    String formattedDate =
        gongsi.getCreatedDt().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"));

    GongsiResponse.GongsiInfo gongsiInfo =
        GongsiResponse.GongsiInfo.of(
            gongsi.getId(),
            gongsi.getGongsiTitle(),
            formattedDate,
            viewCount,
            gongsi.getContentSummary(),
            gongsi.getOriginalGongsiLink());

    CompanyEntity company = gongsi.getCompany();

    boolean isSubscribed = false;
    try {
      Long userId = getCurrentUserId();
      if (userId != null) {
        isSubscribed = notificationRepository.existsByUserIdAndCompanyId(userId, company.getId());
      }
    } catch (Exception e) {
      logger.error("Error checking subscription status: {}", e.getMessage());
    }

    double prdyCtr = 0.0;
    try {
      String stockCode = company.getStockCode();

      if (stockCode != null && !stockCode.isEmpty()) {
        prdyCtr = koreanInvestmentClient.getPrdyCtr(stockCode);
      }
    } catch (Exception e) {
      logger.warn("Error fetching prdyCtr: {}", e.getMessage());
      prdyCtr = 0.0; // Default value
    }

    CompanyResponse.CompanyInfo companyInfo =
        CompanyResponse.CompanyInfo.of(
            company.getId(), company.getCompanyName(), prdyCtr, isSubscribed);

    return GongsiResponse.GongsiDetailResponse.of(gongsiInfo, companyInfo);
  }

  private GongsiResponse.GongsiItem mapToGongsiItem(
      GongsiEntity entity, boolean includeContent, int viewCount) {
    String formattedDate =
        entity.getCreatedDt().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"));

    return GongsiResponse.GongsiItem.of(
        entity.getId(),
        entity.getGongsiTitle(),
        entity.getCompany().getCompanyName(),
        formattedDate,
        viewCount,
        includeContent ? entity.getContentSummary() : null);
  }
}
