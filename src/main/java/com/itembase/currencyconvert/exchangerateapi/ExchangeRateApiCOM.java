package com.itembase.currencyconvert.exchangerateapi;

import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiCOM implements IExchangeRateApi {

	private WebClient webClient = WebClient.create("https://api.exchangerate-api.com");

	public ExchangeRateApiCOM() {

	}

	@Override
	public Mono<Double> getRate(String fromCurrency, String toCurrency) {
		return this.webClient.get().uri(uriBuilder -> 
			uriBuilder.path("/v4/latest/" + fromCurrency)
			.build())
				.retrieve()
				.bodyToMono(Rates.class)
				.flatMap(ExchangeRateApiIOCOMCommon.functionGetRate(toCurrency))
				.log();
	}

}
