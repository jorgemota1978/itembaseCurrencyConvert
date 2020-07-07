package com.itembase.currencyconvert;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiCOM;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiIO;
import com.itembase.currencyconvert.model.Rates;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class ExchangeRateApisTests {

	public static MockWebServer mockBackEnd;
	 
	private ExchangeRateApiIO exchangeRateApiIO = new ExchangeRateApiIO();
	
	private ExchangeRateApiCOM exchangeRateApiCOM = new ExchangeRateApiCOM();
	
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }
 
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
	
    @BeforeEach
    void initialize() {
        String baseUrl = mockBackEnd.getHostName() + ":" + mockBackEnd.getPort();
        exchangeRateApiIO.setBaseURL(baseUrl);
        exchangeRateApiCOM.setBaseURL(baseUrl);
    }
       
	@Test
	@Order(1)
	public void testNullRatesForFromCurrency() throws JsonProcessingException {
		Rates nullRates = new Rates(); 
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(nullRates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(nullRates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
	
	@Test
	@Order(2)
	public void testFromCurrencyDoesNotMatchWithBase() throws JsonProcessingException {
		Rates rates = new Rates(); 
		rates.setBase("EUR");
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
	
	@Test
	@Order(3)
	public void testToCurrencyNotInRates() throws JsonProcessingException {
		Rates rates = new Rates(); 
		rates.setBase("EEE");
		rates.setRates(new HashMap<String, Double>());
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
	
	@Test
	@Order(4)
	public void testToCurrencyWithRateNull() throws JsonProcessingException {
		HashMap<String, Double> ratesList = new HashMap<String, Double>();
		ratesList.put("WWW", null);
		
		Rates rates = new Rates(); 
		rates.setBase("EEE");
		rates.setRates(ratesList);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EEE", "WWW");
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
	
	@Test
	@Order(5)
	public void testToCurrencyWithRateOK() throws JsonProcessingException {
		HashMap<String, Double> ratesList = new HashMap<String, Double>();
		ratesList.put("USD", 1.2);
		
		Rates rates = new Rates(); 
		rates.setBase("EUR");
		rates.setRates(ratesList);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EUR", "USD");
		StepVerifier.create(monoDouble)
			.expectNext(1.2)
			.verifyComplete();
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EUR", "USD");
		StepVerifier.create(monoDouble2)
			.expectNext(1.2)
			.verifyComplete();
	}
	
	@Test
	@Order(6)
	public void testFromCurrencyNull() throws JsonProcessingException {
		Rates rates = new Rates(); 
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate(null, "USD");
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate(null, "USD");
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
	
	@Test
	@Order(7)
	public void testToCurrencyNull() throws JsonProcessingException {
		Rates rates = new Rates(); 
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("USD", null);
		StepVerifier.create(monoDouble).verifyError(NoRatesForGivenCurrencyException.class);
		
		mockBackEnd.enqueue(new MockResponse()
			      .setBody(objectMapper.writeValueAsString(rates))
			      .addHeader("Content-Type", "application/json"));
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("USD", null);
		StepVerifier.create(monoDouble2).verifyError(NoRatesForGivenCurrencyException.class);
	}
}
