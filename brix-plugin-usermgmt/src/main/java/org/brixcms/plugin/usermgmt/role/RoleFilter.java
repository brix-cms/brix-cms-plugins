package org.brixcms.plugin.usermgmt.role;

import org.brixcms.plugin.usermgmt.BaseEntityFilter;

/**
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class RoleFilter extends BaseEntityFilter {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
