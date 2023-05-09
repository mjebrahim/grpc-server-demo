package com.example.grpcserver;

import com.example.HelloRequest;
import com.example.HelloResponse;
import com.example.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase{

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting =
                "Hello, " + request.getName() + "!";

        HelloResponse response = HelloResponse.newBuilder().setMessage(greeting).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
