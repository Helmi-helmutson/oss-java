package de.openschoolserver.dao;

import java.util.List;

public class Clone {
	
	private boolean multipath;
	
	private List<Long> deviceIds;
	
	private List<Long> partitionIds;

	public Clone() {
		// TODO Auto-generated constructor stub
	}

	public boolean isMultipath() {
		return multipath;
	}

	public void setMultipath(boolean multipath) {
		this.multipath = multipath;
	}

	public List<Long> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<Long> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<Long> getPartitionIds() {
		return partitionIds;
	}

	public void setPartitionIds(List<Long> partitionIds) {
		this.partitionIds = partitionIds;
	}

}
