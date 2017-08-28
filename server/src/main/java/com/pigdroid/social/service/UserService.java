package com.pigdroid.social.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pigdroid.hub.web.rest.dto.UserClientProfile;
import com.pigdroid.hub.web.rest.dto.UserForm;
import com.pigdroid.social.dto.RegistrationForm;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.service.exception.DuplicateEmailException;

/**
 * @author eduyayo@gmail.com
 */
@Service
public interface UserService {

    /**
     * Creates a new user account to the service.
     * @param userAccountData   The information of the created user account.
     * @return  The information of the created user account.
     * @throws DuplicateEmailException Thrown when the email address is found from the database.
     */
    User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException;

    User login(UserForm userForm);

    List<User> search(String email, String sessionEmail);

	User addContact(String email, String sessionEmail);

	List<UserClientProfile> listContacts(String sessionEmail);

	List<String> getDeviceTokens(List<String> toEmails);

	User findByEmail(String email);

}
