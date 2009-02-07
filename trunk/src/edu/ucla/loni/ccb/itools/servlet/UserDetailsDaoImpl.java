package edu.ucla.loni.ccb.itools.servlet;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.RUser;

public class UserDetailsDaoImpl implements UserDetailsService {
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		RUser user = DaoFactory.getDaoFactory().getUserDao().getUser(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		GrantedAuthority[] arrayAuths = {new GrantedAuthorityImpl(user.getRole())};
		return new User(username, user.getPasswd(), true, true, true, true, arrayAuths);
	}

}
