package com.itembase.currencyconvert.exchangerateapi;

import reactor.core.publisher.Mono;

public interface IExchangeRateApi {

	public Mono<Double> getRate(String fromCurrency, String toCurrency);
}
