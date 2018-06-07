package util;

import models.VenueRole;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arkady on 23/06/16.
 */
public class RolesHelper {

	private final static List<VenueRole> MANAGER = Arrays.asList(VenueRole.MANAGER);
	private final static List<VenueRole> ADVANCED = Arrays.asList(VenueRole.ASSISTANT_MANAGER, VenueRole.VIP_HOST);
	private final static List<VenueRole> SERVER = Arrays.asList(VenueRole.SERVER, VenueRole.BARTENDER);
	private final static List<VenueRole> BASIC = Arrays.asList(VenueRole.PROMOTER, VenueRole.WILL_CALL,
			VenueRole.INVENTORY, VenueRole.BUSSER, VenueRole.HEAD_BUSSER, VenueRole.GUEST_LIST, VenueRole.SECURITY,
			VenueRole.FRONT_DOOR_SECURITY, VenueRole.BARBACK, VenueRole.COAT_CHECK, VenueRole.HEAD_DOORMAN,
			VenueRole.FRONT_DOOR_CASH);

	public static boolean canViewAndModifyAllReservations(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	/**
	 * SPECIAL permissions
	 */
	public static boolean canViewAndModifyAllGuestListResos(List<VenueRole> currentUserRoles) {
		return currentUserRoles.contains(VenueRole.GUEST_LIST);
	}

	/**
	 * SPECIAL permissions
	 */
	public static boolean canOnlyViewAllResos(List<VenueRole> currentUserRoles) {
		return currentUserRoles.contains(VenueRole.HEAD_DOORMAN);
	}

	/**
	 * SPECIAL permissions
	 */
	public static boolean canWorkWithClicker(List<VenueRole> currentUserRoles) {
		return currentUserRoles.contains(VenueRole.HEAD_DOORMAN) || currentUserRoles.contains(VenueRole.SECURITY) ||
				currentUserRoles.contains(VenueRole.FRONT_DOOR_SECURITY);
	}

	public static boolean canViewAndManageTableView(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canReleaseAndCompleteAllTables(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canReleaseAndCompleteTablesAssignedToMe(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (SERVER.contains(role) || MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canViewClientsSection(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canSeeAndUseReportsSection(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canControlBsMinsAndCloseBsGl(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canManageVenueInfo(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role)) return true;
		}
		return false;
	}

	public static boolean canCreateAndModifyEventInfo(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canViewAndManageEmployeeSection(List<VenueRole> currentUserRoles) {
		if (currentUserRoles == null || currentUserRoles.isEmpty()) return false;
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role)) return true;
		}
		return false;
	}

	public static boolean canArriveReservationsAndUseClicker(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canViewAndMakeNotesOnAllReservations(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canCreateAndModifyTags(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canAssignTags(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role) || SERVER.contains(role)) return true;
		}
		return false;
	}

	public static boolean canViewFullSnapshotInfo(List<VenueRole> currentUserRoles) {
		for (VenueRole role : currentUserRoles) {
			if (MANAGER.contains(role) || ADVANCED.contains(role)) return true;
		}
		return false;
	}

	public static boolean canViewEODStatementAndConfirm(List<VenueRole> currentUserRoles) {
		return currentUserRoles != null && currentUserRoles.contains(VenueRole.MANAGER);
	}

	public static List<VenueRole> normalizePromoterRoles(List<VenueRole> preferredRoles) {
		List<VenueRole> availableRolesOfPromoter = Arrays.asList(VenueRole.MANAGER, VenueRole.PROMOTER);
		final List<VenueRole> normalizedPromoterRoles = preferredRoles.stream()
				.filter(availableRolesOfPromoter::contains).collect(Collectors.toList());
		return normalizedPromoterRoles;
	}
}