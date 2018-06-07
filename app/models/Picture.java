package models;

import com.avaje.ebean.Model;
import com.google.inject.Inject;
import modules.s3.S3;
import play.Logger;

import javax.persistence.*;

/**
 * Created by arkady on 27/06/16.
 */
@Entity
public class Picture extends Model {

	@Inject
	@Transient
	private S3 s3;

	@Id
	private Long id;
	@Column(columnDefinition = "varchar(1000)")
	private String link;

	public static Finder<Long, Picture> find = new Finder<>(Picture.class);

	public Picture() {
	}

	public Picture(String link) {
		this.link = link;
	}

	public Picture(Long id, String link) {
		this.id = id;
		this.link = link;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public void delete() {
		super.delete();

		try {
			final int lastIndexOfSlash = link.lastIndexOf("/");
			final String fileName = link.substring(lastIndexOfSlash + 1); // +1 to exclude slash
			s3.deleteFromS3(fileName);
		} catch (Exception e) {
			Logger.warn("Can't delete image from s3", e);
		}
	}
}
