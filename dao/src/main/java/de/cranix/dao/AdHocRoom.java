/**
 * 
 */
package de.cranix.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author petervarkoly
 *
 */
public class AdHocRoom extends Room {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean studentsOnly = false;
	private List<User> users     = new ArrayList<User>();
	private List<Group> groups   = new ArrayList<Group>();

	/**
	 * 
	 */
	public AdHocRoom() {
		// TODO Auto-generated constructor stub
	}
	public AdHocRoom(Room room) {
		super.setId(room.getId());
		super.setName(room.getName());
		super.setDescription(room.getDescription());
		super.setNetMask(room.getNetMask());
		super.setPlaces(room.getPlaces());
		super.setStartIP(room.getStartIP());
		super.setHwconf(room.getHwconf());
		super.setRoomControl(room.getRoomControl());
		super.setRoomType(room.getRoomType());
	}
	
	public boolean isStudentsOnly() {
		return studentsOnly;
	}

	public void setStudentsOnly(boolean studentsOnly) {
		this.studentsOnly = studentsOnly;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
