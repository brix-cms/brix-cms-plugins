package org.brixcms.plugin.usermgmt;

import java.io.Serializable;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class BaseEntityFilter implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
