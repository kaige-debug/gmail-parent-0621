package fight.it.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.product.BaseTrademark;
import fight.it.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product/baseTrademark")
@CrossOrigin
public class BaseTradeMarkApiController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable("page") long page,
                              @PathVariable("limit") long limit){
//         Page<BaseTrademark> baseTrademarkPage = new Page<>(page,limit);
//        List<BaseTrademark> baseTrademarkList = baseTrademarkPage.getRecords();
//        long total = baseTrademarkPage.getTotal();
        List<BaseTrademark> baseTrademarkList= baseTrademarkService.getPageList(page,limit);
//        List<BaseTrademark> baseTrademarkList = baseTrademarkPage.getRecords();
//        int total = baseTrademarkList.size();
        return Result.ok(baseTrademarkList);
    }
    @GetMapping("/getBaseTrademark")
    public Result getBaseTrademark(){

      List<BaseTrademark> baseTrademarkList=  baseTrademarkService.getBaseTrademarkList();

        return Result.ok(baseTrademarkList);
    }
    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    @PutMapping("update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.update(baseTrademark);
        return Result.ok();
    }
    @DeleteMapping("remove/{trademarkId}")
    public Result removeById(@PathVariable long trademarkId){
        baseTrademarkService.removeById(trademarkId);
        return Result.ok();
    }
    @GetMapping("get/{trademarkId}")
    public Result getTrademarkById(@PathVariable long trademarkId){
        BaseTrademark trademark = baseTrademarkService.getTrademarkById(trademarkId);
        return Result.ok(trademark);
    }
}
