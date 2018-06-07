package models;

import com.avaje.ebean.Model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by arkady on 02/04/16.
 */
@Entity
public class ClickerPoint extends Model {

	@EmbeddedId
	private ClickerPointPk clickerPointPk = new ClickerPointPk();
	private Short men = 0;
	private Short women = 0;

	public static Finder<ClickerPointPk, ClickerPoint> find = new Finder<>(ClickerPoint.class);

	public ClickerPoint() {
	}

	public ClickerPoint(ClickerPointPk clickerPointPk) {
		this.clickerPointPk = clickerPointPk;
	}

	public ClickerPointPk getClickerPointPk() {
		return clickerPointPk;
	}

	public void setClickerPointPk(ClickerPointPk clickerPointPk) {
		this.clickerPointPk = clickerPointPk;
	}

	public Short getMen() {
		return men;
	}

	public void setMen(Short men) {
		this.men = men;
	}

	public Short getWomen() {
		return women;
	}

	public void setWomen(Short women) {
		this.women = women;
	}
}
