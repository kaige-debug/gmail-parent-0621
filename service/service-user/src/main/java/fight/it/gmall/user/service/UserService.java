package fight.it.gmall.user.service;

import fight.it.gmall.model.user.UserAddress;
import fight.it.gmall.model.user.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserInfo  verify(String token);

    UserInfo login(UserInfo userInfo);

    List<UserAddress> findUserAddressByUserId(String userId);
}
