package com.itembase.currencyconvert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;
import com.itembase.currencyconvert.service.ConversionService;
import com.itembase.currencyconvert.service.ExchangeRatesApiService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class ConversionServiceTests {

	@Mock
	private ExchangeRatesApiService exchangeRatesApiService;
	
	private ConversionService itembaseConversionService;
	
	@BeforeEach
	void setup() {
		itembaseConversionService = new ConversionService(exchangeRatesApiService);
	}
	
	@Test
	public void testRequestWithNoData() {
		Mono<ConvertionResponseDto> monoResp = itembaseConversionService.convert(new ConversionRequestDto());
		
		StepVerifier.create(monoResp)
			.assertNext(respFromWS -> assertEquals("There are issues with the provided request", respFromWS.getError()))
			.verifyComplete();
	}
	
	@Test
	public void testOK() {
		Mockito.when(exchangeRatesApiService.getRate("EUR", "USD")).thenReturn(Mono.just(1.2));
				
		ConversionRequestDto req = new ConversionRequestDto();
		req.setFrom("EUR");
		req.setTo("USD");
		req.setAmount(200.90);
		
		Mono<ConvertionResponseDto> monoResp = itembaseConversionService.convert(req);
						
		StepVerifier.create(monoResp)
			.assertNext(respFromWS -> {
				assertEquals(req.getFrom(), respFromWS.getFrom());
				assertEquals(req.getTo(), respFromWS.getTo());
				assertEquals(req.getAmount(), respFromWS.getAmount());
				assertEquals(241.08, respFromWS.getConverted());
				
				assertNull(respFromWS.getError());
			})
			.verifyComplete();
	}
	
	@Test
	public void testCurrencyUnknown() {
		Mockito.when(exchangeRatesApiService.getRate("EEE", "WWW")).thenReturn(Mono.error(new NoRatesForGivenCurrencyException("None of the providers returned a satisfactory response")));
		
		ConversionRequestDto req = new ConversionRequestDto();
		req.setFrom("EEE");
		req.setTo("WWW");
		req.setAmount(200.90);
		
		Mono<ConvertionResponseDto> monoResp = itembaseConversionService.convert(req);
		StepVerifier.create(monoResp)
			.verifyErrorMessage("None of the providers returned a satisfactory response");
	}

}
