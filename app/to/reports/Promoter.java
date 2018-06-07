package to.reports;

import models.PromotionCompany;

/**
 * Created by arkady on 01/04/16.
 */
public class Promoter {
	private Long id;
	private String name;

	public Promoter() {
	}

	public Promoter(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Promoter(PromotionCompany promotionCompany) {
		this.id = promotionCompany.getId();
		this.name = promotionCompany.getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
