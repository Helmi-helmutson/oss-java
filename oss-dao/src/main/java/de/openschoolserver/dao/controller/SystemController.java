package de.openschoolserver.dao.controller;

import de.openschoolserver.dao.Session;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Enumerate;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Room;
import java.util.*;

public class SystemController extends Controller {

    Logger logger = LoggerFactory.getLogger(SystemController.class);
    
    public SystemController(Session session) {
        super(session);
    }

    public List<Map<String, String>> getStatus() {
        //Initialize of some variable
        List<Map<String, String>> statusList = new ArrayList<>();
        EntityManager em = getEntityManager();
        Map<String,String> statusMap;
        Query query;
        Integer count;
        
        //TODO System Load, HD, License, ....

        //Groups;
        statusMap = new HashMap<>();
        statusMap.put("name","groups");
        for( String groupType : this.getEnumerates("groupType")) {
            query = em.createNamedQuery("Group.getByType").setParameter("groupType",groupType);
            count = query.getResultList().size();
            statusMap.put(groupType,count.toString());
        }
        statusList.add(statusMap);
        
        //Users
        statusMap = new HashMap<>();
        statusMap.put("name","users");
        for( String role : this.getEnumerates("role")) {
            query = em.createNamedQuery("User.getByRole").setParameter("role",role);
            count = query.getResultList().size();
            statusMap.put(role,count.toString());
            Integer loggedOn = 0;
            for( User u : (List<User>) query.getResultList() ) {
                loggedOn += u.getLoggedOn().size();
            }
            statusMap.put(role + "-loggedOn", loggedOn.toString());
        }
        statusList.add(statusMap);
        
        //Rooms
        statusMap = new HashMap<>();
        statusMap.put("name","rooms");
        for( String roomType : this.getEnumerates("roomType")) {
            query = em.createNamedQuery("Room.getByType").setParameter("type",roomType);
            count = query.getResultList().size();
            statusMap.put(roomType,count.toString());
        }
        statusList.add(statusMap);
        
        //Rooms
        statusMap = new HashMap<>();
        statusMap.put("name","devices");
        for( String deviceType : this.getEnumerates("deviceType")) {
            query = em.createNamedQuery("Device.getByType").setParameter("type",deviceType);
            count = query.getResultList().size();
            statusMap.put(deviceType,count.toString());
        }
        statusList.add(statusMap);
        
        //Software
        SoftwareController softwareController = new SoftwareController(this.session);
        statusList.add(softwareController.statistic());
        
        
        return statusList;
    }
    
