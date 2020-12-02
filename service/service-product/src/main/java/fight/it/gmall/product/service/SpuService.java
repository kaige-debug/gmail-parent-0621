package fight.it.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.model.product.SpuImage;
import fight.it.gmall.model.product.SpuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;

import java.util.List;

public interface SpuService {
    IPage<SpuInfo> getSpuList(long pageNum, long pageSize, String category3Id);

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    List<SpuImage> spuImageList(Long spuId);
}
