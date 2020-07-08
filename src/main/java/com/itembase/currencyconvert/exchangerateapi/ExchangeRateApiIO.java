package com.itembase.currencyconvert.exchangerateapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiIO implements IExchangeRateApi {

	Logger log = LoggerFactory.getLogger(ExchangeRateApiIO.class);
	
	private WebClient webClient = WebClient.create("https://api.exchangeratesapi.io");

	private String baseURL = "";
	
	public ExchangeRateApiIO() {

	}

	@Override
	public Mono<Double> getRate(String fromCurrency, String toCurrency) {
		if(fromCurrency == null || toCurrency == null) {
			return Mono.error(new NoRatesForGivenCurrencyException("FromCurrency and/or toCurrency are null"));
		}
		
		return this.webClient.get()
				.uri(uriBuilder -> 
					uriBuilder.path(baseURL + "/latest")
					.queryParam("base", fromCurrency)
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
