package org.bob.siungongsi.client.clientinterface;

import org.bob.siungongsi.client.dto.GoogleAiDtos.GoogleAiRequest;
import org.bob.siungongsi.client.dto.GoogleAiDtos.GoogleAiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Profile("batch")
@Component
@HttpExchange
public interface GoogleAiClientInterface {

  @PostExchange("/models/gemini-2.0-flash:generateContent")
  GoogleAiResponse summarizeWithGemini2Flash(@RequestBody GoogleAiRequest documentContent);

  @PostExchange("/models/gemini-2.0-flash-lite:generateContent")
  GoogleAiResponse summarizeWithGemini2FlashLite(@RequestBody GoogleAiRequest documentContent);
}
