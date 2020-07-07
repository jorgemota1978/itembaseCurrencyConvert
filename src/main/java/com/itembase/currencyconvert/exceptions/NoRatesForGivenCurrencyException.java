package com.itembase.currencyconvert.exceptions;

public class NoRatesForGivenCurrencyException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3552669519234950003L;

	public NoRatesForGivenCurrencyException() {
		super();
	}

	public NoRatesForGivenCurrencyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoRatesForGivenCurrencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoRatesForGivenCurrencyException(String message) {
		super(message);
	}

	public NoRatesForGivenCurrencyException(Throwable cause) {
		super(cause);
	}

}
