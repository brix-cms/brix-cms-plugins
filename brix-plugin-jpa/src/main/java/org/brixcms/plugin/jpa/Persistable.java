package org.brixcms.plugin.jpa;

import java.io.Serializable;

/**
 * Simple interface for entities.
 * 
 * @param <ID>
 *            the type of the identifier
 * 
 * @author dan.simko@gmail.com
 */
public interface Persistable<ID extends Serializable> extends Serializable {

    /**
     * Returns the id of the entity.
     * 
     * @return the id
     */
    ID getId();

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     * 
     * @return if the object is new
     */
    boolean isNew();

}
