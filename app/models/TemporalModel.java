package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * http://www.playframework.org/community/snippets/5
 */
@MappedSuperclass
public abstract class TemporalModel extends Model {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Date updated;

    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }
}
