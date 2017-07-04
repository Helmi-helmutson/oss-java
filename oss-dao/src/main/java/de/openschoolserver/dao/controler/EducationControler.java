package de.openschoolserver.dao.controler;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import de.openschoolserver.dao.*;

public class EducationControler extends Controler {

	public EducationControler(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Return the list of ids of rooms in which a user may actually control the access.
	 */
	public List<Long> getMyRooms() {
		List<Long> rooms = new ArrayList<Long>();
		if( this.session.getRoom().getRoomControl().equals("no_control")){
			for( Room room : new RoomControler(this.session).getAll() ) {
				switch(room.getRoomControl()) {
				case "no_control":
					break;
				case "all_teacher_control":
					rooms.add(room.getId());
					break;
				case "teacher_control":
					if( this.checkMConfig(room, "teacher_control", Long.toString((this.session.getUserId())))) {
						rooms.add(room.getId());
					}
				}
			}
		} else {
			rooms.add(this.session.getRoomId());
		}
		for( Category category : this.session.getUser().getCategories() ) {
			for( Room room : category.getRooms() ) {
				if( room.getRoomType().equals("virtualRoom")) {
					rooms.add(room.getId());
				}
			}
		}
		return rooms;
 	}

	/*
	 * Create the a new virtual room from a hash:
	 * {
	 *     "name"  : <Virtual room name>,
	 *     "description : <Descripton of the room>,
	 *     "studentsOnly : true/false
	 * }
	 */
	public Response createVirtualRoom(Map<String, String> virtualRoom) {
		EntityManager   em = getEntityManager();
		String name        = virtualRoom.get("name");
		String description = virtualRoom.get("description");
		Boolean studentsOnly = virtualRoom.get("studentsOnly").equals("true") ? true : false;
		User   owner        = this.session.getUser();
		/* Define the room */
		Room     room      = new Room();
		room.setName(name);
		room.setDescription(description);
		room.setRoomType("virtualRoom");
		
		/* Define the category */
		Category category  = new Category();
		category.setOwner(owner);
		owner.getCategories().add(category);
		
		try {
			em.getTransaction().begin();
			em.persist(room);
			em.persist(category);
			em.merge(owner);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			em.close();
			return new Response(this.getSession(),"ERROR", e.getMessage());
		}
		try {
			em.getTransaction().begin();
			if( virtualRoom.containsKey("groups") ) {
				/*
				 * Add group members to the virtual room
				 */
				GroupControler groupControler = new GroupControler(this.session);
				for( String id : virtualRoom.get("groups").split(",")) {
					Group group = groupControler.getById(Long.valueOf(id));
					for( User user : group.getUsers() ) {
						if(studentsOnly && ! user.getRole().equals("studetns")){
							continue;
						}
						category.getUsers().add(user);
						user.getCategories().add(category);
						em.merge(user);
						em.merge(category);
					}
				}
			}
			if( virtualRoom.containsKey("users") ) {
				/*
				 * Add users to the virtual room
				 */
				UserControler  userControler  = new UserControler(this.session);
				for( String id : virtualRoom.get("users").split(",")) {
					User user = userControler.getById(Long.valueOf(id));
					if(studentsOnly && ! user.getRole().equals("studetns")){
						continue;
					}
					category.getUsers().add(user);
					user.getCategories().add(category);
					em.merge(user);
					em.merge(category);
				}
			}
			if( virtualRoom.containsKey("devices") ) {
				/*
				 * Add devices to the virtual room
				 */
				DeviceControler deviceControler = new DeviceControler(this.session);
				for( String id: virtualRoom.get("devices").split(",")) {
					Device device = deviceControler.getById(Long.valueOf(id));
					category.getDevices().add(device);
					device.getCategories().add(category);
					em.merge(device);
					em.merge(category);
				}

			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Virtual Room was created succesfully"); 
	}
}
