package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.commission.CommissionApplierStrategyResolver;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import inc.evil.refactorswitch.domain.ProductStatus;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class PaymentServiceImpl implements PaymentService {
  private static final double REWARDS_COMMISSION = 0.016;
  private static final double REWARDS_DISCOUNT = 0.007;
  private static final double SECURED_COMMISSION = 0.0155;
  private static final double DEFAULT_COMMISSION = 0.015;
  private static final double LOW_INTEREST_COMMISSION = 0.0165;
  private static final double CASHBACK_COMMISSION = 0.016;
  private static final double CASHBACK_DISCOUNT = 0.006;
  private static final double STUDENT_SCHOOL_COMMISSION = 0.008;
  private static final double STUDENT_UNIVERSITY_COMMISSION = 0.012;
  private static final double TRAVEL_COMMISSION = 0.014;
  private static final double BUSINESS_COMMISSION = 0.017;
  private static final double BUSINESS_TAX = 0.003;
  private final ProductRepository productRepository;
  private final PaymentClient paymentClient;

  private final CommissionApplierStrategyResolver commissionApplierResolver;

  @Override
  public void payForProduct(Long productId, Client client) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new ProductNotFoundException(
                        String.format("Product with id %s not found", productId)));
    double updatedPrice =
        commissionApplierResolver
            .getCommissionApplier(client.getCard().getType())
            .applyCommission(client, product);

    PaymentResponse paymentResponse = paymentClient.debitCard(client.getCard(), updatedPrice);
    if (paymentResponse.isSuccess()) {
      product.setStatus(ProductStatus.PAID);
      // process delivery, etc
      log.info("Product {} paid with success", productId);
    } else {
      // alert the user & other business logic
      log.warn("Product {} payment failed with [{}]", productId, paymentResponse.getErrorMessage());
      throw new ProductPaymentException(paymentResponse.getErrorMessage());
    }
  }
}
