package fight.it.gmall.list.service;

import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.model.list.SearchParam;
import fight.it.gmall.model.list.SearchResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void createGoodsIndex();

    SearchResponseVo list(SearchParam searchParam);

    void hotScore(Long skuId);

}
