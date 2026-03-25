package com.medicalstore.inventory.service.impl;

import com.medicalstore.inventory.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final RestTemplate restTemplate;

    @Value("${sms.enabled:false}")
    private boolean enabled;

    @Value("${sms.api.url:}")
    private String apiUrl;

    @Value("${sms.api.key:}")
    private String apiKey;

    @Value("${sms.sender.id:FGHSTR}")
    private String senderId;

    public SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendSms(String phone, String message) {
        if (phone == null || phone.trim().isEmpty()) {
            log.warn("SMS not sent: Phone number is empty.");
            return;
        }

        if (!enabled || apiUrl.isEmpty() || apiKey.isEmpty()) {
            log.info("[SMS MOCK] To: {}, Message: {}", phone, message);
            return;
        }

        try {
            // This is a generic implementation using Query Parameters (commonly used by SMS gateways)
            // Example for Fast2SMS or similar: url?authorization=apiKey&route=q&message=msg&numbers=num
            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("authorization", apiKey)
                    .queryParam("message", message)
                    .queryParam("language", "english")
                    .queryParam("route", "q")
                    .queryParam("numbers", phone)
                    .queryParam("flash", "0")
                    .build().toUriString();

            log.info("Sending SMS to {} via {}", phone, apiUrl);
            // In a real scenario, you might need to handle specific JSON response formats
            Object responseObj = restTemplate.getForObject(url, String.class);
            String response = responseObj != null ? responseObj.toString() : "No Response";
            log.info("SMS Gateway Response: {}", response);
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phone, e.getMessage());
        }
    }
}
