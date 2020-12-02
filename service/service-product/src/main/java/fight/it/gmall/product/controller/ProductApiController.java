package fight.it.gmall.product.controller;

import fight.it.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    SkuService skuService;

    @RequestMapping("getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId){

        BigDecimal bigDecimal = new BigDecimal("0");

        return bigDecimal;

    }
}
