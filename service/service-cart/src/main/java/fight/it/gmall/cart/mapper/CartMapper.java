package fight.it.gmall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.cart.CartInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper  extends BaseMapper<CartInfo> {
}
