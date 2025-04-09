package org.bob.siungongsi.batch.controller;

import java.util.List;

import org.bob.siungongsi.batch.controller.spec.BatchSupportControllerSpec;
import org.bob.siungongsi.batch.service.CompanyNameAutofillGenerator;
import org.bob.siungongsi.batch.service.ProcessingFailedGongsiService;
import org.bob.siungongsi.batch.service.TodayProcessedGongsiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("batch")
@RestController
@RequestMapping("/support")
public class BatchSupportController implements BatchSupportControllerSpec {
  @Value("${admin.key}")
  private String adminKey;

  private final TodayProcessedGongsiService todayProcessedGongsiService;
  private final ProcessingFailedGongsiService processingFailedGongsiService;
  private final CompanyNameAutofillGenerator companyNameAutofillGenerator;

  public BatchSupportController(
      TodayProcessedGongsiService todayProcessedGongsiService,
      ProcessingFailedGongsiService processingFailedGongsiService,
      CompanyNameAutofillGenerator companyNameAutofillGenerator) {
    this.todayProcessedGongsiService = todayProcessedGongsiService;
    this.processingFailedGongsiService = processingFailedGongsiService;
    this.companyNameAutofillGenerator = companyNameAutofillGenerator;
  }

  @PostMapping("/remove-processed-gongsi")
  @Override
  public void removeProcessedGongsiList(
      @RequestParam("api-key") String apiKey, @RequestBody List<String> gongsiCodes) {
    if (!adminKey.equals(apiKey)) {
      throw new RuntimeException("Invalid admin key");
    }
    todayProcessedGongsiService.removeGongsiList(gongsiCodes);
  }

  @PostMapping("/retry-failed-gongsi")
  @Override
  public void retryFailedGongsiList(
      @RequestParam("api-key") String apiKey, @RequestBody List<String> gongsiCodes) {
    if (!adminKey.equals(apiKey)) {
      throw new RuntimeException("Invalid admin key");
    }
    processingFailedGongsiService.retryGongsiMessageList(gongsiCodes);
  }

  @PostMapping("/company-name-autofill")
  @Override
  public void autofillCompanyName(
      @RequestParam("api-key") String apiKey,
      @RequestParam(value = "startDt") String startDt,
      @RequestParam(value = "endDt", required = false) String endDt) {
    if (!adminKey.equals(apiKey)) {
      throw new RuntimeException("Invalid admin key");
    }
    companyNameAutofillGenerator.generate(startDt, endDt);
  }
}
