package com.avlija.furniture.service;

public class DefaultHelloService implements HelloService {

    @Override
    public void hello() {
       System.out.println("Hello from Hello Service");
    }
}