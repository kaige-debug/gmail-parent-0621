package fight.it.gmall.product.service;

import fight.it.gmall.model.product.BaseAttrInfo;
import fight.it.gmall.model.product.BaseAttrValue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface BaseAttrInfoService {
    List<BaseAttrInfo> attrInfoList(long category3Id);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
