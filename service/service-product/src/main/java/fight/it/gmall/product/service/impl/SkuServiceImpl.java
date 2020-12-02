package fight.it.gmall.product.service.impl;

import fight.it.gmall.model.product.*;
import fight.it.gmall.product.mapper.SkuAttrValueMapper;
import fight.it.gmall.product.mapper.SkuImageMapper;
import fight.it.gmall.product.mapper.SkuInfoMapper;
import fight.it.gmall.product.mapper.SkuSaleAttrValueMapper;
import fight.it.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;


    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        skuInfoMapper.insert(skuInfo);

        Long sku_id = skuInfo.getId();

        List<SkuImage> skuImages = skuInfo.getSkuImageList();
        if(null!=skuImages){
            for (SkuImage skuImage : skuImages) {
                skuImage.setSkuId(sku_id);
                skuImageMapper.insert(skuImage);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(null!=skuSaleAttrValueList){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(sku_id);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(null!=skuAttrValueList){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(sku_id);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

    }

    @Override
    public IPage<SkuInfo> skuList(IPage<SkuInfo> page) {
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        page.setSize(50);
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(page,null);

        return skuInfoIPage;
    }

    @Override
    public void cancelSale(Long skuId) {
        // mysql 下架

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(0);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);

        // 清理nosql
        System.out.println("同步搜索引擎");
    }

    @Override
    public void onSale(Long skuId) {
        // mysql 上架

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(1);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);

        // 写入nosql
        System.out.println("同步搜索引擎");

    }
}
