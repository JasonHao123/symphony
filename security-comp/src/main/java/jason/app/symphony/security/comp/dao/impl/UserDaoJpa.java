package jason.app.symphony.security.comp.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import jason.app.symphony.security.comp.dao.UserDao;
import jason.app.symphony.security.comp.entity.User;

@Repository
public class UserDaoJpa implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public User findByUsername(String username) {
        Query query = entityManager.createQuery("select u from User u where u.username=:username");
        query.setParameter("username", username);
        return (User) query.getSingleResult();
    }

	@Override
	public User save(User user) {
		entityManager.persist(user);
		return user;
	}

}
