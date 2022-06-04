package net.eden.userservice.service.imp;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import net.eden.serviceinterface.client.userservice.UserServiceClient;
import net.eden.userservice.dao.UserDao;

@DubboService
public class UserServiceClientImp implements UserServiceClient{

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void loadPidsByUid(Long uid) {
        userDao.loadUserPids(uid);
    }
    
}
