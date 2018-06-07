package tech.peller.mrblackandroidwatch.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arkady on 15/02/16
 */
public enum VenueRole {
	VIP_HOST("VIP Host"),
	GUEST_LIST("Guest List"),
	FRONT_DOOR_SECURITY("Front Door Security"),
	FRONT_DOOR_CASH("Front Door Cash"),
	HEAD_BUSSER("Head Busser"),
	WILL_CALL("Will Call"),
	BARTENDER("Bartender"),
	OWNER("Owner"),
	BARBACK("Barback"),
	ASSISTANT_MANAGER("Assistant Manager"),
	BUSSER("Busser"),
	INVENTORY("Inventory"),
	COAT_CHECK("Coat Check"),
	IN_HOUSE_PROMOTER("Basic User"),
	OFFICE("Office"),
	SERVER("Server"),
	SECURITY("Security"),
	MANAGER("Manager"),
	HEAD_DOORMAN("Head Doorman"),
	VIEW_ONLY_LIMITED("View Only Limited"),
	VIEW_ONLY_OWNER("View Only Owner"),
	ENTERTAINMENT("Entertainment"),
	PROMOTER("Promoter");

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

	public static VenueRole fromString(String text) {
		if(text != null) {
			for (VenueRole b : VenueRole.values()) {
				if (text.equalsIgnoreCase(b.name)) {
					return b;
				}
			}
		}
		return null;
	}
}
