package com.pigdroid.social.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.persistent.Usr;
import com.pigdroid.hub.repository.UsrRepository;
import com.pigdroid.hub.service.MessageService;
import com.pigdroid.hub.web.rest.dto.UserClientProfile;
import com.pigdroid.hub.web.rest.dto.UserForm;
import com.pigdroid.social.dto.RegistrationForm;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.repository.UserRepository;
import com.pigdroid.social.service.UserService;
import com.pigdroid.social.service.exception.DuplicateEmailException;

/**
 * @author eduyayo@gmail.com
 */
@Service
public class RepositoryUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUserService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsrRepository usrRepository;

	@Autowired
	MessageService messageService;

    public RepositoryUserService() {
    }

    @Transactional
    @Override
    public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException {
        LOGGER.debug("Registering new user account with information: {}", userAccountData);

        if (emailExist(userAccountData.getEmail())) {
            LOGGER.debug("Email: {} exists. Throwing exception.", userAccountData.getEmail());
            throw new DuplicateEmailException("The email address: " + userAccountData.getEmail() + " is already in use.");
        }

        LOGGER.debug("Email: {} does not exist. Continuing registration.", userAccountData.getEmail());

        String encodedPassword = encodePassword(userAccountData);

        User.Builder user = User.getBuilder()
                .email(userAccountData.getEmail())
                .firstName(userAccountData.getFirstName())
                .lastName(userAccountData.getLastName())
                .password(encodedPassword);

        if (userAccountData.isSocialSignIn()) {
            user.signInProvider(userAccountData.getSignInProvider());
        }

        User registered = user.build();

        //eduyayo
        Usr usr = new Usr();
        registered.setUsr(usr);
        usr.setUser(registered);

        LOGGER.debug("Persisting new user with information: {}", registered);

        return userRepository.save(registered);
    }

    private boolean emailExist(String email) {
        LOGGER.debug("Checking if email {} is already found from the database.", email);

        User user = userRepository.findByEmail(email);

        if (user != null) {
            LOGGER.debug("User account: {} found with email: {}. Returning true.", user, email);
            return true;
        }

        LOGGER.debug("No user account found with email: {}. Returning false.", email);

        return false;
    }

    private String encodePassword(RegistrationForm dto) {
        String encodedPassword = null;
        if (dto.isNormalRegistration()) {
            LOGGER.debug("Registration is normal registration. Encoding password.");
            encodedPassword = passwordEncoder.encode(dto.getPassword());
        }
        return encodedPassword;
    }

	@Override
	public User login(UserForm userForm) {
		User user = null;
		if (!StringUtils.isEmpty(userForm.getDeviceToken())) {
			user = userRepository.findByEmail(userForm.getEmail());
			String lastToken = user.getUsr().getDeviceToken();
			if (!userForm.getDeviceToken().equals(lastToken)) {
				HubMessage newMsg =
						HubMessage.builder().to(user.getEmail())
						.type(HubMessage.TYPE_MSG_DISCONNECT).build();
				messageService.sendMessage(newMsg);
			}
			user.getUsr().setDeviceToken(userForm.getDeviceToken());
			usrRepository.save(user.getUsr());
		}
		return user;
	}

	@Override
	public List<User> search(String email, String sessionEmail) {
		Pageable pageRequest = Pageable.ofSize(10);
		User session = userRepository.searchUserByEmailWithCollections(sessionEmail);
		Set<Usr> notIn = new HashSet<Usr>(session.getUsr().getContacts());
		notIn.add(session.getUsr());
		Page<User> page = null;
		if (notIn == null || notIn.isEmpty()) {
			page = userRepository.findByEmailLike(email + "%", pageRequest);
		} else {
			page = userRepository.findByEmailLikeAndUsrIsNotIn(email + "%", notIn.isEmpty() ? null : notIn, pageRequest);
		}
		return page.getContent();
	}

	@Override
	@Transactional
	public User addContact(String email, String sessionEmail) {
		User session = userRepository.findByEmail(sessionEmail);
		User contact = userRepository.findByEmail(email);
		Usr sessionUsr = session.getUsr();
		sessionUsr.getContacts().add(contact.getUsr());
		usrRepository.save(sessionUsr);
		ObjectMapper mapper = new ObjectMapper();
		HubMessage newMsg;
		try {
			newMsg = new HubMessage(
					HubMessage.TYPE_ADD_CONTACT,
					mapper.writeValueAsString(
							UserClientProfile.builder().reverseAccepted(true)
							.accepted(contact.getUsr().getInverseContacts()
									.contains(sessionUsr)).from(session).build()), sessionEmail, email);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		messageService.sendMessage(newMsg);
		return contact;
	}

	@Override
	@Transactional
	public List<UserClientProfile> listContacts(String sessionEmail) {
		User session = userRepository.findByEmail(sessionEmail);
		List<UserClientProfile> ret = new ArrayList<UserClientProfile>();
		Set<Usr> inverse = session.getUsr().getInverseContacts();
		Set<Usr> alreadyThere = new HashSet<Usr>();
		for (Usr c : session.getUsr().getContacts()) {
			ret.add(UserClientProfile.builder().from(c.getUser()).accepted(true).reverseAccepted(inverse.contains(c)).build());
			alreadyThere.add(c);
		}
		for (Usr c : inverse) {
			if (!alreadyThere.contains(c)) {
				ret.add(UserClientProfile.builder().from(c.getUser()).accepted(false).reverseAccepted(true).build());
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public List<String> getDeviceTokens(List<String> toEmails) {
		List<String> ret = null;
		List <User> users = userRepository.findByEmailIn(toEmails);
		ret = new ArrayList<String>(users.size());
		for (User user : users) {
			ret.add(user.getUsr().getDeviceToken());
		}
		return ret;
	}

	@Override
	public User findByEmail(String from) {
		return userRepository.findByEmail(from);
	}

}
