package fight.it.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import fight.it.gmall.item.service.ItemService;
import fight.it.gmall.model.product.BaseCategoryView;


import fight.it.gmall.model.product.SkuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;
import fight.it.gmall.product.client.ProductFeignClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String, Object> map = new HashMap<>();
        CompletableFuture<SkuInfo> completableFutureSkuInfo =CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo= productFeignClient.getSkuInfoById(skuId);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);
       CompletableFuture<Void> completableFuture1 =CompletableFuture.runAsync(new Runnable() {
           @Override
           public void run() {
               BigDecimal price = productFeignClient.getPrice(skuId);
               map.put("price",price);
           }
       },threadPoolExecutor);
       CompletableFuture<Void> completableFuture2 = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
           @Override
           public void accept(SkuInfo skuInfo) {
               List<SpuSaleAttr>  spuSaleAttrs = productFeignClient.getSpuSaleAttr(skuInfo.getSpuId(),skuId);
               map.put("spuSaleAttrList",spuSaleAttrs);
           }
       },threadPoolExecutor);
        CompletableFuture<Void> completableFuture3 = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView categoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", categoryView);
            }
        },threadPoolExecutor);
        CompletableFuture<Void> completableFuture4 = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Map<String,Long> jsonMap = productFeignClient.getSaleAttrValuesBySpuId(skuInfo.getSpuId());
                String json = JSON.toJSONString(jsonMap);
                map.put("valuesSkuJson",json);
            }
        },threadPoolExecutor);
//        BigDecimal price = productFeignClient.getPrice(skuId);
//        SkuInfo skuInfo= productFeignClient.getSkuInfoById(skuId);
//        BaseCategoryView categoryView =productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
//       // List<SpuSaleAttr>  spuSaleAttr = productFeignClient.getSpuSaleAttr(skuInfo.getSpuId());
//        List<SpuSaleAttr>  spuSaleAttrs = productFeignClient.getSpuSaleAttr(skuInfo.getSpuId(),skuId);
//        Map<String,Long> jsonMap = productFeignClient.getSaleAttrValuesBySpuId(skuInfo.getSpuId());
//        String json = JSON.toJSONString(jsonMap);
//
//
//
//
//        map.put("valuesSkuJson",json);
//        map.put("spuSaleAttrList",spuSaleAttrs);
//        map.put("skuInfo",skuInfo);
//        map.put("price",price);
//        map.put("categoryView",categoryView);
        CompletableFuture.allOf(completableFutureSkuInfo,completableFuture1,completableFuture2,completableFuture3,completableFuture4).join();
        return map;
    }
}
