package fight.it.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.list.SearchAttr;
import fight.it.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List< BaseAttrInfo> selectAttrInfoList();
    List<SearchAttr> selectSearchAttrList(Long skuId);
}
