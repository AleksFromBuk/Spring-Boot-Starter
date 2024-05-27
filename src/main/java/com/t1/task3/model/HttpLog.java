package com.t1.task3.model;

import lombok.Data;

import java.util.Map;

@Data
public class HttpLog {
    private String method;
    private String uriEndpoint;
    private int statusCode;
    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private long executionTime;

    @Override
    public String toString() {
        return "\n=========================================\n" +
                "Тип запроса: " + method + "\n" +
                "URI эндпоинта: " + uriEndpoint + "\n" +
                "Статус: " + statusCode + "\n" +
                "Заголовки запроса: " + requestHeaders + "\n" +
                "Заголовки ответа: " + responseHeaders + "\n" +
                "Время выполнения запроса: " + executionTime + " мс" + "\n" +
                "=========================================";
    }
}
