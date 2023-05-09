package fight.it.gmall.pay.controller;

import com.alibaba.fastjson.JSON;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.enums.PaymentStatus;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;
import fight.it.gmall.mq.service.RabbitService;
import fight.it.gmall.order.client.OrderFeignClient;
import fight.it.gmall.pay.service.PaymentService;
import org.apache.commons.lang.StringUtils;
import org.redisson.misc.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/payment")
public class PaymentApiController {

    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    PaymentService paymentService;

    @Autowired
    RabbitService rabbitService;

    @RequestMapping("alipay/query/{out_trade_no}")
    public Result query(@PathVariable("out_trade_no") String out_trade_no){
        Map<String,Object> map = paymentService.query(out_trade_no);
        return Result.ok(map);
    }

    @RequestMapping("alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") Long orderId){
        OrderInfo orderInfoById = orderFeignClient.getOrderInfoById(orderId);
        String form =  paymentService.alipaySubmit(orderInfoById);

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOrderId(orderInfoById.getId());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        paymentInfo.setOutTradeNo(orderInfoById.getOutTradeNo());
        paymentInfo.setPaymentType("在线支付");
        paymentInfo.setTotalAmount(orderInfoById.getTotalAmount());
        paymentInfo.setSubject(orderInfoById.getOrderDetailList().get(0).toString());
        paymentInfo.setCreateTime(new Date());

        paymentService.savePaymentInfo(paymentInfo);

        Map<Object, Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfoById.getOutTradeNo());
        map.put("count",5);
        //发送请求到order服务
        rabbitService.SendDeadMessage("exchange.delayed","routing.delayed", JSON.toJSONString(map),10l, TimeUnit.SECONDS);

        return form;
    }
    @RequestMapping("alipay/callback/return")
    public String callbackReturn(HttpServletRequest request) {
        String tradeNo = request.getParameter("trade_no");
        String outTradeNo = request.getParameter("out_trade_no");
        String callbackContent = request.getQueryString();

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.toString());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(callbackContent);
        paymentInfo.setTradeNo(tradeNo);

        String status = "未支付";
        if(!StringUtils.isEmpty("PAID")){
            paymentService.updatePayment(paymentInfo);
        }

        return "<form action=\"http://payment.gmall.com/paySuccess.html\">\n" +
                "</form>\n" +
                "<script>\n" +
                "document.forms[0].submit();\n" +
                "</script>";
    }
    @RequestMapping("alipay/callback/notify")
    public String callbackNotify(){
        return null;
    }
}
