package com.getxinfo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body)
            throws IOException {
        logger.info("===========================request begin================================================");

        logger.info("URI : " + request.getURI());
        logger.info("Method : " + request.getMethod());
        logger.info("Request Body : " + new String(body, "UTF-8"));
        logger.info("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        logger.info("============================response begin==========================================");
        logger.info("status code: " + response.getStatusCode());
        logger.info("status text: " + response.getStatusText());
        logger.info("content type: " + response.getHeaders().getContentType());
        /*long len = response.getHeaders().getContentLength();
        if (len != -1) {*/
            StringBuilder inputStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getBody(), "UTF-8"));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
            logger.info("Response Body : " + inputStringBuilder.toString());
        //}
        logger.info("=======================response end=================================================");
    }

}