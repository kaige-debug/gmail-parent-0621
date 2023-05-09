package fight.it.gmall.all.controller;

import fight.it.gmall.model.list.SearchAttr;
import fight.it.gmall.model.list.SearchParam;
import com.alibaba.fastjson.JSONObject;
import fight.it.gmall.list.client.ListFeignClient;
import fight.it.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {
    @Autowired
    ListFeignClient listFeignClient;
    @RequestMapping("/")
    public String index(Model model){
        List<JSONObject> list = listFeignClient.getBaseCategoryList();
         model.addAttribute("list",list);
         return "index/index";
    }
    @RequestMapping({"list.html","search.html"})
    public String list(Model model, SearchParam searchParam, HttpServletRequest request){
        //获取当前url
        String urlParam = getUrlParam(searchParam, request);

        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);
        if(null!=searchResponseVo.getGoodsList()&&searchResponseVo.getGoodsList().size()>0){
            model.addAttribute("urlParam",urlParam);
            model.addAttribute("attrsList",searchResponseVo.getAttrsList());
            model.addAttribute("goodsList",searchResponseVo.getGoodsList());
            model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        }
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }
        if(null!=searchParam.getProps()&&searchParam.getProps().length>0){
            List<SearchAttr> searchAttrs = new ArrayList<>();
            for (String prop : searchParam.getProps()) {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrs);
        }

        if(!StringUtils.isEmpty(searchParam.getOrder())){
            Map<String,String> orderMap = new HashMap<>();
            orderMap.put("type",searchParam.getOrder().split(":")[0]);
            orderMap.put("sort",searchParam.getOrder().split(":")[1]);

            model.addAttribute("orderMap",orderMap);
        }

        return "list/index";
    }

    private String getUrlParam(SearchParam searchParam, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        StringBuffer urlParam = new StringBuffer(requestURI);
        String trademark = searchParam.getTrademark();
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();

        if(!StringUtils.isEmpty(keyword)){
            urlParam.append("?keyword="+keyword);
        }
        if(null!=category3Id&&category3Id>0){
            urlParam.append("?category3Id="+category3Id);
        }
        if (null!=props&&props.length>0) {
            for (String prop : props) {
                urlParam.append("&props="+prop);
            }
        }
        if(!StringUtils.isEmpty(trademark)){
            urlParam.append("&trademark="+trademark);
        }
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            urlParam.append("&order="+searchParam.getOrder());
        }


        return urlParam.toString();
    }
}
