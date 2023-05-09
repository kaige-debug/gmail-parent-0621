package fight.it.gmall.cart.service;

import fight.it.gmall.model.cart.CartInfo;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CartService {

    void addCart(CartInfo cartInfo);

    List<CartInfo> cartList(CartInfo cartInfo);

}
