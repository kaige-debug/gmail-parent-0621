package fight.it.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.product.BaseSaleAttr;
import fight.it.gmall.model.product.BaseTrademark;
import fight.it.gmall.model.product.SpuInfo;
import fight.it.gmall.product.service.BaseSaleAttrService;
import fight.it.gmall.product.service.BaseTrademarkService;
import fight.it.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuApiController {
    @Autowired
    private SpuService spuService;

    @Autowired
    private BaseSaleAttrService baseSaleAttrService;
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("{pageNum}/{pageSize}")
    public Result getSpuList(@PathVariable long pageNum,@PathVariable long pageSize,String category3Id){
        IPage<SpuInfo> spuInfoPage = spuService.getSpuList(pageNum,pageSize,category3Id);
        return Result.ok(spuInfoPage);
    }

    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){
       List<BaseSaleAttr> baseSaleAttrList=  baseSaleAttrService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarkList = baseTrademarkService.getBaseTrademarkList();
        return Result.ok(baseTrademarkList);
    }
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }
}
