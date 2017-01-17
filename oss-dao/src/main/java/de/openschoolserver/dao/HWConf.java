/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the HWConfs database table.
 * 
 */
@Entity
@Table(name="HWConfs")
@NamedQueries({
	@NamedQuery(name="HWConf.findAll", query="SELECT h FROM HWConf h"),
	@NamedQuery(name="HWConf.getByName", query="SELECT h FROM HWConf h WHERE h.name = :name")
})
public class HWConf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String description;

	private String name;

	private String deviceType;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="hwconf")
	private List<Device> devices;

	//bi-directional many-to-one association to Partition
	@OneToMany(mappedBy="hwconf")
	private List<Partition> partitions;

	//bi-directional many-to-one association to Room
	@OneToMany(mappedBy="hwconf")
	private List<Room> rooms;

	public HWConf() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public Device addDevice(Device device) {
		getDevices().add(device);
		device.setHwconf(this);

		return device;
	}

	public Device removeDevice(Device device) {
		getDevices().remove(device);
		device.setHwconf(null);

		return device;
	}

	public List<Partition> getPartitions() {
		return this.partitions;
	}

	public void setPartitions(List<Partition> partitions) {
		this.partitions = partitions;
	}

	public Partition addPartition(Partition partition) {
		getPartitions().add(partition);
		partition.setHwconf(this);

		return partition;
	}

	public Partition removePartition(Partition partition) {
		getPartitions().remove(partition);
		partition.setHwconf(null);

		return partition;
	}

	public List<Room> getRooms() {
		return this.rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public Room addRoom(Room room) {
		getRooms().add(room);
		room.setHwconf(this);

		return room;
	}

	public Room removeRoom(Room room) {
		getRooms().remove(room);
		room.setHwconf(null);

		return room;
	}

}
