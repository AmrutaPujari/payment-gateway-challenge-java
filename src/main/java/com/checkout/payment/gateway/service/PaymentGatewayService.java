package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.checkout.payment.gateway.simulators.BankSimulator;

import static com.checkout.payment.gateway.Validator.PaymentValidator.validatePaymentDetails;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankSimulator bankSimulator;
  private final PostPaymentResponse response;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankSimulator bankSimulator,
      PostPaymentResponse response) {
    this.paymentsRepository = paymentsRepository;
    this.bankSimulator = bankSimulator;
    this.response = response;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    // Validate the payment details
    if (validatePaymentDetails(paymentRequest)) {
      LOG.info("Payment details are valid.");
      String status = bankSimulator.processPayment(paymentRequest);
      response.setStatus(PaymentStatus.valueOf(status));
      response.setId(UUID.fromString(UUID.randomUUID().toString()));
      response.setCardNumberLastFour(paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4));
      response.setAmount(paymentRequest.getAmount());
      response.setCurrency(paymentRequest.getCurrency());
      response.setExpiryMonth(paymentRequest.getExpiryMonth());
      response.setExpiryYear(paymentRequest.getExpiryYear());
      paymentsRepository.add(response);
      return response;
      } else

        throw new RuntimeException("Invalid payment request");
  }

    }




