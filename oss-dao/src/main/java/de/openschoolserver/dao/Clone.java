package de.openschoolserver.dao;

import java.util.List;

public class Clone {
	
	private boolean multicast;
	
	private List<Long> deviceIds;
	
	private List<Long> partitionIds;

	public Clone() {
		// TODO Auto-generated constructor stub
	}

	public boolean isMultipath() {
		return this.multicast;
	}

	public void setMultipath(boolean multicast) {
		this.multicast = multicast;
	}

	public List<Long> getDeviceIds() {
		return this.deviceIds;
	}

	public void setDeviceIds(List<Long> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<Long> getPartitionIds() {
		return this.partitionIds;
	}

	public void setPartitionIds(List<Long> partitionIds) {
		this.partitionIds = partitionIds;
	}

}
