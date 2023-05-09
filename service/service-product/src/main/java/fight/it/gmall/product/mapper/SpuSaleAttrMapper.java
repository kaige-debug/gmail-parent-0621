package fight.it.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSaleAttrListBySpu(@Param("spuId") Long spuId, @Param("skuId") Long skuId);

    List<Map> selectSaleAttrValuesBySpuId(Long spuId);
}
