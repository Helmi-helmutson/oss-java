/* (c) 2017 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao.internal;

import de.openschoolserver.dao.User;

public interface OSSUserPlugin {
	public boolean addUser(User user);
	public boolean deleteUser(User user);
}
