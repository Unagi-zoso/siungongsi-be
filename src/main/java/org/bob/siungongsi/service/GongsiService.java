package org.bob.siungongsi.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.GongsiResponse;
import org.bob.siungongsi.controller.dto.PaginationResponse;
import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.GongsiEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.CompanyRepository;
import org.bob.siungongsi.repository.GongsiRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GongsiService {
  private final GongsiRepository gongsiRepository;
  private final CompanyRepository companyRepository;

  public GongsiService(GongsiRepository gongsiRepository, CompanyRepository companyRepository) {
    this.gongsiRepository = gongsiRepository;
    this.companyRepository = companyRepository;
  }

  public GongsiResponse.GongsiListResponse getGongsiList(
      Long companyId,
      String sort,
      Boolean includeContent,
      Integer page,
      Integer size,
      String startDate,
      String endDate) {

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

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortProperty));

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

    if (companyId != null) {
      CompanyEntity company =
          companyRepository
              .findById(companyId.toString())
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

    List<GongsiResponse.GongsiItem> gongsiItems =
        gongsiPage.getContent().stream()
            .map(entity -> mapToGongsiItem(entity, includeContent))
            .collect(Collectors.toList());

    PaginationResponse pagination =
        PaginationResponse.of(page, gongsiPage.getTotalPages(), gongsiPage.getTotalElements());

    return GongsiResponse.GongsiListResponse.of(gongsiItems, gongsiItems.size(), pagination);
  }

  private GongsiResponse.GongsiItem mapToGongsiItem(GongsiEntity entity, boolean includeContent) {
    String formattedDate =
        entity.getCreatedDt().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"));

    return GongsiResponse.GongsiItem.of(
        entity.getId(),
        entity.getGongsiTitle(),
        entity.getCompany().getCompanyName(),
        formattedDate,
        includeContent ? entity.getContentSummary() : null);
  }
}
