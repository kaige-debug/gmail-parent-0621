package fight.it.gmall.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.payment.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper  extends BaseMapper<PaymentInfo> {
}
