package fight.it.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.model.list.SearchAttr;
import fight.it.gmall.model.product.BaseAttrInfo;
import fight.it.gmall.model.product.BaseAttrValue;
import fight.it.gmall.product.mapper.BaseAttrInfoMapper;
import fight.it.gmall.product.mapper.baseAttrValueMapper;
import fight.it.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private baseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseAttrInfo> attrInfoList(@PathVariable long category3Id) {
        QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",category3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectList(queryWrapper);
        return baseAttrInfoList;
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(queryWrapper);
        return baseAttrValueList;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long id = baseAttrInfo.getId();
        if(null==id || id<=0) {
            //添加属性
           baseAttrInfoMapper.insert(baseAttrInfo);
            Long attr_id = baseAttrInfo.getId();
            id = attr_id;
         }else{
            //更改属性值
            baseAttrInfoMapper.updateById(baseAttrInfo);
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id" , id );
            baseAttrValueMapper.delete(queryWrapper);
        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {

            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }

    }

    @Override
    public List<SearchAttr> getSearchAttrList(Long skuId) {
        List<SearchAttr> searchAttrList=  baseAttrInfoMapper.selectSearchAttrList(skuId);
        return searchAttrList;
    }
}
