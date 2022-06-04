package net.eden.userservice.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import net.eden.userservice.dao.UserDao;
import net.eden.userservice.domain.Privilege;
import net.eden.userservice.domain.Role;
import net.eden.userservice.domain.User;
import net.eden.userservice.domain.Privilege.Type;
import net.eden.userservice.repository.PrivilegeRepository;
import net.eden.userservice.repository.RoleRepository;
import net.eden.userservice.repository.UserRepository;
import net.eden.userservice.util.JwtAssistant;



@RestController
// @RequestMapping("user-service")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserDao userDao;

    @GetMapping("loaduserpids/{uid}")
    public void loadUserPids(@PathVariable Long uid){
        userDao.loadUserPids(uid);
        String key = "u_" + uid;
        Set<Object> pids = redisTemplate.opsForSet().members(key);
        StringBuilder ret = new StringBuilder();
        for(Object pid:pids){
            ret.append(pid.toString() + "-");
        }
        System.out.println(ret.toString());
        return;
    }

    @GetMapping("showPrivs")
    public String show(){
        Map<Object,Object> map = new HashMap<>();
        map = redisTemplate.opsForHash().entries("Priv");
        StringBuilder ret = new StringBuilder();
        for(Entry<Object,Object> tmp:map.entrySet()){
            ret.append(tmp.getKey().toString() + ":" +tmp.getValue().toString());
        }
        return ret.toString();
    }

    @GetMapping("/admin")
    public String admin(){
        return "I'm administrator.";
    }
    @GetMapping("/user")
    public String user(){
        return "I'm user.";
    }

    @GetMapping("/login/{uid}")
    public String login(@PathVariable Long uid){
        return JwtAssistant.initToken(uid, 16);
    }

    @GetMapping("test")
    public String test(){
        return redisTemplate.opsForHash().get("Priv", "/admin-GET").toString();
    }

    @GetMapping("addpriv")
    public void addpriv(){
        Privilege priv1 = new Privilege();
        priv1.setId(1l);
        priv1.setUrl("/admin");
        priv1.setType(Type.GET.toString());

        Privilege priv2 = new Privilege();
        priv2.setId(2l);
        priv2.setUrl("/user");
        priv2.setType(Type.GET.toString());

        privilegeRepository.save(priv1);
        privilegeRepository.save(priv2);
    }

    @GetMapping("addrole")
    public void addrole() {
        Role role1 = new Role();
        role1.setName("管理员");
        role1.setId(1l);

        Role role2 = new Role();
        role2.setName("用户");
        role2.setId(2l);

        roleRepository.save(role1);
        roleRepository.save(role2);
        
    }

    @GetMapping("adduser")
    public void adduser(){
        User user1 = new User();
        user1.setId(1l);
        user1.setUsername("admin");
        user1.setPassword("admin");

        User user2 = new User();
        user2.setId(2l);
        user2.setUsername("user");
        user2.setPassword("user");

        userRepository.save(user1);
        userRepository.save(user2);

    }

    @GetMapping("addroleforuser")
    public void addRoleForUser(){
        User user1 = userRepository.findById(1l).get();
        Role role1 = roleRepository.findById(1l).get();
        user1.addRole(role1);
        userRepository.save(user1);

        User user2 = userRepository.findById(2l).get();
        Role role2 = roleRepository.findById(2l).get();
        user2.addRole(role2);
        userRepository.save(user2);
    }

    @GetMapping("addprivforrole")
    public void addPrivForRole(){
        Role role1 = roleRepository.findById(1l).get();
        List<Privilege> privs1 = role1.getPrivs();
        privs1.add(privilegeRepository.findById(1l).get());
        role1.setPrivs(privs1);
        roleRepository.save(role1);

        Role role2 = roleRepository.findById(2l).get();
        List<Privilege> privs2 = role2.getPrivs();
        privs2.add(privilegeRepository.findById(2l).get());
        role2.setPrivs(privs2);
        roleRepository.save(role2);
    }


    @GetMapping("loadpidsbyuid")
    public String load(){
        List<Long> pids = new LinkedList<>();

        List<Role> roles = userRepository.findById(1l).get().getRoles();
        for(Role role:roles){
            List<Privilege> privs = role.getPrivs();
            for(Privilege priv:privs){
                pids.add(priv.getId());
            }
        }
        // redisTemplate.opsForSet().add("u_1", pids);
        return pids.toString();
    }
}
