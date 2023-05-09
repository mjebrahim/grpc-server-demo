package com.example.grpcserver;

import com.example.Stock;
import com.example.StockQuote;
import com.example.StockQuoteProviderGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class StockService extends StockQuoteProviderGrpc.StockQuoteProviderImplBase {
    private static final Logger logger = LoggerFactory.getLogger(StockService.class.getName());

    StockService() {
    }

    private static double fetchStockPriceBid(Stock stock) {

        return stock.getTickerSymbol()
                .length()
                + ThreadLocalRandom.current()
                .nextDouble(-0.1d, 0.1d);
    }

    @Override
    public void serverSideStreamingGetListStockQuotes(Stock request, StreamObserver<StockQuote> responseObserver) {

        System.out.println("serverSideStreamingGetListStockQuotes");
        System.out.println("request: " + request.getTickerSymbol());
        System.out.println("request: " + request.getCompanyName());

        for (int i = 1; i <= 5; i++) {
            // Simulate a delay
            try {
                Thread.sleep(1000);
                StockQuote stockQuote = StockQuote.newBuilder()
                        .setPrice(fetchStockPriceBid(request))
                        .setOfferNumber(i)
                        .setDescription("Price for stock:" + request.getTickerSymbol())
                        .build();
                responseObserver.onNext(stockQuote);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(final StreamObserver<StockQuote> responseObserver) {
        return new StreamObserver<Stock>() {
            int count;
            double price = 0.0;
            StringBuffer sb = new StringBuffer();

            @Override
            public void onNext(Stock stock) {
                count++;
                price = +fetchStockPriceBid(stock);
                sb.append(":")
                        .append(stock.getTickerSymbol());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StockQuote.newBuilder()
                        .setPrice(price / count)
                        .setDescription("Statistics-" + sb.toString())
                        .build());
                logger.info("onCompleted: "+ sb.toString());
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                logger.warn("error:{}", t.getMessage());
            }
        };
    }

    @Override
    public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(final StreamObserver<StockQuote> responseObserver) {
        return new StreamObserver<Stock>() {
            @Override
            public void onNext(Stock request) {

                for (int i = 1; i <= 5; i++) {

                    StockQuote stockQuote = StockQuote.newBuilder()
                            .setPrice(fetchStockPriceBid(request))
                            .setOfferNumber(i)
                            .setDescription("Price for stock:" + request.getTickerSymbol())
                            .build();
                    responseObserver.onNext(stockQuote);
                }
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                logger.warn("error:{}", t.getMessage());
            }
        };
    }
}
