package com.itembase.currencyconvert.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Mono;

@Service("itembaseConversionService")
public class ConversionService {

	Logger log = LoggerFactory.getLogger(ConversionService.class);
	
	private final ExchangeRatesApiService exchangeRatesApiService;

	public ConversionService(ExchangeRatesApiService exchangeRatesApiService) {
		this.exchangeRatesApiService = exchangeRatesApiService;
	}

	public Mono<ConvertionResponseDto> convert(ConversionRequestDto conversionRequestDto) {
		if(conversionRequestDto == null
				|| conversionRequestDto.getFrom() == null || conversionRequestDto.getFrom().trim().isEmpty()
				|| conversionRequestDto.getTo() == null || conversionRequestDto.getTo().trim().isEmpty()
				|| conversionRequestDto.getAmount() == null) {
			ConvertionResponseDto res = new ConvertionResponseDto();
			res.setError("There are issues with the provided request");
			return Mono.just(res);
		}
		
		return exchangeRatesApiService.getRate(conversionRequestDto.getFrom(), conversionRequestDto.getTo())
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
				.log();

	}
	
}
