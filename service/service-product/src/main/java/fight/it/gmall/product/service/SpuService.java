package fight.it.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.model.product.SpuImage;
import fight.it.gmall.model.product.SpuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;

import java.util.List;
import java.util.Map;

public interface SpuService {
    IPage<SpuInfo> getSpuList(long pageNum, long pageSize, String category3Id);

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId,Long skuId);

    List<SpuImage> spuImageList(Long spuId);

    Map<String,Long> getSaleAttrValuesBySpuId(Long spuId);


}
