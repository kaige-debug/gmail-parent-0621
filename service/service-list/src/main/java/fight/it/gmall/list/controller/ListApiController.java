package fight.it.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.list.service.ListService;
import fight.it.gmall.model.list.SearchParam;
import fight.it.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/list")
public class ListApiController {
    @Autowired
    ListService listService;
    @RequestMapping("getBaseCategoryList")
    public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list= listService.getBaseCategoryList();
        return list;
    }
    @PostMapping("cancelSale/{skuId}")
    public void cancelSale(@PathVariable("skuId") Long skuId){
        listService.cancelSale(skuId);
    }

    @PostMapping("onSale/{skuId}")
    public void onSale(@PathVariable("skuId") Long skuId){
        listService.onSale(skuId);
    }

    @RequestMapping("list")
    public SearchResponseVo list(@RequestBody SearchParam searchParam)  {
        SearchResponseVo searchResponseVo = listService.list(searchParam);
        return searchResponseVo;
    }
    @RequestMapping("hotScore/{skuId}")
    public void hotScore(@PathVariable("skuId") Long skuId){
        listService.hotScore(skuId);
    }
}
