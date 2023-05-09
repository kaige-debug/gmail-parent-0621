package fight.it.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import fight.it.gmall.model.product.SkuInfo;

import java.math.BigDecimal;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> skuList(IPage<SkuInfo> page);
    void cancelSale(Long skuId);

    void onSale(Long skuId);

    BigDecimal getPrice(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);
}
