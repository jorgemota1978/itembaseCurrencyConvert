# Itembase Currency Convert

### How to download the application

Use the following command to clone the application:
  - git clone https://github.com/jorgemota1978/itembaseCurrencyConvert.git
  
### How to run the application

There are different ways to run the application:

**Docker**
  1. In the root folder of the application, itembaseCurrencyConvert/, go to folder docker: cd docker
  2. Build docker image (Use sudo if your user does not have permissions): docker build -t currencyconvert:0.0.1 .
  2. (Use sudo if your user does not have permissions) Run docker image: docker run  -p 8080:8080 -t currencyconvert:0.0.1
  
With the above command the application starts on port 8080, if you want to run on port 8180 just use ... -p 8180:8080 ...

**Maven**
  1. In the root folder of the application, itembaseCurrencyConvert/, run: mvn spring-boot:run
  
### How to send requests

1. You can send requests via Swagger. Please access: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
2. You can send requests in Java, please see examples in class IntegrationTests
3. With curl command, example: curl -d '{"from":"EUR", "to":"USD", "amount":200.04}' -H "Content-Type: application/json" -X POST http://localhost:8080/currency/convert

### How is the application designed

When sending a request to /currency/convert a Mono<ConvertionResponseDto> is returned with the converted value or with a error explaining what was the problem.

Below is a small explanation of each component:
  - Rest Controller: ConversionController
  - Services
    - ConversionService: This service is in charge of doing the conversion with the rate value returned by the ExchangeRatesApiService;
    - ExchangeRatesApiService: This service has a list of providers, and it randomly asks to any of those providers what is the rate for a given tupple (from, to) currencies. We can add as many providers as we need in this list;
  - Providers using WebClient
    - ExchangeRateApiIO: Provider client for https://api.exchangeratesapi.io
    - ExchangeRateApiCOM: Provider client for https://api.exchangerate-api.com
    
### Tests

Below is a small explanation of each test class developed:
  - ExchangeRateApisTests: Unit tests on the providers using MockWebServer to simulate responses;
  - ExchangeRatesApiServiceTests: Unit tests on ExchangeRatesApiService using Mockito to mock the providers;
  - ConversionServiceTests: Unit tests on ConversionService using Mockito to mock ExchangeRatesApiService;
  - IntegrationTests: This runnable class start a spring boot and does real end to end tests (This class does not run when running maven);
  
### Improvements

Here are some improvements that can be done to the application:
1. Caching of provider responses
2. Caching of the endpoint responses: We can use annotation @EnableCaching to enable caching of our responses on the Rest controller;
3. Tests on performance with parallel requests: Further testing would be needed, for example to see how the application behaves in terms of performance. 
4. Authentication of endpoint /currency/convert: We can use spring-security to add authentication to our endpoint;

**Authentication to providers** - If the providers need authentication we can use the following:
```
	private WebClient client = WebClient.builder()
            	.defaultHeaders(header -> header.setBasicAuth(userName, password))
            	.build();
```
or
```
	Mono<String> response = client.get()
            	.url("/customers")
            	.headers(headers -> headers.setBasicAuth(userName, password))
            	.retrieve()
            	.bodyToFlux(String.class);
```
or if we need to authenticate with SSL client side:
```
	SslContext sslContext = SslContextBuilder
            	.forClient()
            	.trustManager(InsecureTrustManagerFactory.INSTANCE)
            	.build();
	ClientHttpConnector httpConnector = HttpClient.create().secure(t -> t.sslContext(sslContext) )
	return WebClient.builder().clientConnector(httpConnector).build();
```

**Caching of provider responses** - To improve performance we can cache the provider responses with:
- Spring CacheManager
- Mono.cache
- 