package net.eden.userservice.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import net.eden.userservice.domain.Privilege;
import net.eden.userservice.domain.Role;
import net.eden.userservice.repository.RoleRepository;

@Repository
public class RoleDao {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private SetOperations<String,Long> setOps;


    /**
     * 检查 rid 是否存在，存在则返回对应角色，否则返回null
     * @param rid
     * @return Role
     */
    public Role getRole(Long rid){

        Optional<Role> role = roleRepository.findById(rid);
        if(role.isPresent()){
            return role.get();
        }else{
            System.out.println(String.format("{}对应的角色不存在", rid));
            return null;
        }
    }

    /**
     * 根据角色ID查找该角色所拥有的权限ID列表
     */
    public List<Long> getPrivIds(Long rid){
        
        Role role = getRole(rid);
        if(role == null) return null;

        List<Privilege> privs = role.getPrivs();
        List<Long> pids = new ArrayList<>(privs.size());
        for(Privilege priv:privs){
            pids.add(priv.getId());
        }
        return pids;
    }

    /**
     * 将一个角色的所有权限id缓存到redis中
     */
    public void loadPrivs(Long rid){

        List<Long> pids = getPrivIds(rid);
        if(pids == null) return;

        String key = "r_" + rid;
        for(Long pid:pids){
            setOps.add(key, pid);
        }
        redisTemplate.expire(key, 3600l, TimeUnit.SECONDS);
        
    }

}
