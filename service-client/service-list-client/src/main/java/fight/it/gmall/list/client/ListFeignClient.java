package fight.it.gmall.list.client;
import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.model.list.SearchParam;
import fight.it.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value="service-list")
public interface ListFeignClient {
    @RequestMapping("api/list/getBaseCategoryList")
    List<JSONObject> getBaseCategoryList();

    @PostMapping("api/list/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId);

    @PostMapping("api/list/onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/list")
    SearchResponseVo list(@RequestBody SearchParam searchParam);

    @RequestMapping("api/list/hotScore/{skuId}")
    void hotScore(@PathVariable("skuId") Long skuId);
}
