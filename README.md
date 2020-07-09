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

You can send requests via Swagger. Please access: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

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

caching
authentication