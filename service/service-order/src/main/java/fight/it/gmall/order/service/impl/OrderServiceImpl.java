package fight.it.gmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.model.enums.OrderStatus;
import fight.it.gmall.model.enums.ProcessStatus;
import fight.it.gmall.model.order.OrderDetail;
import fight.it.gmall.model.order.OrderInfo;
import fight.it.gmall.model.payment.PaymentInfo;
import fight.it.gmall.order.mapper.OrderDetailMapper;
import fight.it.gmall.order.mapper.OrderMapper;
import fight.it.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public String getTrade(String userId) {
        String tradeNo = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("user:"+ userId + ":tradeCode",tradeNo);

        return tradeNo;
    }

    @Override
    public String submitOrder(OrderInfo order) {
        //设置保存订单数据
        order.setProcessStatus(ProcessStatus.UNPAID.getComment());//未支付
        order.setOrderStatus(ProcessStatus.UNPAID.getComment());//未支付
        //设置日期  直接调用jvm中的类方法 Calendar.getInstance()
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.add(1,Calendar.DATE);
        order.setExpireTime(instance.getTime());
        order.setCreateTime(date);
        //外部订单号
        String outTradeNo = "admin";
        outTradeNo+= System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        outTradeNo+= simpleDateFormat.format(date);

        order.setOutTradeNo(outTradeNo);
        order.setOrderComment("fightIt");
        order.setTotalAmount(getTotalAmount(order.getOrderDetailList()));
        orderMapper.insert(order);
        //设置订单详情Id
        Long orderId = order.getId();
        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insert(orderDetail);
        }
        return orderId +"";
    }
    //得到订单总金额
    private BigDecimal getTotalAmount(List<OrderDetail> orderDetailList) {

        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            totalAmount = totalAmount.add(orderPrice);
        }
        return totalAmount;
    }
     //验证是否为已提交订单 核对提交页面号
    @Override
    public Boolean checkout(String userId,String tradeNo) {

        boolean b = false;
        String tradeNoFromCache =(String) redisTemplate.opsForValue().get("user:" + userId + ":tradeCode");
        if(!StringUtils.isEmpty(tradeNoFromCache)&&tradeNoFromCache.equals(tradeNo)){
            redisTemplate.delete("user:" + userId + ":tradeCode");
            b = true;
        }
        return b;
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {

        OrderInfo orderInfo = orderMapper.selectById(orderId);

        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(queryWrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    @Override
    public Long updateOrderPay(PaymentInfo paymentInfo) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        orderInfo.setTradeBody(paymentInfo.getCallbackContent());

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());
        orderMapper.update(orderInfo,queryWrapper);

        OrderInfo orderInfoFromDb = orderMapper.selectOne(queryWrapper);

        if(null!=orderInfoFromDb){
            return orderInfoFromDb.getId();
        }else {
            return null;
        }
    }
}
