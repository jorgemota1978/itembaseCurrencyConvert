package com.itembase.currencyconvert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class IntegrationTests {

	
	private static WebClient webClient = WebClient.create("http://localhost:8080");

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ItembaseCurrencyConvertApplication.class, new String[] {});
		try {
						
			testRequestWithNoData();
			
			testCurrencyUnknown();
			
			testOK();
			
			System.out.println("\n\nALL TESTS PASSED SUCCESSFULLY");
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			SpringApplication.exit(ctx, () -> 0);
			System.exit(0);
		}
			
	}
	
	public static void testRequestWithNoData() {
		Mono<ConversionRequestDto> monoReq = Mono.just(new ConversionRequestDto());
		
		Mono<ConvertionResponseDto> monoResp = webClient.post()
			.uri("/currency/convert")
			.body(monoReq, ConversionRequestDto.class)
			.retrieve().bodyToMono(ConvertionResponseDto.class);
		
		StepVerifier.create(monoResp)
			.assertNext(respFromWS -> assertEquals("There are issues with the provided request", respFromWS.getError()))
			.verifyComplete();
	}
	
	public static void testOK() {
		ConversionRequestDto req = new ConversionRequestDto();
		req.setFrom("EUR");
		req.setTo("USD");
		req.setAmount(200.90);
		Mono<ConversionRequestDto> monoReq = Mono.just(req);
		
		Mono<ConvertionResponseDto> monoResp = webClient.post()
			.uri("/currency/convert")
			.body(monoReq, ConversionRequestDto.class)
			.retrieve().bodyToMono(ConvertionResponseDto.class);
		
		ConvertionResponseDto res = new ConvertionResponseDto();
		res.setError("There are issues with the provided request");
				
		StepVerifier.create(monoResp)
			.assertNext(respFromWS -> {
				assertEquals(req.getFrom(), respFromWS.getFrom());
				assertEquals(req.getTo(), respFromWS.getTo());
				assertEquals(req.getAmount(), respFromWS.getAmount());
				assertNotNull(respFromWS.getConverted());
				assertNull(respFromWS.getError());
			})
			.verifyComplete();
	}
	
	public static void testCurrencyUnknown() {
		ConversionRequestDto req = new ConversionRequestDto();
		req.setFrom("EEE");
		req.setTo("USD");
		req.setAmount(200.90);
		Mono<ConversionRequestDto> monoReq = Mono.just(req);
		
		Mono<ConvertionResponseDto> monoResp = webClient.post()
			.uri("/currency/convert")
			.body(monoReq, ConversionRequestDto.class)
			.retrieve().bodyToMono(ConvertionResponseDto.class);
		
		ConvertionResponseDto res = new ConvertionResponseDto();
		res.setError("There are issues with the provided request");
				
		StepVerifier.create(monoResp)
			.verifyError();
	}
	
}