    public List<String> getEnumerates(String type ) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type);
            List<String> results = new ArrayList<String>();
            for( Enumerate e :  (List<Enumerate>) query.getResultList() ) {
                results.add(e.getValue());
            }
            return results;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    public Response addEnumerate(String type, String value) {
        EntityManager em = getEntityManager();
        Enumerate en = new Enumerate();
        en.setName(type);
        en.setValue(value);
        try {
            em.persist(en);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Response(this.getSession(),"ERROR", e.getMessage());
        } finally {
            em.close();
        }
        return new Response(this.getSession(),"OK","Enumerate was created succesfully.");
    }
    
    public Response removeEnumerate(String type, String value) {
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type).setParameter("value", value);
        try {
            Enumerate en = (Enumerate) query.getSingleResult();
            em.remove(en);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new Response(this.getSession(),"ERROR", e.getMessage());
        } finally {
            em.close();
        }
        return new Response(this.getSession(),"OK","Enumerate was removed succesfully.");
    }
    
    // Functions for manipulating firewall
    
    public Map<String, String> getFirewallIncomingRules() {
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        Map<String,String> statusMap;
        //External Ports
        statusMap = new HashMap<>();
        statusMap.put("ssh", "false");
        statusMap.put("https", "false");
        statusMap.put("rdesktop", "false");
        statusMap.put("other", "");
        for( String extPort : fwConfig.getConfigValue("FW_SERVICES_EXT_TCP").split(" ") ) {
            switch(extPort) {
            case "ssh":
            case "22":
                statusMap.put("ssh","true");
                break;
            case "443":
            case "https":
                statusMap.put("https", "true");
                break;
            case "3389":
            case "ms-wbt-server":
                statusMap.put("rdesktop", "true");
                break;
            default:
                statusMap.put("other",statusMap.get("other")+" "+ extPort);
                
            }
        }
        return statusMap;
    }
    
    public Response setFirewallIncomingRules(Map<String, String> firewallExt) {
        List<String> fwServicesExtTcp = new ArrayList<String>();
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        if( firewallExt.get("ssh").equals("true") )
            fwServicesExtTcp.add("ssh");
        if( firewallExt.get("https").equals("true"))
            fwServicesExtTcp.add("https");
        if( firewallExt.get("rdesktop").equals("true") )
            fwServicesExtTcp.add("3389");
        if( firewallExt.get("other") != null && !firewallExt.get("other").isEmpty())
            fwServicesExtTcp.add(firewallExt.get("other"));
        fwConfig.setConfigValue("FW_SERVICES_EXT_TCP", String.join(" ", fwServicesExtTcp));
        this.systemctl("start", "SuSEfirewall2");
        return new Response(this.getSession(),"OK","Firewall incoming access rule  was set succesfully.");
    }
    
    public List<Map<String, String>> getFirewallOutgoingRules() {
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        List<Map<String, String>> firewallList = new ArrayList<>();
        Map<String,String> statusMap;
        RoomController roomController = new RoomController(this.session);
        DeviceController deviceController = new DeviceController(this.session);
        
        for( String outRule : fwConfig.getConfigValue("FW_MASQ_NETS").split(" ") ) {
            statusMap = new HashMap<>();
            String[] rule = outRule.split(",");
            String[] host = rule[0].split("/");
            String   dest = rule[1];
            String   prot = rule.length > 2 ? rule[2] : "all";
            String   port = rule.length > 3 ? rule[3] : "all";
            if(host[1].equals("32")) {
                Device device = deviceController.getByIP(host[0]);
                statusMap.put("id", Long.toString(device.getId()));
                statusMap.put("name", device.getName());
                statusMap.put("type", "host");
            } else {
                Room room = roomController.getByIP(host[0]);
                statusMap.put("id", Long.toString(room.getId()));
                statusMap.put("name", room.getName());
                statusMap.put("type", "room" );
            }
            statusMap.put("dest", dest);
            statusMap.put("prot", prot);
            statusMap.put("port", port);
            firewallList.add(statusMap);
        }
        return firewallList;
    }
    
    public Response setFirewallOutgoingRules(List<Map<String, String>> firewallList) {
        List<String> fwMasqNets = new ArrayList<String>();
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        RoomController roomController = new RoomController(this.session);
        DeviceController deviceController = new DeviceController(this.session);
        Device device;
        Room   room;
        for( Map<String,String> map : firewallList ) {
            StringBuilder data = new StringBuilder();
            if( map.get("type").equals("room")) {
                room = roomController.getById(Long.parseLong(map.get("id")));
                data.append(room.getNetwork() + "/" + String.valueOf(room.getNetMask()) +",");
            } else {
                device = deviceController.getById(Long.parseLong(map.get("id")));
                data.append(device.getIp() + "/32,");
            }
            data.append(map.get("dest"));
            if( !map.get("prot").equals("all") ) {
                data.append("," + map.get("prot") + "," + map.get("port"));
            }
            fwMasqNets.add(data.toString());
        }
        fwConfig.setConfigValue("FW_ROUTE","yes");
        fwConfig.setConfigValue("FW_MASQUERADE","yes");
        fwConfig.setConfigValue("FW_MASQ_NETS", String.join(" ", fwMasqNets));
        this.systemctl("start", "SuSEfirewall2");
        return new Response(this.getSession(),"OK","Firewall outgoing access rule  was set succesfully.");
    }
    
    public List<Map<String, String>> getFirewallRemoteAccessRules() {
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        List<Map<String, String>> firewallList = new ArrayList<>();
        Map<String,String> statusMap;
        DeviceController deviceController = new DeviceController(this.session);
        
        for( String outRule : fwConfig.getConfigValue("FW_FORWARD_MASQ").split(" ") ) {
            statusMap = new HashMap<>();
            String[] rule = outRule.split(",");
            Device device = deviceController.getByIP(rule[1]);
            statusMap.put("ext", rule[3]);
            statusMap.put("id",  Long.toString(device.getId()) );
            statusMap.put("name", device.getName() );
            statusMap.put("port", rule[4]);
            firewallList.add(statusMap);
        }
        return firewallList;
    }
    
    public Response setFirewallRemoteAccessRules(List<Map<String, String>> firewallList) {
        List<String> fwForwardMasq = new ArrayList<String>();
        Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2");
        DeviceController deviceController = new DeviceController(this.session);
        for( Map<String,String> map : firewallList ) {
            Device device = deviceController.getById(Long.parseLong(map.get("id")));
            fwForwardMasq.add("0/0," + device.getIp() + ",tcp," + map.get("ext") + "," + map.get("port") );
        }
        fwConfig.setConfigValue("FW_FORWARD_MASQ", String.join(" ", fwForwardMasq));
        this.systemctl("start", "SuSEfirewall2");
        return new Response(this.getSession(),"OK","Firewall remote access rule  was set succesfully.");
    }
}
