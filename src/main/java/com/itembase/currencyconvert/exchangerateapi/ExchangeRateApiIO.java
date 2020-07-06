package com.itembase.currencyconvert.exchangerateapi;

import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.model.Rates;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Mono;

public class ExchangeRateApiIO implements IExchangeRateApi {

	private WebClient webClient = WebClient.create("https://api.exchangeratesapi.io");

	public ExchangeRateApiIO() {

	}

	@Override
	public Mono<Double> getRate(String fromCurrency, String toCurrency) {
		return this.webClient.get()
				.uri(uriBuilder -> 
					uriBuilder.path("/latest")
					.queryParam("base", fromCurrency)
					.build())
				.retrieve()
				.bodyToMono(Rates.class)
				.flatMap(ExchangeRateApiIOCOMCommon.functionGetRate(toCurrency))
				.log();

	}

}
