package com.itembase.currencyconvert;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
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

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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
	public void testNullRatesForFromCurrency() throws JsonProcessingException {
		
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	Rates nullRates = new Rates(); 
		    	nullRates.setBase("AAA");
		    	
		        try {
					switch (request.getPath()) {
					    case "/latest?base=AAA":
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(nullRates))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/AAA":
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(nullRates))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
		
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("AAA", "BBB");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("Rates not returned from provider");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("AAA", "BBB");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("Rates not returned from provider");
	}
	
	@Test
	public void testFromCurrencyDoesNotMatchWithBase() throws JsonProcessingException {
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	Rates rates = new Rates(); 
				rates.setBase("EUR");
				
		        try {
					switch (request.getPath()) {
					    case "/latest?base=CCC":
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/CCC":
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
		
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("CCC", "DDD");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("Rates not returned from provider");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("CCC", "DDD");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("Rates not returned from provider");
	}
	

	@Test
	public void testUnsupportedFromCurrency() throws JsonProcessingException {
		
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	
		        try {
					switch (request.getPath()) {
					    case "/latest?base=EEE":
					    	Rates ratesIO = new Rates(); 
					    	ratesIO.setError("Base 'EEE' is not supported.");
					    	
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(ratesIO))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/EEE":
					    	Rates ratesCOM = new Rates(); 
					    	ratesCOM.setResult("error");
					    	ratesCOM.setError_type("unsupported_code");
					    	
					        return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(ratesCOM))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
		
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EEE", "FFF");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("Rates not supported from provider");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EEE", "FFF");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("Rates not supported from provider");
	}
	
	@Test
	public void testToCurrencyNotInRates() throws JsonProcessingException {
		
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	Rates rates = new Rates(); 
				rates.setBase("GGG");
				rates.setRates(new HashMap<String, Double>());
				
		        try {
					switch (request.getPath()) {
					    case "/latest?base=GGG":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/GGG":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
				
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("GGG", "HHH");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("No rate returned for provided to currency");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("GGG", "HHH");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("No rate returned for provided to currency");
	}
	
	@Test
	public void testToCurrencyWithRateNull() throws JsonProcessingException {
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	HashMap<String, Double> ratesList = new HashMap<String, Double>();
				ratesList.put("JJJ", null);
				
				Rates rates = new Rates(); 
				rates.setBase("III");
				rates.setRates(ratesList);
								
		        try {
					switch (request.getPath()) {
					    case "/latest?base=III":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/III":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
						
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("III", "JJJ");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("No rate returned for provided to currency");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("III", "JJJ");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("No rate returned for provided to currency");
	}
	
	@Test
	public void testToCurrencyWithRateOK() throws JsonProcessingException {
		final Dispatcher dispatcher = new Dispatcher() {

		    @Override
		    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
		    	HashMap<String, Double> ratesList = new HashMap<String, Double>();
				ratesList.put("USD", 1.2);
				
				Rates rates = new Rates(); 
				rates.setBase("EUR");
				rates.setRates(ratesList);
												
		        try {
					switch (request.getPath()) {
					    case "/latest?base=EUR":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					    case "/v4/latest/EUR":
					    	return new MockResponse()
					        		.setResponseCode(200)
					        		.setBody(objectMapper.writeValueAsString(rates))
							      	.addHeader("Content-Type", "application/json");
					}
					return new MockResponse().setResponseCode(404);
				} catch (JsonProcessingException e) {
					throw new InterruptedException(e.getMessage());
				}
		    }
		};
		mockBackEnd.setDispatcher(dispatcher);
							
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("EUR", "USD");
		StepVerifier.create(monoDouble)
			.expectNext(1.2)
			.verifyComplete();
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("EUR", "USD");
		StepVerifier.create(monoDouble2)
			.expectNext(1.2)
			.verifyComplete();
	}
	
	@Test
	public void testFromCurrencyNull() throws JsonProcessingException {
		Mono<Double> monoDouble = exchangeRateApiIO.getRate(null, "USD");
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("FromCurrency and/or toCurrency are null");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate(null, "USD");
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("FromCurrency and/or toCurrency are null");
	}
	
	@Test
	public void testToCurrencyNull() throws JsonProcessingException {
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("USD", null);
		StepVerifier.create(monoDouble)
			.verifyErrorMessage("FromCurrency and/or toCurrency are null");
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("USD", null);
		StepVerifier.create(monoDouble2)
			.verifyErrorMessage("FromCurrency and/or toCurrency are null");
	}
	
	@Test
	public void testUnknownHost() throws JsonProcessingException {
		exchangeRateApiIO.setBaseURL("http://unknownhost.com:8080");
        exchangeRateApiCOM.setBaseURL("http://unknownhost.com:8080");
        		
		Mono<Double> monoDouble = exchangeRateApiIO.getRate("USD", "EUR");
		StepVerifier.create(monoDouble).verifyError(UnknownHostException.class);
		
		Mono<Double> monoDouble2 = exchangeRateApiCOM.getRate("USD", "EUR");
		StepVerifier.create(monoDouble2).verifyError(UnknownHostException.class);
		
		String baseUrl = mockBackEnd.getHostName() + ":" + mockBackEnd.getPort();
        exchangeRateApiIO.setBaseURL(baseUrl);
        exchangeRateApiCOM.setBaseURL(baseUrl);
        
	}
	
}
