package to.venue;

import to.EventInfo;
import to.clicker.ClickerState;

/**
 * Created by arkady on 27/05/16.
 */
public class Snapshot {
	private int seated;
	private int approved;
	private int queue;
	private int guestList;

	private ClickerState clickerState = new ClickerState();
	//TODO remove
	private int capacity;
	private int ins;
	private int outs;

	private int glBooked;
	private int glGuysActual;
	private int glGirlsActual;

	private int bsBooked;
	private int bsMinSpend;
	private int bsGuysActual;
	private int bsGirlsActual;
	private int myReservations;

	private int unreadReservationsApprovalRequest;
	private int unreadReservationsApproved;
	private int unreadReservationsRejected;
	private int unreadTableReleased;
	private int unreadEmployees;
	private int unreadAssignments;

	private boolean showClicker;

	private EventInfo eventInfo;
	private State state;

	public int getSeated() {
		return seated;
	}

	public void setSeated(int seated) {
		this.seated = seated;
	}

	public int getApproved() {
		return approved;
	}

	public void setApproved(int approved) {
		this.approved = approved;
	}

	public int getQueue() {
		return queue;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}

	public int getGuestList() {
		return guestList;
	}

	public void setGuestList(int guestList) {
		this.guestList = guestList;
	}

	public ClickerState getClickerState() {
		return clickerState;
	}

	public void setClickerState(ClickerState clickerState) {
		this.clickerState = clickerState;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getIns() {
		return ins;
	}

	public void setIns(int ins) {
		this.ins = ins;
	}

	public int getOuts() {
		return outs;
	}

	public void setOuts(int outs) {
		this.outs = outs;
	}

	public int getGlBooked() {
		return glBooked;
	}

	public void setGlBooked(int glBooked) {
		this.glBooked = glBooked;
	}

	public int getGlGuysActual() {
		return glGuysActual;
	}

	public void setGlGuysActual(int glGuysActual) {
		this.glGuysActual = glGuysActual;
	}

	public int getGlGirlsActual() {
		return glGirlsActual;
	}

	public void setGlGirlsActual(int glGirlsActual) {
		this.glGirlsActual = glGirlsActual;
	}

	public int getBsBooked() {
		return bsBooked;
	}

	public void setBsBooked(int bsBooked) {
		this.bsBooked = bsBooked;
	}

	public int getBsGuysActual() {
		return bsGuysActual;
	}

	public void setBsGuysActual(int bsGuysActual) {
		this.bsGuysActual = bsGuysActual;
	}

	public int getBsGirlsActual() {
		return bsGirlsActual;
	}

	public void setBsGirlsActual(int bsGirlsActual) {
		this.bsGirlsActual = bsGirlsActual;
	}

	public EventInfo getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}

	public int getBsMinSpend() {
		return bsMinSpend;
	}

	public void setBsMinSpend(int bsMinSpend) {
		this.bsMinSpend = bsMinSpend;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isShowClicker() {
		return showClicker;
	}

	public void setShowClicker(boolean showClicker) {
		this.showClicker = showClicker;
	}

	public int getMyReservations() {
		return myReservations;
	}

	public void setMyReservations(int myReservations) {
		this.myReservations = myReservations;
	}

	public int getUnreadReservationsApprovalRequest() {
		return unreadReservationsApprovalRequest;
	}

	public void setUnreadReservationsApprovalRequest(int unreadReservationsApprovalRequest) {
		this.unreadReservationsApprovalRequest = unreadReservationsApprovalRequest;
	}

	public int getUnreadReservationsApproved() {
		return unreadReservationsApproved;
	}

	public void setUnreadReservationsApproved(int unreadReservationsApproved) {
		this.unreadReservationsApproved = unreadReservationsApproved;
	}

	public int getUnreadReservationsRejected() {
		return unreadReservationsRejected;
	}

	public void setUnreadReservationsRejected(int unreadReservationsRejected) {
		this.unreadReservationsRejected = unreadReservationsRejected;
	}

	public int getUnreadTableReleased() {
		return unreadTableReleased;
	}

	public void setUnreadTableReleased(int unreadTableReleased) {
		this.unreadTableReleased = unreadTableReleased;
	}

	public int getUnreadEmployees() {
		return unreadEmployees;
	}

	public void setUnreadEmployees(int unreadEmployees) {
		this.unreadEmployees = unreadEmployees;
	}

	public int getUnreadAssignments() {
		return unreadAssignments;
	}

	public void setUnreadAssignments(int unreadAssignments) {
		this.unreadAssignments = unreadAssignments;
	}
}
