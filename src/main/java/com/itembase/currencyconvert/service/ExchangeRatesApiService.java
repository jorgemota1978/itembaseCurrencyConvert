package com.itembase.currencyconvert.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.exchangerateapi.IExchangeRateApi;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRatesApiService {

	Logger log = LoggerFactory.getLogger(ExchangeRatesApiService.class);
	
	private SecureRandom random = new SecureRandom();
	
	public ExchangeRatesApiService() {
		
	}

	/**
	 * Recursive method that calls another ExchangeRateApi if one is unavailable
	 * 
	 * @param fromCurrency
	 * @param toCurrency
	 * @return
	 */
	public Mono<Double> getRate(String fromCurrency, String toCurrency, 
			List<IExchangeRateApi> exchangeRateApiList) {
		log.debug("getRate");
		
		Integer sizeOfListOfExchangeRateApi = exchangeRateApiList.size();
		if(sizeOfListOfExchangeRateApi <= 0) {
			return Mono.error(new NoRatesForGivenCurrencyException());
		}
		
		// Index of the next ExchangeRateApi to call, choosen randomly
		Integer indexNextOfListOfExchangeRateApi = random.nextInt(sizeOfListOfExchangeRateApi);
		
		// Next ExchangeRateApi to call
		IExchangeRateApi exchangeRateApi = exchangeRateApiList
				.get(indexNextOfListOfExchangeRateApi);
		
		// Remove it from the list so that if there is an error we can try another provider
		exchangeRateApiList.remove(exchangeRateApi);
		
		return exchangeRateApi.getRate(fromCurrency, toCurrency)
				.onErrorResume(functionFallback(fromCurrency, toCurrency, exchangeRateApiList));
	}
	
	private Function<Throwable, Mono<Double>> functionFallback(String fromCurrency, String toCurrency,
			List<IExchangeRateApi> exchangeRateApiList){
		return c -> {
			
			return getRate(fromCurrency, toCurrency, exchangeRateApiList);
		};
	}

	public void setRandom(SecureRandom random) {
		this.random = random;
	}
	
	
}
