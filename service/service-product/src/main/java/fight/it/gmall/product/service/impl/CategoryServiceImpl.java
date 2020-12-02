package fight.it.gmall.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.model.product.BaseCategory1;
import fight.it.gmall.model.product.BaseCategory2;
import fight.it.gmall.model.product.BaseCategory3;
import fight.it.gmall.product.mapper.BaseCategory1Mapper;
import fight.it.gmall.product.mapper.BaseCategory2Mapper;
import fight.it.gmall.product.mapper.BaseCategory3Mapper;
import fight.it.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
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
}
