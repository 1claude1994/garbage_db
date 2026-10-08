package com.garbage.collector;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class WebCollector {

    private final HttpClient httpClient;

    public WebCollector() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String fetch(String url) {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("User-Agent", "Mozilla/5.0")
                        .build();

        try {

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "웹 페이지 요청 실패. status="
                                + response.statusCode()
                );
            }

            return response.body();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "웹 페이지 요청이 중단되었습니다.",
                    e
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "웹 페이지 요청 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}