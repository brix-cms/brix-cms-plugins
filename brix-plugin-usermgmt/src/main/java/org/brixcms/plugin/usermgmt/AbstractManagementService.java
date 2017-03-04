package org.brixcms.plugin.usermgmt;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dan.simko@gmail.com
 */
public abstract class AbstractManagementService<T extends BaseEntity> implements ManagementService<T> {

    protected final EntityManager em;

    public AbstractManagementService(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void clone(List<T> entities) {
        for (T user : entities) {
            em.detach(user);
            user.setId(null);
            em.persist(user);
        }
    }

    @Override
    @Transactional
    public void delete(List<T> entity) {
        getRepository().delete(entity);
    }

    @Override
    @Transactional
    public void save(T entity) {
        getRepository().save(entity);
    }

    protected abstract CrudRepository<T, Long> getRepository();
}
