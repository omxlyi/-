package net.eden.serviceinterface.client.userservice;

public interface UserServiceClient {

    /**
     * 根据用户ID向Redis中写入对应的权限信息
     * @param uid
     */
    public void loadPidsByUid(Long uid);
}
