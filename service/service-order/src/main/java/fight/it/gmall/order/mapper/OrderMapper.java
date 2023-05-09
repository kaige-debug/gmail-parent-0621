package fight.it.gmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
