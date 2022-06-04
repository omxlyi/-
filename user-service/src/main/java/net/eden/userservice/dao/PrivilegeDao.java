package net.eden.userservice.dao;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import net.eden.userservice.domain.Privilege;
import net.eden.userservice.repository.PrivilegeRepository;

@Repository
public class PrivilegeDao implements InitializingBean{

    private final String KEY = "Priv";

    @Resource(name = "redisTemplate")
    private HashOperations<String,String,Long> hashOps;

    @Autowired
    PrivilegeRepository privilegeRepository;

    /**
     * 将所有权限信息放到redis中,用于根据用户请求信息找到对应权限id
     * Hash：
     *     Key:Priv
     *     HK:path-method
     *     HV:pid
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        Iterable<Privilege> allPri = privilegeRepository.findAll();

        for(Privilege priv : allPri){
            hashOps.putIfAbsent(KEY, priv.getKey(), priv.getId());
        }

    }


    
}
