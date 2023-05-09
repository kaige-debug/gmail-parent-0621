package fight.it.gmall.product.service.impl;

import fight.it.gmall.model.product.BaseCategoryView;
import fight.it.gmall.product.mapper.BaseCategoryViewMapper;
import fight.it.gmall.product.service.BaseCategoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseCategoryViewServiceImpl implements BaseCategoryViewService {
    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public BaseCategoryView getBaseCategoryView(Long category3Id) {
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectById(category3Id);
        return baseCategoryView;
    }
}
