package com.itembase.currencyconvert.exchangerateapi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ExchangeRateApiList {

	private List<IExchangeRateApi> listOfExchangeRateApi = new ArrayList();
	
	{
		listOfExchangeRateApi.add(new ExchangeRateApiIO());
		listOfExchangeRateApi.add(new ExchangeRateApiCOM());
	}

	public List<IExchangeRateApi> getListOfExchangeRateApi() {
		return listOfExchangeRateApi;
	}

	public void setListOfExchangeRateApi(List<IExchangeRateApi> listOfExchangeRateApi) {
		this.listOfExchangeRateApi = listOfExchangeRateApi;
	}
	
	

}
