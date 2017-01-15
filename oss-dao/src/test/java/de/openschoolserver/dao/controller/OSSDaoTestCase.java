package de.openschoolserver.dao.controller;



import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import junit.framework.TestCase;

public class OSSDaoTestCase extends TestCase {

     protected SessionController c = null;

    protected Session getValidAdminSession() {
    	User user = new User(); 
    	user.setId(0);
    	user.setUid("admin");
    	user.setRole("sysadmins");
    	user.setSureName("Administrator");
    	
        Session sessionAdmin1 = c.createSessionWithUser(user, null, null); //TODO login
        return sessionAdmin1;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        c = new SessionController();
       
     

//      

    }

    public void testDummy() {
        assertNotNull(c);
    }

}
