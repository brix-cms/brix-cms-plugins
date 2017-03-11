package org.brixcms.plugin.jpa.web.admin;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.plugin.jpa.Persistable;

/**
 * Generic detachable model for JPA entities.
 * 
 * @author dan.simko@gmail.com
 */
@SuppressWarnings("serial")
public class EntityModel<T extends Persistable<ID>, ID extends Serializable> implements IModel<T> {

    @SpringBean
    private EntityManager em;

    private ID id;
    private Class<T> type;

    private transient T entity;

    public EntityModel(Class<T> clazz, ID id) {
        Injector.get().inject(this);
        this.type = clazz;
        this.id = id;
    }

    public EntityModel(T entity) {
        Injector.get().inject(this);
        setObject(entity);
    }

    @Override
    public T getObject() {
        if (entity == null && id != null) {
            entity = em.find(type, id);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void setObject(T other) {
        if (other == null) {
            id = null;
            entity = null;
        } else {
            type = (Class<T>) other.getClass();
            id = other.getId();
            entity = other;
        }
    }

    @Override
    public void detach() {
        if (entity != null && !entity.isNew()) {
            entity = null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        EntityModel<T, ID> other = (EntityModel<T, ID>) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}