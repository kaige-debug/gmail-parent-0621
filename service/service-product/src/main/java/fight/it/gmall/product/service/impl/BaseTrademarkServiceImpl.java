package fight.it.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fight.it.gmall.model.product.BaseTrademark;
import fight.it.gmall.product.mapper.BaseTrademarkMapper;

import fight.it.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public List<BaseTrademark> getBaseTrademarkList() {
        List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null);
        return baseTrademarkList;
    }

    @Override
    public List<BaseTrademark> getPageList(long page,long limit) {
        IPage<BaseTrademark> baseTrademarkPage = new Page<BaseTrademark>(page,limit);
        long total = baseTrademarkPage.getTotal();
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkMapper.selectPage(baseTrademarkPage, null);
        List<BaseTrademark> baseTrademarkList = baseTrademarkIPage.getRecords();
        return baseTrademarkList;
    }

    @Override
    public void save(BaseTrademark baseTrademark) {
        Long id = baseTrademark.getId();

        baseTrademarkMapper.insert(baseTrademark);
    }

    @Override
    public void update(BaseTrademark baseTrademark) {

        baseTrademarkMapper.updateById(baseTrademark);
    }

    @Override
    public void removeById(long trademarkId) {
        baseTrademarkMapper.deleteById(trademarkId);
    }

    @Override
    public BaseTrademark getTrademarkById(long trademarkId) {

        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(trademarkId);
        return  baseTrademark;
    }
}
