package com.itembase.currencyconvert;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiCOM;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiIO;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiList;
import com.itembase.currencyconvert.exchangerateapi.IExchangeRateApi;
import com.itembase.currencyconvert.service.ExchangeRatesApiService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class ExchangeRatesApiServiceTests {

	@Mock
	private ExchangeRateApiIO exchangeRateApiIO;

	@Mock
	private ExchangeRateApiCOM exchangeRateApiCOM;

	@Mock
	private SecureRandom random;
	
	private ExchangeRatesApiService exchangeRatesApiService;
	
	@BeforeEach
    void setUp() throws IOException {
		List<IExchangeRateApi> listOfExchangeRateApi = new ArrayList<IExchangeRateApi>();
		listOfExchangeRateApi.add(exchangeRateApiIO);
		listOfExchangeRateApi.add(exchangeRateApiCOM);
		
		ExchangeRateApiList exchangeRateApiList = new ExchangeRateApiList();
		exchangeRateApiList.setListOfExchangeRateApi(listOfExchangeRateApi);
		exchangeRatesApiService = new ExchangeRatesApiService(exchangeRateApiList);
		exchangeRatesApiService.setRandom(random);
	}
	
	@Test
	public void test2ProvidersFailing() {
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.error(new NoRatesForGivenCurrencyException("No rate returned for provided to currency")));
		Mockito.when(exchangeRateApiCOM.getRate("EEE", "WWW")).thenReturn(Mono.error(new NoRatesForGivenCurrencyException("No rate returned for provided to currency")));
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyErrorMessage("None of the providers returned a satisfactory response");
	}
	
	@Test
	public void testFirstProviderFailing() {
		Mockito.when(random.nextInt(2)).thenReturn(0);
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.error(new NoRatesForGivenCurrencyException("No rate returned for provided to currency")));
		Mockito.when(random.nextInt(1)).thenReturn(0);
		Mockito.when(exchangeRateApiCOM.getRate("EEE", "WWW")).thenReturn(Mono.just(1.3));
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).expectNext(1.3).verifyComplete();
	}
	
	@Test
	public void testFirstProviderOK() {
		Mockito.when(random.nextInt(2)).thenReturn(0);
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.just(0.9));
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).expectNext(0.9).verifyComplete();
	}
	
	
	@Test
	public void test2ProvidersFailingUnknownHostException() {
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.error(new UnknownHostException("UnknownHostException")));
		Mockito.when(exchangeRateApiCOM.getRate("EEE", "WWW")).thenReturn(Mono.error(new UnknownHostException("UnknownHostException")));
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyErrorMessage("There are no providers available");
	}
	
	@Test
	public void testFirstProviderFailingUnknownHostException() {
		Mockito.when(random.nextInt(2)).thenReturn(0);
		Mockito.when(exchangeRateApiIO.getRate("EEE", "WWW")).thenReturn(Mono.error(new UnknownHostException("UnknownHostException")));
		Mockito.when(random.nextInt(1)).thenReturn(0);
		Mockito.when(exchangeRateApiCOM.getRate("EEE", "WWW")).thenReturn(Mono.just(1.3));
		
		Mono<Double> monoDouble = exchangeRatesApiService.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).expectNext(1.3).verifyComplete();
	}
	
}
