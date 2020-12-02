package fight.it.gmall.product.controller;

import fight.it.gmall.common.result.Result;
import fight.it.gmall.product.service.SkuService;
import fight.it.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fight.it.gmall.model.product.SkuInfo;
import fight.it.gmall.model.product.SpuImage;
import fight.it.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuApiController {

    @Autowired
    SkuService skuService;

    @Autowired
    SpuService spuService;

    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){

        skuService.cancelSale(skuId);

        return Result.ok();
    }

    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){

        skuService.onSale(skuId);

        return Result.ok();
    }


    @RequestMapping("list/{pageNo}/{size}")
    public Result skuList(@PathVariable("pageNo") Long pageNo ,@PathVariable("size") Long size){

        IPage<SkuInfo> page = new Page<>();

        page.setSize(size);
        page.setCurrent(pageNo);

        IPage<SkuInfo> skuInfoIPage =  skuService.skuList(page);
        return Result.ok(skuInfoIPage);
    }


    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuService.saveSkuInfo(skuInfo);

        return Result.ok();
    }


    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttrs =  spuService.spuSaleAttrList(spuId);

        return Result.ok(spuSaleAttrs);
    }


    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        List<SpuImage> spuImages =  spuService.spuImageList(spuId);

        return Result.ok(spuImages);
    }

}
