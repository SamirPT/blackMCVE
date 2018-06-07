package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by arkady on 02/04/16.
 */
@Entity
public class PromotionCompany extends Model {
	@Id
	private Long id;
	private String name;

	public static Finder<Long, PromotionCompany> finder = new Finder<>(PromotionCompany.class);

	public PromotionCompany() {
	}

	public PromotionCompany(Long id, String name) {
		this.id = id;
		this.name = name;
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
