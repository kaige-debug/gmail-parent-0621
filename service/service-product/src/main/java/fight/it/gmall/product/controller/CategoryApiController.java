package fight.it.gmall.product.controller;

import fight.it.gmall.common.result.Result;
import fight.it.gmall.model.product.BaseCategory1;
import fight.it.gmall.model.product.BaseCategory2;
import fight.it.gmall.model.product.BaseCategory3;
import fight.it.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class CategoryApiController{
    @Autowired
    private CategoryService categoryService;
    @GetMapping("getCategory1")
    public Result getCategory1(){
       List<BaseCategory1> category1List = categoryService.getCategory1();
       return Result.ok(category1List);
    }
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable String category1Id){
      List<BaseCategory2> baseCategory2List = categoryService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable String category2Id){
      List<BaseCategory3> baseCategory3List =  categoryService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

}
