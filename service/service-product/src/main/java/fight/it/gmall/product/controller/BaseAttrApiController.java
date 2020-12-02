package fight.it.gmall.product.controller;


import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.product.BaseAttrInfo;
import fight.it.gmall.model.product.BaseAttrValue;
import fight.it.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("admin/product/")
public class BaseAttrApiController {
     @Autowired
    private BaseAttrInfoService baseAttrInfoService;

     @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
      public Result attrInfoList(@PathVariable long category3Id){
       List<BaseAttrInfo> BaseAttrInfoList = baseAttrInfoService.attrInfoList(category3Id);
       return Result.ok(BaseAttrInfoList);
     }
     @GetMapping("getAttrValueList/{attrId}")
    public  Result getAttrValueList(@PathVariable String attrId){
      List<BaseAttrValue> baseAttrValueList =   baseAttrInfoService.getAttrValueList(attrId);
      return Result.ok(baseAttrValueList);
     }
     @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
         baseAttrInfoService.saveAttrInfo(baseAttrInfo);
         return Result.ok();
     }
}
