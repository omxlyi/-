package net.eden.gatewayservice.util;


import javax.annotation.PostConstruct;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import net.eden.serviceinterface.client.userservice.UserServiceClient;


@Component
public class GatewayAssistant {
    
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @DubboReference
    private UserServiceClient userServiceClient;


    public static RedisTemplate<String,Object> redis;
    public static UserServiceClient userService;

    private static final String KEY = "Priv";

    /**
     * 为了让静态方法调用注入进来的组件
     */
    @PostConstruct
    public void init(){
        redis = this.redisTemplate;
        userService = this.userServiceClient;
    }

    /**
     * 找到权限对应的id
     * Hash:
     *  Key:"Priv"
     *  hk: path + method
     *  hv: pid
     * @param hk
     * @return pid
     */
    public static String getpid(String hk){

        if(redis.opsForHash().hasKey(KEY, hk)){
            return redis.opsForHash().get(KEY, hk).toString();
        }else{
            return null;
        }
    }

    // public static long getpidTest(String hk){

    //     if(redis.opsForHash().hasKey(KEY, hk)){
    //         return (long)redis.opsForHash().get(KEY, hk);
    //     }else{
    //         return -1l;
    //     }
    // }

    /**
     * 如果redis中没有 uid:pids，重新载入
     * @param uid
     */
    public static void loadPidsIfAbsent(Long uid){

        String key = "u_" + uid;
        if(!redis.hasKey(key)){
            userService.loadPidsByUid(uid);
        }
    }

    /**
     * 检查用户是否有对应的权限
     * @param uid
     * @param pid
     * @return
     */
    public static Boolean checkPermissons(Long uid,String pid){

        String key = "u_" + uid;
        if(redis.opsForSet().isMember(key, Integer.parseInt(pid))){
            return true;
        }else{
            return false;
        }
    }
}
