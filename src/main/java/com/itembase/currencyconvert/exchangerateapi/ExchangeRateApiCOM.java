package com.itembase.currencyconvert.exchangerateapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiCOM implements IExchangeRateApi {
	Logger log = LoggerFactory.getLogger(ExchangeRateApiCOM.class);
	
	private WebClient webClient = WebClient.create("https://api.exchangerate-api.com");

	private String baseURL = "";
	
	public ExchangeRateApiCOM() {

	}

	@Override
	public Mono<Double> getRate(String fromCurrency, String toCurrency) {
		if(fromCurrency == null || toCurrency == null) {
			return Mono.error(new NoRatesForGivenCurrencyException("FromCurrency and/or toCurrency are null"));
		}
		
			return this.webClient.get().uri(uriBuilder -> 
				uriBuilder.path(baseURL + "/v4/latest/" + fromCurrency)
				.build())
					.retrieve()
					.bodyToMono(Rates.class)
					.flatMap(ExchangeRateApiIOCOMCommon.functionGetRate(fromCurrency, toCurrency))
					.log();
		
	}

	public void setBaseURL(String baseURL) {
		webClient = WebClient.create();
		this.baseURL = baseURL;
		
		
	}
	
	

}
