package com.avlija.furniture.service;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
  public String getGreeting(String name) {
    return "Hello " + name;
  }
}