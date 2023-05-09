package fight.it.gmall.product.service.impl;
import fight.it.gmall.config.GmallCache;
import fight.it.gmall.common.constant.RedisConst;
import fight.it.gmall.list.client.ListFeignClient;
import fight.it.gmall.model.product.*;
import fight.it.gmall.product.mapper.SkuAttrValueMapper;
import fight.it.gmall.product.mapper.SkuImageMapper;
import fight.it.gmall.product.mapper.SkuInfoMapper;
import fight.it.gmall.product.mapper.SkuSaleAttrValueMapper;
import fight.it.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ListFeignClient listFeignClient;


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
        listFeignClient.cancelSale(skuId);
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
        listFeignClient.onSale(skuId);
    }

    @Override
    public BigDecimal getPrice(Long skuId) {

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        return skuInfo.getPrice();
    }

    @GmallCache
    @Override
    public SkuInfo  getSkuInfoById(Long skuId){
       SkuInfo skuInfo = getSkuInfoByIdFromDB(skuId);
         //SkuInfo skuInfo = null;
         //解释RedisConst.SKUKEY_PREFIX=“sku：”
        //解释RedisConst.SKUKEY_SUFFIX=“：Info”
        //访问缓存，查看缓存中是否有值 得到的结果赋值给skuInfo
//        skuInfo = (SkuInfo) redisTemplate.opsForValue().get(RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX);
//        if(null==skuInfo){
//
//            String key = UUID.randomUUID().toString();
//            Boolean ok = redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":lock", key, 2, TimeUnit.SECONDS);
//
//            if(ok){
//                //直接访问数据库
//                skuInfo =getSkuInfoByIdFromDB(skuId);
//
//                if (null!=skuInfo) {
//                    //同步到缓存
//                    redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX, skuInfo);
//                    //释放锁
//                    String OpenKey = (String) redisTemplate.opsForValue().get(("sku:" + skuId + ":lock"));
//                    if (OpenKey.equals(key)) {
//                        redisTemplate.delete("sku:" + skuId + ":lock");
//                    }
//                }else{
//                    // 同步空缓存
//                    redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX, skuInfo,5,TimeUnit.SECONDS);
//                }
//                System.out.println("归还分布式锁");
//            }else{//若没有拿到锁，则回旋
//                try {
//                    Thread.sleep(1000);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//                 return getSkuInfoByIdFromDB(skuId);
//            }
//        }
     
        return skuInfo;
    }
    //查询方法
    private SkuInfo getSkuInfoByIdFromDB(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }
}
