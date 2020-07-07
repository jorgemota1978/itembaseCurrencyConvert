package com.itembase.currencyconvert;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiIOCOMCommon;
import com.itembase.currencyconvert.model.Rates;
import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Mono;

@SpringBootTest
class ItembaseCurrencyConvertApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testCurrencyConvert() {
		ConversionRequestDto req = new ConversionRequestDto();
		req.setFrom("CHF");
		req.setTo("EUR");
		req.setAmount(3000.00);
		
		WebClient webClient = WebClient.create("http://localhost:8180");

		Mono<ConvertionResponseDto> monoresp = webClient.post().uri(uriBuilder -> 
		uriBuilder.path("/currency/convert")
		.build())
		.body(Mono.just(req), ConversionRequestDto.class)
			.retrieve()
			.bodyToMono(ConvertionResponseDto.class)
			.log();
		
		System.out.println("Retrieve done");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sleep done");
		
		ConvertionResponseDto resp = monoresp.block();
		
		System.out.println("Converted: " + resp.getConverted());
	}
}
