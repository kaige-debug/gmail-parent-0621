package fight.it.gmall.item.service;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

public interface ItemService {
    Map<String, Object> getItem(@PathVariable("skuId") Long skuId);
}
