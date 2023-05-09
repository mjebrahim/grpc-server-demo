package com.example.grpcserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);


        Server server = ServerBuilder
                .forPort(8080)
                .addService(new StockService())
                .addService(new HelloServiceImpl()).build();
        try {
            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
