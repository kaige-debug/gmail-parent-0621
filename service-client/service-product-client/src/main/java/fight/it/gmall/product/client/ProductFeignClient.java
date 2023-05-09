package fight.it.gmall.product.client;
import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.model.list.SearchAttr;
import fight.it.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "service-product")
public interface ProductFeignClient {
    @GetMapping("api/product/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("api/product/getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId);

    @GetMapping("api/product/getSpuSaleAttr/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("spuId") Long spuId,@PathVariable("skuId") Long skuId);

    @GetMapping("api/product/getBaseCategoryView/{category3Id}")
    BaseCategoryView  getBaseCategoryView(@PathVariable("category3Id") Long category3Id);

    @GetMapping("api/product/getSaleAttrValuesBySpuId/{spuId}")
    Map<String,Long> getSaleAttrValuesBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("api/product/getBaseCategoryList")
    List<JSONObject> getBaseCategoryList();

    @PostMapping("api/product/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId);

    @PostMapping("api/product/onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId);

    @GetMapping("api/product/getSearchAttrList/{skuId}")
    List<SearchAttr> getSearchAttrList(@PathVariable("skuId")Long skuId);

    @GetMapping("api/product/getTrademarkById/{tmId}")
    BaseTrademark getTrademarkById(@PathVariable("tmId")Long tmId);
}
