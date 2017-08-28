package com.pigdroid.social.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pigdroid.hub.model.persistent.Usr;
import com.pigdroid.social.model.user.User;

/**
 * @author eduyayo@gmail.com
 */
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    public User findByEmail(String email);

    Page<User> findByEmailLike(String email, Pageable pageable);
    Page<User> findByEmailLikeAndUsrIsNotIn(String email, Set<Usr> notIn, Pageable pageable);

	public List<User> findByEmailIn(List<String> toEmails);
    
}
