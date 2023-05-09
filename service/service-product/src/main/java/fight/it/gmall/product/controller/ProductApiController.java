package fight.it.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.model.list.SearchAttr;
import fight.it.gmall.model.product.BaseCategoryView;
import fight.it.gmall.model.product.BaseTrademark;
import fight.it.gmall.model.product.SkuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;
import fight.it.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    SkuService skuService;
    @Autowired
    BaseCategoryViewService baseCategoryViewService;
    @Autowired
    SpuService spuService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseTrademarkService baseTrademarkService;
    @GetMapping("getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        BigDecimal bigDecimal = new BigDecimal("0");
        bigDecimal= skuService.getPrice(skuId);
        return bigDecimal;
    }
    @GetMapping("getSkuInfoById/{skuId}")
    public  SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId){
     SkuInfo skuInfo= skuService.getSkuInfoById(skuId);
        return skuInfo;
    }
    @GetMapping("getBaseCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView =baseCategoryViewService.getBaseCategoryView(category3Id);
        return baseCategoryView;
    }
    @GetMapping("getSpuSaleAttr/{spuId}/{skuId}")
       public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("spuId") Long spuId ,@PathVariable("skuId") Long skuId ){
        List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrList(spuId,skuId);
        return spuSaleAttrs;
    }
    @GetMapping("getSaleAttrValuesBySpuId/{spuId}")
    public Map<String,Long> getSaleAttrValuesBySpuId(@PathVariable("spuId") Long spuId){

        Map<String,Long>  map= spuService.getSaleAttrValuesBySpuId(spuId);

        return map;
    }
    @GetMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list=  categoryService.getBaseCategoryList();
        return list;
    }

    @GetMapping("deleteSkuId/{skuId}")
    public void deleteSkuId(@PathVariable("skuId")Long skuId){

    }

    @GetMapping("getSearchAttrList/{skuId}")
    public  List<SearchAttr> getSearchAttrList(@PathVariable("skuId")Long skuId){
       List<SearchAttr> searchAttrList= baseAttrInfoService.getSearchAttrList(skuId);
        return searchAttrList;
    }
    @GetMapping("getTrademarkById/{tmId}")
    public BaseTrademark getTrademarkById(@PathVariable("tmId")Long tmId){
        BaseTrademark trademark = baseTrademarkService.getTrademarkById(tmId);
        return trademark;
    }
}
