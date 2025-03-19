package org.bob.siungongsi.event;

import org.bob.siungongsi.client.dto.OpenDartDtos.GongsiData;
import org.bob.siungongsi.domain.ProcessingFailedGongsiEntity;

public record GongsiMessage(String receiptNo, String receiptTitle, String companyCode) {

  public static GongsiMessage from(GongsiData gongsiData) {
    String cleanedTitle = gongsiData.reportNm().replaceAll("\\s{2,}", " ");
    return new GongsiMessage(gongsiData.rceptNo(), cleanedTitle, gongsiData.corpCode());
  }

  public static GongsiMessage from(ProcessingFailedGongsiEntity failedGongsi) {
    return new GongsiMessage(
        failedGongsi.getGongsiCode(), failedGongsi.getGongsiTitle(), failedGongsi.getCompanyCode());
  }
}
