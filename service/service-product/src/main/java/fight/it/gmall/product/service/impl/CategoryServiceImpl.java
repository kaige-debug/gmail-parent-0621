package fight.it.gmall.product.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.model.product.BaseCategory1;
import fight.it.gmall.model.product.BaseCategory2;
import fight.it.gmall.model.product.BaseCategory3;
import fight.it.gmall.model.product.BaseCategoryView;
import fight.it.gmall.product.mapper.BaseCategory1Mapper;
import fight.it.gmall.product.mapper.BaseCategory2Mapper;
import fight.it.gmall.product.mapper.BaseCategory3Mapper;
import fight.it.gmall.product.mapper.BaseCategoryViewMapper;
import fight.it.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> category1List = baseCategory1Mapper.selectList(null);

        return category1List;
    }

    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(queryWrapper);
        return baseCategory2List;
    }

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {

        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",category2Id);
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(queryWrapper);
        return baseCategory3List;
    }

    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
          //将categoryViews转化为JSONObject
         List<JSONObject> list = new ArrayList<>();
        Map<Long, List<BaseCategoryView>> categroy1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        for(Map.Entry<Long,List<BaseCategoryView>> category1Object : categroy1Map.entrySet()){
            Long category1Id = category1Object.getKey();
            String category1Name = category1Object.getValue().get(0).getCategory1Name();

            JSONObject category1Json = new JSONObject();
            category1Json.put("categoryId",category1Id);
            category1Json.put("categoryName",category1Name);

            //二级分类
            List<JSONObject> category2list= new ArrayList<>();
            List<BaseCategoryView> category2Views = category1Object.getValue();
            Map<Long, List<BaseCategoryView>> category2Map = category2Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));

            for(Map.Entry<Long,List<BaseCategoryView>> category2Object : category2Map.entrySet()){
                Long category2Id = category2Object.getKey();
                String category2Name = category2Object.getValue().get(0).getCategory2Name();

                JSONObject category2Json = new JSONObject();
                category2Json.put("categoryId",category2Id);
                category2Json.put("categoryName",category2Name);

                //三级分类
                 List<JSONObject> category3list = new ArrayList<>();
                List<BaseCategoryView> category3Views = category2Object.getValue();
                Map<Long, List<BaseCategoryView>> category3Map = category3Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for(Map.Entry<Long,List<BaseCategoryView>> category3Object : category3Map.entrySet()){
                    Long category3Id = category3Object.getKey();
                    String category3Name = category3Object.getValue().get(0).getCategory3Name();

                    JSONObject category3Json = new JSONObject();
                    category3Json.put("categoryId",category3Id);
                    category3Json.put("categoryName",category3Name);

                    category3list.add(category3Json);
                }
                category2Json.put("categoryChild",category3list);
                category2list.add(category2Json);

            }
            category1Json.put("categoryChild",category2list);
            list.add(category1Json);
        }

        return list;
    }
}
