package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 15/02/16.
 */
public enum VenueRole {

	MANAGER("Manager"),
	OFFICE("Office"),
	PROMOTER("Promoter"),
	HEAD_DOORMAN("Head doorman"),
	SERVER("Server"),
	BARBACK("Barback"),
	GUEST_LIST("Guest list"),
	WILL_CALL("Will call"),
	SECURITY("Security"),
	ASSISTANT_MANAGER("Assistant manager"),
	VIP_HOST("VIP host"),
	HEAD_BUSSER("Head busser"),
	BARTENDER("Bartender"),
	BUSSER("Busser"),
	FRONT_DOOR_CASH("Front door cash"),
	COAT_CHECK("Coat check"),
	INVENTORY("Inventory"),
	FRONT_DOOR_SECURITY("Front door security");

	private String name;
	private static Map<String, String> roleToNameMapping;

	VenueRole(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Map<String, String> all() {
		if (roleToNameMapping == null) {
			initMapping();
		}

		return roleToNameMapping;
	}

	private static void initMapping() {
		roleToNameMapping = new HashMap<>();
		for (VenueRole s : values()) {
			roleToNameMapping.put(s.name(), s.name);
		}
	}
}
