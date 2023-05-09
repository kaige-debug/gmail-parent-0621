package fight.it.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fight.it.gmall.common.constant.RedisConst;
import fight.it.gmall.common.util.MD5;
import fight.it.gmall.model.user.UserAddress;
import fight.it.gmall.model.user.UserInfo;
import fight.it.gmall.user.mapper.UserAddressMapper;
import fight.it.gmall.user.mapper.UserInfoMapper;
import fight.it.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;
    @Override
    public UserInfo verify(String token) {

       UserInfo userInfo = (UserInfo)redisTemplate.opsForValue().get("user:login:"+token);
       return userInfo;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("login_name",userInfo.getLoginName());
        queryWrapper.eq("passwd", userInfo.getPasswd());
        userInfo = userInfoMapper.selectOne(queryWrapper);

        if(null!=userInfo){
            String token= UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX+token,userInfo);
            userInfo.setToken(token);
        }else{
           return null;
        }

        return  userInfo;
    }

    @Override
    public List<UserAddress> findUserAddressByUserId(String userId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_id",userId);
        List<UserAddress> userAddresses = userAddressMapper.selectList(queryWrapper);

        return userAddresses;
    }
}
