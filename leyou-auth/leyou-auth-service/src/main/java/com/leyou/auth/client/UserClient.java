package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import com.leyou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {

}
