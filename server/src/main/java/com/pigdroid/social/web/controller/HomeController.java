//package com.pigdroid.social.web.controller;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.social.connect.ConnectionRepository;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.pigdroid.social.service.ContactService;
//
///**
// * @author eduyayo@gmail.com
// */
//@Controller
//public class HomeController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
//
//    protected static final String VIEW_NAME_HOMEPAGE = "index";
//
//    @Autowired
//    private ConnectionRepository connectionRepository;
//
//    @Autowired
//    private ContactService contactService;
//
//    @RequestMapping(value="/", method = RequestMethod.GET)
//    public String showHomePage(Model model) {
////    	Connection<Facebook> facebookConnection = connectionRepository.findPrimaryConnection(Facebook.class);
////    	Connection<Twitter> twitterConnection = connectionRepository.findPrimaryConnection(Twitter.class);
////    	if (facebookConnection != null) {
////    		model.addAttribute("connection", facebookConnection);
////    		model.addAttribute("friends", facebookConnection.getApi().friendOperations().getFriends());
////    	} else {
////    		model.addAttribute("connection", twitterConnection);
////    	}
//    	contactService.list();
//        LOGGER.debug("Rendering homepage.");
//        return VIEW_NAME_HOMEPAGE;
//    }
//}
