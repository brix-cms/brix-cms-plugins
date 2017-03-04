package org.brixcms.plugin.usermgmt.role;

import javax.persistence.Entity;

import org.brixcms.plugin.usermgmt.BaseEntity;

/**
 * @author dan.simko@gmail.com
 */
@Entity
@SuppressWarnings("serial")
public class Role extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
