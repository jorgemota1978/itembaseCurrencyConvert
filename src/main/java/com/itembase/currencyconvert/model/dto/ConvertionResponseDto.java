package com.itembase.currencyconvert.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ConvertionResponseDto {

	@JsonInclude(value = Include.NON_NULL)
	private String from;
	
	@JsonInclude(value = Include.NON_NULL)
	private String to;
		
	@JsonInclude(value = Include.NON_NULL)
	private Double amount;

	@JsonInclude(value = Include.NON_NULL)
	private Double converted;

	@JsonInclude(value = Include.NON_NULL)
	private String error;
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getConverted() {
		return converted;
	}

	public void setConverted(Double converted) {
		this.converted = converted;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
