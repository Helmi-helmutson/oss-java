/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.ArrayList;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.*;

public class DeviceController extends Controller {

	public DeviceController(Session session) {
		super(session);
	}

	/*
	 * Return a device found by the Id
	 */
	public Device getById(long deviceId) {
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

	/*
	 * Delivers a list of devices wit the given device type
	 */
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

	/*
	 * Delivers a list of all existing devices
	 */
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

	/*
	 * Deletes a list of device given by the device Ids.
	 */
	public Response delete(List<Long> deviceIds) {
		EntityManager em = getEntityManager();
		try {
			for( Long deviceId : deviceIds) {
				Device dev = em.find(Device.class, deviceId);
				em.remove(dev);
			}
			return new Response(this.getSession(),"OK", "Devices were deleted succesfully.");
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Deletes a list of device given by the device Ids.
	 */
	public Response delete(Long deviceId) {
		EntityManager em = getEntityManager();
		try {
			Device dev = em.find(Device.class, deviceId);
			em.remove(dev);
			return new Response(this.getSession(),"OK", "Device was deleted succesfully.");
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Creates devices
	 */
	public Response add(List<Device> devices) {
		EntityManager em = getEntityManager();
		try {
			for(Device dev: devices){
				em.getTransaction().begin();
				em.persist(dev);
				em.getTransaction().commit();
			}
			return new Response(this.getSession(),"OK", "Devices were created succesfully.");
		} catch (Exception e) {
			System.err.println(e.getMessage()); //TODO
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Find a device given by the IP address
	 */
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

	/*
	 * Find a device given by the MAC address
	 */
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

	/*
	 * Find a device given by the name
	 */
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

	/*
	 * Search devices given by a substring
	 */
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

	/*
	 * Find the default printer for a device
	 * If no printer was defined by the device find this from the room
	 */
	public String getDefaultPrinter(long deviceId) {
		Device device = this.getById(deviceId);
		Device printer = device.getDefaultPrinter();
		if( printer != null)
		   return printer.getName();
		printer = device.getRoom().getDefaultPrinter();
		if( printer != null)
			   return printer.getName();
		return "";
	}

	/*
	 * Find the available printer for a device
	 * If no printer was defined by the device find these from the room
	 */
	public List<String> getAvailablePrinters(long deviceId) {
		Device device = this.getById(deviceId);
		List<String> printers   = new ArrayList<String>();
		for( Device printer : device.getAvailablePrinters() ) {
			printers.add(printer.getName());
		}
		if( printers.isEmpty() ){
			for(Device printer : device.getRoom().getAvailablePrinters()){
				printers.add(printer.getName());
			}
		}
		return printers;
	}
	
	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<String> getLoggedInUsers(String IP) {
		Device device = this.getByIP(IP);
		List<String> users = new ArrayList<String>();
		for( User user : device.getLoggedIn() )
			users.add(user.getUid());
			//users.add(user.getUid() + " " + user.getGivenName() + " " +user.getSureName());
		return users;
	}
	
	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<String> getLoggedInUsers(Long deviceId) {
		Device device = this.getById(deviceId);
		List<String> users = new ArrayList<String>();
		for( User user : device.getLoggedIn() )
			users.add(user.getUid());
		return users;
	}

	public Response setDefaultPrinter(long deviceId, long defaultPrinterId) {
		// TODO Auto-generated method stub
		Device device         = this.getById(deviceId);
		Device defaultPrinter = this.getById(defaultPrinterId);
		device.setDefaultPrinter(defaultPrinter);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Default printer was set succesfully.");
	}
	
	public Response setAvailablePrinters(long deviceId, List<Long> availablePrinterIds) {
		// TODO Auto-generated method stub
		Device device         = this.getById(deviceId);
		List<Device> availablePrinters = new ArrayList<Device>();
		for( Long aP : availablePrinterIds ) {
			availablePrinters.add(this.getById(aP));
		}
		device.setAvailablePrinters(availablePrinters);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Available printers were set succesfully.");
	}
}
