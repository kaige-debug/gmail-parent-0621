package fight.it.gmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fight.it.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
