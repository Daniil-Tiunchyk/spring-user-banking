package com.example.spring_user_banking.service;

import com.example.spring_user_banking.model.Greeting;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GreetingService {
    private final AtomicLong counter = new AtomicLong();

    public Greeting greet(String name) {
        return new Greeting(counter.incrementAndGet(), String.format("Hello, %s!", name));
    }
}
