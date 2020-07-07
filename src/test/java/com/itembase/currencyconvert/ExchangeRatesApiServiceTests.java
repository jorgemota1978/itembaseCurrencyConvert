package com.itembase.currencyconvert;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiCOM;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiIO;
import com.itembase.currencyconvert.exchangerateapi.IExchangeRateApi;
import com.itembase.currencyconvert.service.ExchangeRatesApiService;

import reactor.core.publisher.Mono;

@SpringBootTest
public class ExchangeRatesApiServiceTests {

	@Mock
	private ExchangeRateApiIO exchangeRateApiIO;

	@Mock
	private ExchangeRateApiCOM exchangeRateApiCOM;

	@Mock
	private SecureRandom random;
	
	private List<IExchangeRateApi> exchangeRateApiList = new ArrayList<IExchangeRateApi>();
	
	private ExchangeRatesApiService exchangeRatesApiService = new ExchangeRatesApiService();
	
	@BeforeEach
    void setUp() throws IOException {
		exchangeRateApiList = new ArrayList<IExchangeRateApi>();
		exchangeRateApiList.add(exchangeRateApiIO);
		exchangeRateApiList.add(exchangeRateApiCOM);
		
		exchangeRatesApiService.setRandom(random);
	}
	
	@Test
	public void test() {
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.empty());
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW", exchangeRateApiList);
		assertEquals(Mono.empty(), monoDouble);
	}
	
	@Test
	public void testGetRateThrowsError() {
		Mockito.when(random.nextInt(2)).thenReturn(0);
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenThrow(RuntimeException.class);
		Mockito.when(random.nextInt(1)).thenReturn(0);
		Mockito.when(exchangeRateApiCOM.getRate("EEE", "WWW")).thenThrow(RuntimeException.class);
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW", exchangeRateApiList);
		assertEquals(Mono.empty(), monoDouble);
	}
}
