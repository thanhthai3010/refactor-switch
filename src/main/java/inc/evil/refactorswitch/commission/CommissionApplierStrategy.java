package inc.evil.refactorswitch.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;

public interface CommissionApplierStrategy {

  double applyCommission(Client client, Product product);

  CardType getCardType();
}
