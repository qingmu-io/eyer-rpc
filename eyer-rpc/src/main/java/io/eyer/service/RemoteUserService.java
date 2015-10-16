package io.eyer.service;

/**
 * Created by Administrator on 2015/8/18.
 */
public class RemoteUserService implements  UserService {


    @Override
    public User save(User user) {
        return user;
    }
}
