package com.itembase.currencyconvert.rest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;
import com.itembase.currencyconvert.service.ConversionService;

import reactor.core.publisher.Mono;

@Controller
@RestController
@RequestMapping("/currency")
public class ConversionController {

	private final ConversionService itembaseConversionService;
	
	public ConversionController(ConversionService itembaseConversionService) {
		this.itembaseConversionService = itembaseConversionService;
	}

	@PostMapping(value = "/convert")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ConvertionResponseDto> convert(@RequestBody ConversionRequestDto conversionRequestDto) {
		return itembaseConversionService.convert(conversionRequestDto);
	}
}
