package org.bob.siungongsi.client.clientinterface;

import org.bob.siungongsi.client.dto.OpenDartDtos.GongsiListResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@Profile("batch")
@Component
@HttpExchange
public interface OpenDartClientInterface {
  @GetExchange("/list.json")
  GongsiListResponse getOpenDartList(
      @RequestParam("bgn_de") String bgnDe,
      @RequestParam("end_de") String endDe,
      @RequestParam("page_no") int pageNo,
      @RequestParam("page_count") int pageCount);

  @GetExchange("/document.xml")
  byte[] getOpenDartDocument(@RequestParam("rcept_no") String rceptNo);
}
