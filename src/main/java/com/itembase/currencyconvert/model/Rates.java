package com.itembase.currencyconvert.model;

import java.util.Map;

public class Rates {

	private String base;
	
	private String date;
	
	private String time_last_updated;

	private Map<String, Double> rates;
	
	private String error;
	
	private String result;
	
	private String error_type;
	
	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime_last_updated() {
		return time_last_updated;
	}

	public void setTime_last_updated(String time_last_updated) {
		this.time_last_updated = time_last_updated;
	}

	public Map<String, Double> getRates() {
		return rates;
	}

	public void setRates(Map<String, Double> rates) {
		this.rates = rates;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getError_type() {
		return error_type;
	}

	public void setError_type(String error_type) {
		this.error_type = error_type;
	}
	
	

}
