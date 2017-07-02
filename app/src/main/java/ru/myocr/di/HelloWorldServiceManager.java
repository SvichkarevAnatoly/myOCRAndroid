package ru.myocr.di;


public class HelloWorldServiceManager implements HelloWorldService {
    @Override
    public String greet(String userName) {
        return "Hello " + userName + "!";
    }
}
