package org.bob.siungongsi.batch.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.CompanyNameAutofillEntity;
import org.bob.siungongsi.common.repository.CompanyNameAutofillRepository;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyNameAutofillGenerator {
  private final CompanyRepository companyRepository;
  private final CompanyNameAutofillRepository companyNameAutofillRepository;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

  public CompanyNameAutofillGenerator(
      CompanyRepository companyRepository,
      CompanyNameAutofillRepository companyNameAutofillRepository) {
    this.companyRepository = companyRepository;
    this.companyNameAutofillRepository = companyNameAutofillRepository;
  }

  @Transactional
  public void generate(String startDt, String endDt) {
    if (endDt == null) {
      endDt = LocalDateTime.now(KOREA_ZONE).format(formatter);
    }
    List<CompanyEntity> companies =
        companyRepository.findByCreatedDtBetween(
            LocalDateTime.parse(startDt, formatter), LocalDateTime.parse(endDt, formatter));

    for (CompanyEntity company : companies) {
      generateCombinations(company.getCompanyName())
          .forEach(
              keyword -> {
                CompanyNameAutofillEntity companyNameAutofillEntity =
                    new CompanyNameAutofillEntity(
                        keyword, company.getId(), company.getCompanyName());
                companyNameAutofillRepository.save(companyNameAutofillEntity);
              });
    }
  }

  private Set<String> generateCombinations(String input) {
    Set<String> result = new HashSet<>();
    String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < decomposed.length(); i++) {
      char ch = decomposed.charAt(i);
      if (Character.isLetter(ch)) {
        sb.append(ch);
        String keyword = sb.toString().replaceAll("\\p{M}", "");
        String key = Normalizer.normalize(keyword, Normalizer.Form.NFC);
        result.add(key);
      }
    }
    return result;
  }
}
