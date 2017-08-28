package com.pigdroid.hub.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import com.pigdroid.hub.model.persistent.Message;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class MessageDAO extends BaseDAO<Message>{

}
