package to;

/**
 * Created by arkady on 06/04/16.
 */
public class FbUserInfo {
	private Long id;
	private String fullName;
	private String userpic;
	private Integer numberOfFriends;

	public FbUserInfo() {
	}

	public FbUserInfo(Long id, String fullName, String userpic, Integer numberOfFriends) {
		this.id = id;
		this.fullName = fullName;
		this.userpic = userpic;
		this.numberOfFriends = numberOfFriends;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public Integer getNumberOfFriends() {
		return numberOfFriends;
	}

	public void setNumberOfFriends(Integer numberOfFriends) {
		this.numberOfFriends = numberOfFriends;
	}
}
