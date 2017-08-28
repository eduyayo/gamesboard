package com.pigdroid.social.model;

import java.io.Serializable;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * @author eduyayo@gmail.com
 */
@MappedSuperclass
public abstract class BaseEntity<ID> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6371819762214213305L;

	@Column(name = "creation_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime creationTime;

    @Column(name = "modification_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime modificationTime;

    @Version
    private long version;

    public abstract ID getId();

    public DateTime getCreationTime() {
        return creationTime;
    }

    public DateTime getModificationTime() {
        return modificationTime;
    }

    public long getVersion() {
        return version;
    }

    @PrePersist
    public void prePersist() {
        DateTime now = DateTime.now();
        this.creationTime = now;
        this.modificationTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationTime = DateTime.now();
    }
}


