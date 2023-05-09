package fight.it.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fight.it.gmall.model.product.SpuImage;
import fight.it.gmall.model.product.SpuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;
import fight.it.gmall.model.product.SpuSaleAttrValue;
import fight.it.gmall.product.mapper.SpuImageMapper;
import fight.it.gmall.product.mapper.SpuMapper;
import fight.it.gmall.product.mapper.SpuSaleAttrMapper;
import fight.it.gmall.product.mapper.SpuSaleAttrValueMapper;
import fight.it.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SpuServiceImpl implements SpuService {
  @Autowired
  private SpuMapper spuMapper;
  @Autowired
  private SpuSaleAttrMapper spuSaleAttrMapper;
  @Autowired
  private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
  @Autowired
  private SpuImageMapper spuImageMapper;
    @Override
    public IPage<SpuInfo> getSpuList(long pageNum, long pageSize, String category3Id) {
        IPage<SpuInfo> spuInfoIPage = new Page<>(pageNum,pageSize);
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> spuInfoIPage1 = spuMapper.selectPage(spuInfoIPage, queryWrapper);

        return spuInfoIPage1;
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        spuMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId();
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(null!=spuImageList){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfoId);
                spuImageMapper.insert(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (null!=spuSaleAttrList) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
               
                spuSaleAttr.setSpuId(spuInfoId);
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(null!=spuSaleAttrValueList){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfoId);
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> queryWrapperSaleAttr = new QueryWrapper<>();
        queryWrapperSaleAttr.eq("spu_id",spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(queryWrapperSaleAttr);

        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {

            QueryWrapper<SpuSaleAttrValue> queryWrapperSaleAttrValue = new QueryWrapper<>();
            queryWrapperSaleAttrValue.eq("spu_id",spuSaleAttr.getSpuId());
            queryWrapperSaleAttrValue.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(queryWrapperSaleAttrValue);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrs;
    }



    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId,Long skuId) {
        List<SpuSaleAttr>  SpuSaleAttrList = spuSaleAttrMapper.selectSaleAttrListBySpu(spuId,skuId);
        return SpuSaleAttrList;
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("spu_id",spuId);

        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);

        return spuImages;
    }

    @Override
    public Map<String,Long> getSaleAttrValuesBySpuId(Long spuId) {
        Map<String,Long> jsonMap =new HashMap<>();
       List<Map> mapList= spuSaleAttrMapper.selectSaleAttrValuesBySpuId(spuId);
        for (Map map : mapList) {
            String k = (String)map.get("value_Ids");
            Long v =(Long) map.get("sku_id");
            jsonMap.put(k,v);
        }
       
       return jsonMap;
    }

}
