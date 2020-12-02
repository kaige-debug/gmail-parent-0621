package fight.it.gmall.product.service;


import fight.it.gmall.model.product.BaseTrademark;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BaseTrademarkService {
    List<BaseTrademark> getBaseTrademarkList();

    List<BaseTrademark> getPageList( long page, long limit);

    void save(BaseTrademark baseTrademark);

    void update(BaseTrademark baseTrademark);

    void removeById(long trademarkId);

    BaseTrademark getTrademarkById(long trademarkId);
}
