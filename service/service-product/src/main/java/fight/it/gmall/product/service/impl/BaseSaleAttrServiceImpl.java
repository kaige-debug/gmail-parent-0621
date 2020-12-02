package fight.it.gmall.product.service.impl;

import fight.it.gmall.model.product.BaseSaleAttr;
import fight.it.gmall.product.mapper.BaseSaleAttrMapper;
import fight.it.gmall.product.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseSaleAttrServiceImpl implements BaseSaleAttrService {
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrList;
    }


}
