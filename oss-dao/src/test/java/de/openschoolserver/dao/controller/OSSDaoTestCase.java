package de.openschoolserver.dao.controller;



import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controler.SessionControler;
import junit.framework.TestCase;
import java.nio.file.Paths;

public class OSSDaoTestCase extends TestCase {

    protected SessionControler c = null;

    protected Session getValidAdminSession() {
    	String user =     "admin";
    	String password = "dummy";
    	
        Session sessionAdmin1 = c.createSessionWithUser(user, password,""); //TODO login
        return sessionAdmin1;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
       
    
        Session session =  new Session();
        session.setIP("1.1.1.1");
         c = new SessionControler(session);
     

//      

    }

    public void testDummy() {
        assertNotNull(c);
    }

}
