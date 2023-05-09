package fight.it.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.cart.mapper.CartMapper;
import fight.it.gmall.cart.service.CartService;
import fight.it.gmall.common.constant.RedisConst;
import fight.it.gmall.model.cart.CartInfo;
import fight.it.gmall.model.product.SkuInfo;
import fight.it.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public void addCart(CartInfo cartInfo) {
        String userId = cartInfo.getUserId();
        Long skuId = cartInfo.getSkuId();
        Integer skuNum = cartInfo.getSkuNum();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("sku_id",skuId);
        queryWrapper.eq("user_id",userId);
        CartInfo cartInfoFromDB = cartMapper.selectOne(queryWrapper);
        if(null==cartInfoFromDB){
            SkuInfo skuInfoById = productFeignClient.getSkuInfoById(skuId);
            cartInfo.setCartPrice(skuInfoById.getPrice().multiply(new BigDecimal(skuNum)));
            cartInfo.setImgUrl(skuInfoById.getSkuDefaultImg());
            cartInfo.setUserId("1");
            cartInfo.setIsChecked(1);
            cartInfo.setSkuName(skuInfoById.getSkuName());
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
           // 购物车保存时，没有skuPrice字段，因为一致性差，skuproce字段只能从sku表中查询
            cartMapper.insert(cartInfo);
        }else{
           cartInfo= cartInfoFromDB;
           cartInfo.setSkuNum(cartInfoFromDB.getSkuNum()+skuNum);
           cartMapper.update(cartInfo,queryWrapper);
        }
        //同步数据据
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,cartInfo.getSkuId()+"",cartInfo);


    }

    /**
     *
     * @param cartInfo
     * @return
     */
    @Override
    public List<CartInfo> cartList(CartInfo cartInfo) {

        //先查询数据库
        List<CartInfo> cartInfoList = (List<CartInfo>)redisTemplate.opsForHash().values("user:"+ cartInfo.getUserId() + ":cart");
        if(null== cartInfoList ||cartInfoList.size()<=0){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",cartInfo.getUserId());
         cartInfoList = cartMapper.selectList(queryWrapper);

           if(null!=cartInfoList || cartInfoList.size()>0){
               Map<String,Object> map  = new HashMap<>();
               for (CartInfo info : cartInfoList) {
                  map.put(info.getSkuId()+"",info);
               }
              //把所有的购物车列表放进缓存 用putAll()方法
               redisTemplate.opsForHash().putAll("user:"+ cartInfo.getUserId() + ":cart",map);
           }
        }
        if(null != cartInfoList && cartInfoList.size() > 0){

            for (CartInfo info : cartInfoList) {
                Long skuId = info.getSkuId();
                BigDecimal price = productFeignClient.getPrice(skuId);
                info.setSkuPrice(price);
            }
        }
        return cartInfoList;
    }
}
