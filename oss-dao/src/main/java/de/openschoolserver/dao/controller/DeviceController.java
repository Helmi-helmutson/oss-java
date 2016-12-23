package de.openschoolserver.dao.controller;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.tools.*;

public class DeviceController extends Controller {
	
	public DeviceController(Session session) {
		super(session);
	}

	public Device getById(int deviceId) {
		EntityManager em = getEntityManager();
		
		try {
			return em.find(Device.class, deviceId);
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public List<Device> getByTpe(String type) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getDeviceByType");
			query.setParameter("deviceType", type);
			return query.getResultList();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public List<Device> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.findAll");
			return query.getResultList();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public boolean delete(int deviceId) {
		EntityManager em = getEntityManager();
		try {
			Device dev = em.find(Device.class, deviceId);
			em.remove(dev);
			return true;
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return false;
		} finally {
			em.close();
		}
	}
	
	public boolean add(Device dev) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(dev);
			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage()); //TODO
			return false;
		} finally {
			em.close();
		}
	}
	
	public Device getByIP(String IP) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByIP");
			query.setParameter("IP", IP);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public Device getByMAC(String MAC) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByMAC");
			query.setParameter("MAC", MAC);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public Device getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByName");
			query.setParameter("name", name);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public Device search(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.search");
			query.setParameter("name", name);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public String getDefaultPrinter(int deviceId) {
		Device device = this.getById(deviceId);
		return device.getDefaultPrinter().getName();
	}
	
	public List<String> getAvailablePrinters(int deviceId) {
		Device device = this.getById(deviceId);
		List<String> printers   = new ArrayList<String>();
 		for( Device printer : device.getAvailablePrinters() ) {
 			printers.add(printer.getName());
 		}
 		return printers;
	}
}
