package models;

import com.avaje.ebean.Model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by arkady on 30/06/16.
 */
@Entity
public class ClosedTable extends Model {

	@EmbeddedId
	private ClosedTablePk closedTablePk;

	public static Finder<ClosedTablePk, ClosedTable> find = new Finder<>(ClosedTable.class);

	public ClosedTable(ClosedTablePk closedTablePk) {
		this.closedTablePk = closedTablePk;
	}

	public ClosedTablePk getClosedTablePk() {
		return closedTablePk;
	}

	public void setClosedTablePk(ClosedTablePk closedTablePk) {
		this.closedTablePk = closedTablePk;
	}
}
