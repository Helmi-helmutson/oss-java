/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controler;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Integer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.extis.core.util.UserUtil;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controler.DHCPConfig;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;

@SuppressWarnings( "unchecked" )
public class UserControler extends Controler {

    Logger logger = LoggerFactory.getLogger(UserControler.class);

    public UserControler(Session session) {
        super(session);
    }
    
    public User getById(long userId) {
        EntityManager em = getEntityManager();    
        try {
            return em.find(User.class, userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<User> getByRole(String role) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("User.getByRole");
            query.setParameter("role", role);
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    public User getByUid(String uid) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("User.getByUid");
            query.setParameter("uid", uid);
            return (User) query.getResultList().get(0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<User> search(String search) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("User.search");
            query.setParameter("search", search + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<User> getAll() {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("User.findAll"); 
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Response add(User user){
        EntityManager em = getEntityManager();
        //Check role
        if( user.getRole() == null )
            return new Response(this.getSession(),"ERROR", "You have to define the role of the user.");
        //Check Birthday
        if( user.getBirthDay() == null ) {
            if( user.getRole().equals("sysadmins") || user.getRole().equals("templates")) {
                Date now = new Date(System.currentTimeMillis());
                user.setBirthDay(now);
            } else {
                return new Response(this.getSession(),"ERROR", "You have to define the birthday.");
            }
                
        }
        // Create uid if not given
        if( user.getUid() == "") {
            String userId = UserUtil.createUserId( user.getGivenName(),
                                                   user.getSureName(),
                                                   user.getBirthDay(),
                                                   true,
                                                   this.getConfigValue("SCHOOL_STRING_CONVERT_TYPE") == "telex", 
                                                   this.getConfigValue("SCHOOL_LOGIN_SCHEME")
                                                   );
            user.setUid( this.getConfigValue("SCHOOL_LOGIN_PREFIX") + userId );
            Integer i = 1;
            while( !this.isNameUnique(user.getUid()) ) {
                user.setUid( this.getConfigValue("SCHOOL_LOGIN_PREFIX") + userId + i );
            }
        }
        else
        {
            // First we check if the parameter are unique.
	    // workstation users have a user called as itself
            if( !user.getRole().equals("workstations") && !this.isNameUnique(user.getUid())){
                return new Response(this.getSession(),"ERROR", "User name is not unique.");
            }
            // Check if uid contains non allowed characters
            if( this.checkNonASCII(user.getUid()) ) {
                return new Response(this.getSession(),"ERROR", "Uid contains not allowed characters.");
            }
        }
        // Check the user password
        if( user.getRole().equals("workstations")) {
		user.setPassword(user.getUid());
	} else if( user.getPassword() == "" ) {
            user.setPassword(UserUtil.createRandomPassword(9,"ACGqf123#"));
        }
        else
        {
            Response response = this.checkPassword(user.getPassword());
            if(response != null) {
                return response;
            }
        }
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            GroupControler groupControler = new GroupControler(this.session);
            Group group = groupControler.getByName(user.getRole());
            if( group != null ) {
            	groupControler.addMember(group,user);;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Response(this.getSession(),"ERROR", e.getMessage());
        } finally {
            em.close();
        }
        this.startPlugin("add_user",user);
        return new Response(this.getSession(),"OK", user.getUid() + " (" + user.getGivenName() + " " + user.getSureName() + ") was created with password: '" + user.getPassword()+ "'.");
    }

        
    public List<Response> add(List<User> users) {
        List<Response> results = new ArrayList<Response>();
        for( User user : users ) {
            results.add(this.add(user));
        }
        return results;
    }
    
    public Response modify(User user){
        User oldUser = this.getById(user.getId());
        if(!user.getPassword().isEmpty()) {
            Response response = this.checkPassword(user.getPassword());
            if(response != null)
                return response;
        }
        oldUser.setGivenName( user.getGivenName());
        oldUser.setSureName(user.getSureName());
        oldUser.setBirthDay(user.getBirthDay());
        oldUser.setPassword(user.getPassword());
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(oldUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Response(this.getSession(),"ERROR", e.getMessage());
        } finally {
            em.close();
        }
        this.startPlugin("modify_user",oldUser);
        return new Response(this.getSession(),"OK","User was modified succesfully");
    }
    
    public Response delete(long userId){
        User user = this.getById(userId);
        if( this.isProtected(user)) {
            return new Response(this.getSession(),"ERROR","This user must not be deleted.");
        }

        this.startPlugin("delete_user",user);
        EntityManager em = getEntityManager();
        List<Device> devices = user.getOwnedDevices();
        boolean restartDHCP = ! devices.isEmpty();
        em.getTransaction().begin();
        if( ! em.contains(user)) {
        	user = em.merge(user);
        }
        em.remove(user);
        em.getTransaction().commit();
        em.getEntityManagerFactory().getCache().evictAll();
        if( restartDHCP ) {
            DHCPConfig dhcpConfig = new DHCPConfig(this.session);
            dhcpConfig.Create();
        }
        em.close();
        return new Response(this.getSession(),"OK","User was deleted");
    }
    
    public List<Group> getAvailableGroups(long userId){
        EntityManager em = getEntityManager();
        
        User user = this.getById(userId);
        Query query = em.createNamedQuery("Group.findAll");
        List<Group> allGroups = query.getResultList();
        allGroups.removeAll(user.getGroups());
        em.close();
        return allGroups;
    }

    public List<Group> getGroups(long userId) {
        User user = this.getById(userId);
        return user.getGroups();
    }

    public Response setGroups(long userId, List<Long> groupIds) {
        EntityManager em = getEntityManager();
        List<Group> groupsToRemove = new ArrayList<Group>();
        List<Group> groupsToAdd    = new ArrayList<Group>();
        List<Group> groups = new ArrayList<Group>();
        for( Long groupId : groupIds ) {
            groups.add(em.find(Group.class, groupId));
        }
        User user = this.getById(userId);
        for( Group group : groups ){
            if( ! user.getGroups().contains(group) ){
                groupsToAdd.add(group);
            }
        }
        for ( Group group : user.getGroups() ) {
            if( ! groups.contains(group) ) {
                groupsToRemove.add(group);
            }
        }
        try {
            em.getTransaction().begin();
            for( Group group : groupsToAdd ){
                group.getUsers().add(user);
                user.getGroups().add(group);
                em.merge(group);
            }
            for( Group group : groupsToRemove ) {
                group.getUsers().remove(user);
                user.getGroups().remove(group);
                em.merge(group);
            }
            em.merge(user);
            em.getTransaction().commit();
        }catch (Exception e) {
            return new Response(this.getSession(),"ERROR",e.getMessage());
        } finally {
            em.close();
        }
        for( Group group : groupsToAdd ){
            this.changeMemberPlugin("addmembers", group, user);
        }
        for( Group group : groupsToRemove ) {
            this.changeMemberPlugin("removemembers", group, user);
        }
        return new Response(this.getSession(),"OK","The groups of the user was set.");
    }

    public Response syncFsQuotas(List<List<String>> quotas) {
        EntityManager em = getEntityManager();
        User user;
        try {
            em.getTransaction().begin();
            for( List<String> quota : quotas) {
                if( quota.isEmpty() )
                    continue;
                user = this.getByUid(quota.get(0));
                if( user != null ) {
                    user.setFsQuotaUsed(Integer.valueOf(quota.get(1)));
                    user.setFsQuota(Integer.valueOf(quota.get(2)));
                    em.merge(user);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            return new Response(this.getSession(),"ERROR",e.getMessage());
        } finally {
            em.close();
        }
        return new Response(this.getSession(),"OK","The filesystem quotas was synced succesfully");
    }

	public List<User> getUsers(List<Long> userIds) {
		List<User> users = new ArrayList<User>();
		for ( Long id : userIds ){
			users.add(this.getById(id));
		}
		return users;
	}
}
