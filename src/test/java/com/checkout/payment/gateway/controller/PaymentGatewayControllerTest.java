package com.checkout.payment.gateway.controller;


import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.checkout.payment.gateway.simulators.BankSimulator;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;


  @org.junit.jupiter.api.Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(String.valueOf(4321));

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @org.junit.jupiter.api.Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @org.junit.jupiter.api.Test
  public void testProcessPaymentValid() {
    PaymentsRepository storage = new PaymentsRepository();
    BankSimulator simulator = new BankSimulator();
    PostPaymentResponse response = new PostPaymentResponse();
    PaymentGatewayService gateway = new PaymentGatewayService(storage, simulator, response);

    PostPaymentRequest payment = new PostPaymentRequest();
    payment.setCardNumber("2222405343248879");
    payment.setCvv(String.valueOf(123));
    payment.setExpiryMonth(04);
    payment.setExpiryYear(2026);
    payment.setAmount(100);
    payment.setCurrency("USD");

    PostPaymentResponse processedPayment = gateway.processPayment(payment);

    assertEquals("AUTHORIZED", processedPayment.getStatus().name());
  }

  @org.junit.jupiter.api.Test
  public void testProcessPaymentInvalidCurrency() {
    PaymentsRepository storage = new PaymentsRepository();
    BankSimulator simulator = new BankSimulator();
    PostPaymentResponse response = new PostPaymentResponse();
    PaymentGatewayService gateway = new PaymentGatewayService(storage, simulator, response);

    PostPaymentRequest payment = new PostPaymentRequest();
    payment.setCardNumber("2222405343248870");
    payment.setCvv(String.valueOf(123));
    payment.setExpiryMonth(04);
    payment.setExpiryYear(2026);
    payment.setAmount(100);
    payment.setCurrency("AUD");
//    PostPaymentResponse processedPayment = gateway.processPayment(payment);

    assertThrows(RuntimeException.class, () -> gateway.processPayment(payment));


  }
  @org.junit.jupiter.api.Test
  public void testProcessPaymentInvalidCard1() {
    // Arrange: Set up the required objects for the test
    PaymentsRepository storage = new PaymentsRepository();
    BankSimulator simulator = new BankSimulator();
    PostPaymentResponse response = new PostPaymentResponse();
    PaymentGatewayService gateway = new PaymentGatewayService(storage, simulator, response);

    // Act: Create a payment request with an invalid card number
    PostPaymentRequest payment = new PostPaymentRequest();
    payment.setCardNumber("2222405343248870");  // Invalid card number for simulation
    payment.setCvv("123");
    payment.setExpiryMonth(04);
    payment.setExpiryYear(2026);
    payment.setAmount(100);
    payment.setCurrency("EUR");


    // Assert: Ensure the payment is declined
    assertThrows(RuntimeException.class, () -> gateway.processPayment(payment));
  }
}
