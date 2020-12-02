package fight.it.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.model.product.SkuInfo;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> skuList(IPage<SkuInfo> page);
    void cancelSale(Long skuId);

    void onSale(Long skuId);
}
