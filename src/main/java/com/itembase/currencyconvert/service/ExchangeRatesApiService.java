package com.itembase.currencyconvert.service;

import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itembase.currencyconvert.exceptions.NoRatesForGivenCurrencyException;
import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiList;
import com.itembase.currencyconvert.exchangerateapi.IExchangeRateApi;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRatesApiService {

	Logger log = LoggerFactory.getLogger(ExchangeRatesApiService.class);
	
	private SecureRandom random = new SecureRandom();
	
	private final ExchangeRateApiList exchangeRateApiList;
	
	public ExchangeRatesApiService(ExchangeRateApiList exchangeRateApiList) {

		this.exchangeRateApiList = exchangeRateApiList;
	}

	/**
	 * 
	 * 
	 * @param fromCurrency
	 * @param toCurrency
	 * @return
	 */
	public Mono<Double> getRate(String fromCurrency, String toCurrency) {
				
		return getRatePrivate(fromCurrency, toCurrency, 
				exchangeRateApiList.getListOfExchangeRateApi().stream().collect(Collectors.toList()),
				exchangeRateApiList.getListOfExchangeRateApi().size(), 0);
	}
	
	private Mono<Double> getRatePrivate(String fromCurrency, String toCurrency,
			List<IExchangeRateApi> listOfExchangeRateApi, 
			Integer initialSize, Integer unavailableProviders) {
		
		Integer sizeOfListOfExchangeRateApi = listOfExchangeRateApi.size();
		if(sizeOfListOfExchangeRateApi <= 0) {
			if(initialSize == unavailableProviders) {
				return Mono.error(new NoRatesForGivenCurrencyException("There are no providers available"));
			}
			return Mono.error(new NoRatesForGivenCurrencyException("None of the providers returned a satisfactory response"));
		}
		
		// Index of the next ExchangeRateApi to call, choosen randomly
		Integer indexNextOfListOfExchangeRateApi = random.nextInt(sizeOfListOfExchangeRateApi);
		
		// Next ExchangeRateApi to call
		IExchangeRateApi exchangeRateApi = listOfExchangeRateApi
				.get(indexNextOfListOfExchangeRateApi);
		
		// Remove it from the list so that if there is an error we can try another provider
		listOfExchangeRateApi.remove(exchangeRateApi);
		
		return exchangeRateApi.getRate(fromCurrency, toCurrency)
				.onErrorResume(functionFallback(fromCurrency, toCurrency, listOfExchangeRateApi,
						initialSize, unavailableProviders));
	}
	
	private Function<Throwable, Mono<Double>> functionFallback(String fromCurrency, String toCurrency,
			List<IExchangeRateApi> exchangeRateApiList, Integer initialSize, 
			final Integer unavailableProviders){
		return c -> {
			
			
			Integer unavailableProvidersTmp = unavailableProviders;
			if(c instanceof UnknownHostException) {
				unavailableProvidersTmp++;
			}
			
			return getRatePrivate(fromCurrency, toCurrency, exchangeRateApiList, initialSize, unavailableProvidersTmp);
		};
	}

	public void setRandom(SecureRandom random) {
		this.random = random;
	}
	
	
}
