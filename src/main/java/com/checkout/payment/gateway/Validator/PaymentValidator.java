package com.checkout.payment.gateway.Validator;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Arrays;

public class PaymentValidator {

  private static final List<String> VALID_CURRENCY_CODES = Arrays.asList("USD", "EUR", "GBP");

  public static boolean validatePaymentDetails(PostPaymentRequest request){
    return validateCardNumber(request.getCardNumber()) &&
        validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear()) &&
        validateCurrency(request.getCurrency()) &&
        validateAmount(request.getAmount()) &&
        validateCVV(request.getCvv());
  }

  private static boolean validateCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 14 || cardNumber.length() > 19) {
      System.out.println("Invalid card number length.");
      return false;
    }
    if (!cardNumber.matches("[0-9]+")) {
      System.out.println("Card number must only contain numeric characters.");
      return false;
    }
    return true;
  }

  private static boolean validateExpiryDate(int expiryMonth, int expiryYear) {
    if (expiryMonth < 1 || expiryMonth > 12) {
      System.out.println("Expiry month must be between 1 and 12.");
      return false;
    }
    LocalDate currentDate = LocalDate.now();

    // Create a LocalDate for the expiry month and year (assuming expiry is on the last day of the month)
    YearMonth yearMonth = YearMonth.of(expiryYear, expiryMonth);
    LocalDate expiryDate = yearMonth.atEndOfMonth();

    // Compare expiry date with the current date
    if (expiryDate.isBefore(currentDate)) {
      System.out.println("Expiry date must be in the future.");
      return false;
    }

    return true;
  }


  private static boolean validateCurrency(String currency) {
    if (currency == null || currency.length() != 3) {
      System.out.println("Currency must be exactly 3 characters.");
      return false;
    }
    if (!VALID_CURRENCY_CODES.contains(currency)) {
      System.out.println("Invalid currency code.");
      return false;
    }
    return true;
  }

  private static boolean validateAmount(int amount) {
    if (amount < 0) {
      System.out.println("Amount must be a non-negative integer.");
      return false;
    }
    return true;
  }

  private static boolean validateCVV(String cvv) {
    if (cvv == null || (cvv.length() < 3 || cvv.length() > 4)) {
      System.out.println("CVV must be between 3 and 4 digits.");
      return false;
    }
    if (!cvv.matches("[0-9]+")) {
      System.out.println("CVV must only contain numeric characters.");
      return false;
    }
    return true;
  }
}
