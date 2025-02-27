package com.checkout.payment.gateway.simulators;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class BankSimulator {
  public String processPayment(PostPaymentRequest payment) {
    String cardNumber = String.valueOf(payment.getCardNumber());
    char lastDigit = cardNumber.charAt(cardNumber.length() - 1);

    if (lastDigit == '0') {
      throw new RuntimeException("503 Service Unavailable");
    }

    if (lastDigit % 2 == 0) {
      return "DECLINED";
    } else {
      return "AUTHORIZED";
    }
  }
}
