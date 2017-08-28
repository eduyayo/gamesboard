package com.pigdroid.hub.model.persistent;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public interface PersistentResourceInterface extends Serializable {

	String getId();
	
	void setId(String uuid);

	Date getTimestamp();

	void setTimestamp(Date timestamp);

}
