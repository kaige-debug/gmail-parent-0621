package fight.it.gmall.item.service.impl;

import fight.it.gmall.item.service.ItemService;
import fight.it.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String, Object> map = new HashMap<>();
        BigDecimal price = productFeignClient.getPrice(skuId);
        map.put("price",price);
        return map;
    }
}
