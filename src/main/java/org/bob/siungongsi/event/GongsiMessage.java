package org.bob.siungongsi.event;

import org.bob.siungongsi.client.dto.OpenDartDtos.GongsiData;

public record GongsiMessage(String receiptNo, String receiptTitle, String companyCode) {

  public static GongsiMessage from(GongsiData gongsiData) {
    String cleanedTitle = gongsiData.reportNm().replaceAll("\\s{2,}", " ");
    return new GongsiMessage(gongsiData.rceptNo(), cleanedTitle, gongsiData.corpCode());
  }
}
