package de.openschoolserver.dao.controller;



import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import junit.framework.TestCase;
import java.nio.file.Paths;

public class OSSDaoTestCase extends TestCase {

     protected SessionController c = null;

    protected Session getValidAdminSession() {
    	User user = new User(); 
    	user.setId(1);
    	user.setUid("admin");
    	user.setRole("sysadmins");
    	user.setSureName("Administrator");
    	
        Session sessionAdmin1 = c.createSessionWithUser(user, null, null); //TODO login
        return sessionAdmin1;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
       
        Config.OSS_CONFIG=Paths.get("src/test/resources/schoolserver");
        c = new SessionController();
       
     

//      

    }

    public void testDummy() {
        assertNotNull(c);
    }

}
