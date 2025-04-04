package org.bob.siungongsi.batch.service;

import java.util.List;

import org.bob.siungongsi.batch.domain.ProcessingFailedGongsiEntity;
import org.bob.siungongsi.batch.event.GongsiMessage;
import org.bob.siungongsi.batch.repository.ProcessingFailedGongsiRepository;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("batch")
@Service
public class ProcessingFailedGongsiService {
  private final ProcessingFailedGongsiRepository processingFailedGongsiRepository;
  private final MessageSender messageSender;

  public ProcessingFailedGongsiService(
      ProcessingFailedGongsiRepository processingFailedGongsiRepository,
      MessageSender messageSender) {
    this.processingFailedGongsiRepository = processingFailedGongsiRepository;
    this.messageSender = messageSender;
  }

  public void retryGongsiMessageList(List<String> gongsiCodes) {
    for (String gongsiCode : gongsiCodes) {
      retryGongsiMessage(gongsiCode);
    }
  }

  private void retryGongsiMessage(String gongsiCode) {
    ProcessingFailedGongsiEntity failedGongsi =
        processingFailedGongsiRepository
            .findByGongsiCode(gongsiCode)
            .orElseThrow(
                () ->
                    new CustomException(
                        ApiResponseCode.FAILED_GONGSI_NOT_FOUND,
                        "해당 공시가 존재하지 않습니다. 공시코드: " + gongsiCode));
    GongsiMessage gongsiMessage = GongsiMessage.from(failedGongsi);
    messageSender.sendGongsiMessage(gongsiMessage);
  }
}
