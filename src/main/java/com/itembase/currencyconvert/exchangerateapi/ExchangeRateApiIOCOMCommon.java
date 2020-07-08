package com.itembase.currencyconvert.exchangerateapi;

import java.util.function.Function;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.model.Rates;

import reactor.core.publisher.Mono;

public class ExchangeRateApiIOCOMCommon {

	public static Function<Rates, Mono<Double>> functionGetRate(String fromCurrency, String toCurrency){
		return r -> {
			if(r.getError() != null && r.getError().equals("Base '" + fromCurrency + "' is not supported.")) return Mono.error(new NoRatesForGivenCurrencyException("Rates not supported from provider"));
			
			if(r.getResult() != null && r.getResult().equals("error") 
					&& r.getError_type() != null && r.getError_type().equals("unsupported_code")) return Mono.error(new NoRatesForGivenCurrencyException("Rates not supported from provider"));
			
			if(r.getRates() == null) return Mono.error(new NoRatesForGivenCurrencyException("Rates not returned from provider"));
			
			if(!fromCurrency.equalsIgnoreCase(r.getBase())) return Mono.error(new NoRatesForGivenCurrencyException("Base currency is not the same as from currency"));
						
			Double rate = r.getRates().get(toCurrency);

			return rate == null ? Mono.error(new NoRatesForGivenCurrencyException("No rate returned for provided to currency")) : Mono.just(rate);

		};
	}

}
