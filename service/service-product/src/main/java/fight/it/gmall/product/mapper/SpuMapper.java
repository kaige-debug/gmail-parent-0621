package fight.it.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.product.SpuInfo;
import fight.it.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpuMapper extends BaseMapper<SpuInfo> {
    List<Map> selectSaleAttrValuesBySpuId(Long spuId);
}
