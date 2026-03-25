package com.medicalstore.inventory.service;

public interface SmsService {
    /**
     * Sends an SMS message to a specific phone number.
     * @param phone The recipient's phone number.
     * @param message The message content.
     */
    void sendSms(String phone, String message);
}
