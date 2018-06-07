package to.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arkady on 06/04/16.
 */
public class FbPictureData {
	private String url;
	@JsonProperty("is_silhouette")
	private Boolean isSilhouette;

	public FbPictureData() {
	}

	public FbPictureData(String url, Boolean isSilhouette) {
		this.url = url;
		this.isSilhouette = isSilhouette;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getSilhouette() {
		return isSilhouette;
	}

	public void setSilhouette(Boolean silhouette) {
		isSilhouette = silhouette;
	}
}
