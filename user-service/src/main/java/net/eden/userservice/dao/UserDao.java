package net.eden.userservice.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import net.eden.userservice.domain.Role;
import net.eden.userservice.domain.User;
import net.eden.userservice.repository.UserRepository;

@Repository
public class UserDao {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RoleDao roleDao;



    /**
     * 检查 uid 是否存在，存在则返回对应用户，否则返回null
     * @param uid
     * @return User
     */
    public User getUser(Long uid){

        Optional<User> user = userRepository.findById(uid);
        if(user.isPresent()){
            return user.get();
        }else{
            System.out.println(String.format("{}对应的用户不存在", uid));
            return null;
        }
    }



    /**
     * 根据用户ID，获得该用户拥有的角色列表
     * @param uid
     * @return
     */
    public List<Long> getRoleIds(Long uid){

        User user = getUser(uid);
        if(user == null) return null;

        List<Role> roles = user.getRoles();
        List<Long> rids = new ArrayList<>(roles.size());
        for(Role role:roles){
            rids.add(role.getId());
        }
        return rids;
    }



    /**
     * 计算用户的权限，load到redis中
     * @param uid
     */
    public void loadUserPids(Long uid){

        List<Long> rids = getRoleIds(uid);
        if(rids == null) return;

        String key = "u_" +uid;
        
        Set<String> roleKeys = new HashSet<>(rids.size());

        for(Long rid:rids){
            String roleKey = "r_" + rid;
            roleKeys.add(roleKey);
            if(!redisTemplate.hasKey(roleKey)){
                roleDao.loadPrivs(rid);
            }
        }
        redisTemplate.opsForSet().unionAndStore(roleKeys, key);
        redisTemplate.expire(key, 60*60*10l, TimeUnit.SECONDS);

    }
}
