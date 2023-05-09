package fight.it.gmall.pay.service;

import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    String alipaySubmit(OrderInfo orderInfoById);

    void updatePayment(PaymentInfo paymentInfo);

    Map<String, Object> query(String out_trade_no);

    Map<String, Object> checkPayment(String out_trade_no);
}
