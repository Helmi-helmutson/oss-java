/* (c) 2017 EXTIS GmbH - all rights reserved */
package de.cranix.dao.internal;

import de.cranix.dao.User;

public interface OSSUserPlugin {
	public boolean addUser(User user);
	public boolean deleteUser(User user);
}
