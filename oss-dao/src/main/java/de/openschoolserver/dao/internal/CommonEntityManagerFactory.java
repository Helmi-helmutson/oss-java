/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JComboBox.KeySelectionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

//import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
//import org.eclipse.persistence.jpa.osgi.PersistenceProvider;







public class CommonEntityManagerFactory {

	public static final int DB_VERSION = 1;
	
    private static HashMap<String, CommonEntityManagerFactory> commonEmf = new HashMap<String, CommonEntityManagerFactory>();
    public static HashMap<Long, String> threadKeys = new HashMap<Long, String>();

    // private static HashMap<String, EntityManagerFactory> newEmf = new
    // HashMap<String, EntityManagerFactory>();

    private Map<String, Object> properties;
    private EntityManagerFactory emf;
   //TODO public static Logger logger = Logger.getLogger(CommonEntityManagerFactory.class.getName());

    private CommonEntityManagerFactory() {

    }

    public Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Object>();
            properties.put(PersistenceUnitProperties.TARGET_DATABASE, "MySql");
        //    properties.put(PersistenceUnitProperties.JDBC_DRIVER, "com.mysql.jdbc.Driver");

            // UNEXISTENT DATABASE SET HERE
     //       properties.put(PersistenceUnitProperties.JDBC_URL, "jdbc:mysql://claxssdb:3306/unexistentdatabase");

//            properties.put(PersistenceUnitProperties.JDBC_USER, "claxss");
//            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, "cl8x77");
//            properties.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, "1");
//            properties.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, "1");
//            properties.put(PersistenceUnitProperties.BATCH_WRITING, "JDBC");

            properties.put(PersistenceUnitProperties.CLASSLOADER, CommonEntityManagerFactory.class.getClassLoader());

            properties.put("eclipselink.logging.level", "WARNING");
            properties.put("eclipselink.logging.timestamp", "true");
            properties.put("eclipselink.logging.session", "true");
            properties.put("eclipselink.logging.thread", "true");
            properties.put("eclipselink.logging.exceptions", "true");
            /*
             * <property name="eclipselink.logging.level.sql" value="FINE"/>
             * <property name="eclipselink.logging.parameters" value="true"/>
             * <property name="eclipselink.logging.logger"
             * value="ServerLogger"/>
             * <property name="eclipselink.logging.logger"
             * value="DefaultLogger"/>
             */
	    try {
			File file = new File("/opt/oss-java/conf/oss-api.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties props = new Properties();
			props.load(fileInput);
			fileInput.close();

			Enumeration enuKeys = props.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = props.getProperty(key);
				properties.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        }
        return properties;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
          //  logger.debug("getEntityManagerFactory : EntityManagerFactory is null. Creating...");
            Map<String, Object> props = getProperties();
            //logger.debug("getEntityManagerFactory : JDBC_URL : " + props.get(PersistenceUnitProperties.JDBC_URL));

            emf = Persistence.createEntityManagerFactory("OSS", props);//TODO !!!!!!!!!!! new PersistenceProvider().createEntityManagerFactory(Activator.PLUGIN_ID, props);

            if (emf == null) {
           //     logger.error("getEntityManagerFactory : EntityManagerFactory still null.");
            	System.err.println("getEntityManagerFactory : EntityManagerFactory still null."); //TODO
            }
          //  logger.debug("getEntityManagerFactory : EntityManagerFactory Created.");
        }
        //logger.debug("getEntityManagerFactory : returning EntityManagerFactory. : " + emf);
        return emf;
    }

  

    public static CommonEntityManagerFactory instance(String key) {
       
        if (!commonEmf.containsKey(key)) {
    //        logger.debug("CommonEntityManagerFactory creating new for key : " + key);
            commonEmf.put(key, new CommonEntityManagerFactory());
        }

  
        return commonEmf.get(key);
    }

 

   

}
