package fight.it.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import fight.it.gmall.model.product.BaseSaleAttr;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseSaleAttrMapper  extends BaseMapper<BaseSaleAttr> {
}
