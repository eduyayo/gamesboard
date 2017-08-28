package com.pigdroid.social.repository;

import com.pigdroid.social.model.user.User;

/**
 * @author eduyayo@gmail.com
 */
public interface UserRepositoryCustom {

    public User searchUserByEmailWithCollections(String email);
    
}
