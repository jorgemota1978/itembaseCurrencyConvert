package com.itembase.currencyconvert.exchangerateapi;

import java.util.function.Function;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiIOCOMCommon {

	public static Function<Rates, Mono<Double>> functionGetRate(String fromCurrency, String toCurrency){
		return r -> {
			System.out.println("From Currency: " + fromCurrency + ", base: " + r.getBase());
			if(!fromCurrency.equalsIgnoreCase(r.getBase())) return Mono.error(new NoRatesForGivenCurrencyException("Base currency is not the same as from currency"));;
			
			if(r.getRates() == null) return Mono.error(new NoRatesForGivenCurrencyException("Rates not returned from provider"));;
			
			Double rate = r.getRates().get(toCurrency);

			return rate == null ? Mono.error(new NoRatesForGivenCurrencyException("No rate returned for provided to currency")) : Mono.just(rate);

		};
	}

}
