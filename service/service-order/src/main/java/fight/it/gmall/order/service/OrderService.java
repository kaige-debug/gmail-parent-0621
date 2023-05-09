package fight.it.gmall.order.service;

import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;

public interface OrderService{
    String getTrade(String userId);

    String submitOrder(OrderInfo order);

    Boolean checkout(String userId, String tradeNo);

    OrderInfo getOrderInfoById(Long orderId);

    Long updateOrderPay(PaymentInfo paymentInfo);
}
