package fight.it.gmall.pay.service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;
import fight.it.gmall.pay.config.AliPayConfig;
import fight.it.gmall.pay.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService{

   @Autowired
    PaymentMapper paymentMapper;
   @Autowired
    AlipayClient alipayClient;
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentMapper.insert(paymentInfo);
    }

    @Override
    public String alipaySubmit(OrderInfo orderInfoById) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(AliPayConfig.return_payment_url);
        request.setNotifyUrl(AliPayConfig.notify_payment_url);

        Map<String ,Object> map = new HashMap();

        map.put("out_trade_no",orderInfoById.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);
        map.put("subject",orderInfoById.getOrderDetailList().get(0).getSkuName());

        request.setBizContent(JSON.toJSONString(map));

        AlipayTradePagePayResponse response =null;

        try{
            response= alipayClient.pageExecute(request);
        }catch(AlipayApiException e){
            e.printStackTrace();
        }

        String submitForm = response.getBody();
        return submitForm;
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());

        paymentMapper.update(paymentInfo,queryWrapper);

    }

    @Override
    public Map<String, Object> query(String out_trade_no) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",out_trade_no);

        AlipayTradeQueryResponse response = null;
        request.setBizContent(JSON.toJSONString(map));
        try{
            response = alipayClient.execute(request);
        }catch(AlipayApiException e){
             e.printStackTrace();
        }

        boolean success = response.isSuccess();
        if(success){
            System.out.println("调用成功");
            map.put("trade_status",response.getTradeStatus());
            map.put("trade_no",response.getTradeNo());
            map.put("success",true);
        }else{
            System.out.println("调用失败");
            map.put("success",false);
        }
        return map;
    }
    @Override
    public Map<String, Object> checkPayment(String out_trade_no) {



        return query(out_trade_no);
    }
}
