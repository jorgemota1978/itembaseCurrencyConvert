package com.itembase.currencyconvert.exchangerateapi;

import java.util.function.Function;

import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiIOCOMCommon {

	public static Function<Rates, Mono<Double>> functionGetRate(String toCurrency){
		return r -> {

			Double rate = r.getRates().get(toCurrency);

			return Mono.just(rate);

		};
	}

}
