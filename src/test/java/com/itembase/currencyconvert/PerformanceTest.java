package com.itembase.currencyconvert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.reactive.function.client.WebClient;

import com.itembase.currencyconvert.model.dto.ConversionRequestDto;
import com.itembase.currencyconvert.model.dto.ConvertionResponseDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class PerformanceTest {

	private static WebClient webClient = WebClient.create("http://localhost:8080");

	private static Integer NUM_CALLS = 1000;
	
	public static void main(String args[]) {
		List<ConversionRequestDto> reqs = new ArrayList<ConversionRequestDto>();
		
		for (int i = 0; i < NUM_CALLS; i++) {
			double amount = ThreadLocalRandom.current().nextDouble(100, 2000);
			
			ConversionRequestDto req = new ConversionRequestDto();
			req.setFrom("EUR");
			req.setTo("USD");
			req.setAmount(amount);
			
			reqs.add(req);
		}
		
		long time = System.currentTimeMillis();
		
		Flux<ConvertionResponseDto> resps = convertListOfAmounts(reqs);
		System.out.println("All values converted: " + resps.all(resp -> resp.getConverted() != null).block());
		
		time = System.currentTimeMillis() - time;
		
		System.out.println("Number of calls: " + NUM_CALLS);
		System.out.println("Avg time for each call: " + time/NUM_CALLS + " ms");
		
		System.out.println("TOTAL TIME: " + time + "ms");
	}

	private static Mono<ConvertionResponseDto> callConversionService(ConversionRequestDto req) {
		long time = System.currentTimeMillis();
		
		Mono<ConversionRequestDto> monoReq = Mono.just(req);
		
		Mono<ConvertionResponseDto> monoResp = webClient.post()
			.uri("/currency/convert")
			.body(monoReq, ConversionRequestDto.class)
			.retrieve().bodyToMono(ConvertionResponseDto.class);
		
		time = System.currentTimeMillis() - time;
		
		return monoResp;
	}
	
	public static Flux<ConvertionResponseDto> convertListOfAmounts(List<ConversionRequestDto> reqs) {
	    return Flux.fromIterable(reqs)
	        .parallel()
	        .runOn(Schedulers.elastic())
	        .flatMap(PerformanceTest::callConversionService)
	        .ordered((u1, u2) -> u1.getAmount().compareTo(u2.getAmount()));
	}
}
