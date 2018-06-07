package to;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 19/03/16.
 */
public class StaffList {
	@ApiModelProperty(required = false)
	List<VenueStaffInfo> servers = new ArrayList<>();
	@ApiModelProperty(required = false)
	List<VenueStaffInfo> bussers = new ArrayList<>();
	@ApiModelProperty(required = false)
	List<VenueStaffInfo> hosts = new ArrayList<>();

	public List<VenueStaffInfo> getServers() {
		return servers;
	}

	public void setServers(List<VenueStaffInfo> servers) {
		this.servers = servers;
	}

	public List<VenueStaffInfo> getBussers() {
		return bussers;
	}

	public void setBussers(List<VenueStaffInfo> bussers) {
		this.bussers = bussers;
	}

	public List<VenueStaffInfo> getHosts() {
		return hosts;
	}

	public void setHosts(List<VenueStaffInfo> hosts) {
		this.hosts = hosts;
	}
}
