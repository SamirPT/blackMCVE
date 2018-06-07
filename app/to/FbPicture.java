package to;

import to.reports.FbPictureData;

/**
 * Created by arkady on 06/04/16.
 */
public class FbPicture {

	private FbPictureData data;

	public FbPicture() {
	}

	public FbPicture(FbPictureData data) {
		this.data = data;
	}

	public FbPictureData getData() {
		return data;
	}

	public void setData(FbPictureData data) {
		this.data = data;
	}
}
