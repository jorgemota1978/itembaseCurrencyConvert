package com.itembase.currencyconvert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.itembase.currencyconvert.exchangerateapi.ExchangeRateApiList;
import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Mono;

@Service("itembaseConversionService")
public class ConversionService {

	private final ExchangeRatesApiService exchangeRatesApiService;

	private final ExchangeRateApiList exchangeRateApiList;
	public ConversionService(ExchangeRatesApiService exchangeRatesApiService, ExchangeRateApiList exchangeRateApiList) {
		this.exchangeRatesApiService = exchangeRatesApiService;
		this.exchangeRateApiList = exchangeRateApiList;
	}

	public Mono<ConvertionResponseDto> convert(ConversionRequestDto conversionRequestDto) {

		return exchangeRatesApiService.getRate(conversionRequestDto.getFrom(), 
				conversionRequestDto.getTo(),
				exchangeRateApiList.getListOfExchangeRateApi().stream().collect(Collectors.toList()))
				.flatMap(rate -> {
					ConvertionResponseDto res = new ConvertionResponseDto();
					res.setAmount(conversionRequestDto.getAmount());
					res.setFrom(conversionRequestDto.getFrom());
					res.setTo(conversionRequestDto.getTo());

					Double converted = conversionRequestDto.getAmount() * rate;
					converted = new BigDecimal(converted).setScale(2, RoundingMode.HALF_UP).doubleValue();
					
					res.setConverted(converted);

					return Mono.just(res);

				})
				.switchIfEmpty(getErrorMonoConvertionResponseDto(conversionRequestDto))
				.log();

	}
	
	public Mono<ConvertionResponseDto> getErrorMonoConvertionResponseDto(ConversionRequestDto conversionRequestDto){
		ConvertionResponseDto res = new ConvertionResponseDto();
		res.setAmount(conversionRequestDto.getAmount());
		res.setFrom(conversionRequestDto.getFrom());
		res.setTo(conversionRequestDto.getTo());
		res.setError("No providers available");
		
		return Mono.just(res);
	}
}
