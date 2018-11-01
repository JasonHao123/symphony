package jason.app.symphony.security.comp.dao;

import jason.app.symphony.security.comp.entity.User;



public interface UserDao {

    User findByUsername(String username);

	User save(User user);

}
