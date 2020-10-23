package com.cepheid.cloud.skel.model;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(value = {View.UserView.Internal.class})
    @Column(name = "id")
    protected Long mId;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @JsonView(value = {View.UserView.External.class})
    @Column(length = 36, name = "guid", columnDefinition = "varchar")
    protected UUID mGuid;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy' 'HH:mm:ss", shape = STRING)
    @JsonView(value = {View.UserView.External.class})
    @Column(updatable = false, name = "created_date")
    private Timestamp mCreatedDate;

    @UpdateTimestamp
    @JsonView(value = {View.UserView.Internal.class})
    @JsonFormat(pattern = "dd-MM-yyyy' 'HH:mm:ss", shape = STRING)
    @Column(name = "last_modified_date")
    private Timestamp mLastModifiedDate;


    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public UUID getGuid() {
        return mGuid;
    }

    public void setGuid(UUID guid) {
        this.mGuid = guid;
    }

    public Timestamp getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(Timestamp createdDate) {
        this.mCreatedDate = createdDate;
    }

    public Timestamp getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.mLastModifiedDate = lastModifiedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mId == null) ? 0 : mId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) obj;
        if (mId == null) {
            return other.mId == null;
        } else {
            return mId.equals(other.mId);
        }
    }


    @PrePersist
    public void generateUUID() {
        if (mGuid == null) {
            mGuid = UUID.randomUUID();
        }

    }


}
