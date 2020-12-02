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

import java.util.List;


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
        Long spuInfoId = spuInfo.getId();
        spuMapper.insert(spuInfo);
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
}
