package inc.evil.refactorswitch.commission;

import inc.evil.refactorswitch.domain.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionApplierStrategyResolver {
  private final List<CommissionApplierStrategy> commissionApplierStrategies;

  public CommissionApplierStrategy getCommissionApplier(CardType cardType) {
    return commissionApplierStrategies.stream()
        .filter(strategy -> strategy.getCardType().equals(cardType))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No commission applier strategy found for card type " + cardType));
  }
}
