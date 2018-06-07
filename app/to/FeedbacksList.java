package to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 26/03/16.
 */
public class FeedbacksList {
	private List<FeedbackInfo> feedbackList = new ArrayList<>();

	public FeedbacksList() {
	}

	public FeedbacksList(List<FeedbackInfo> feedbackList) {
		this.feedbackList = feedbackList;
	}

	public List<FeedbackInfo> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(List<FeedbackInfo> feedbackList) {
		this.feedbackList = feedbackList;
	}
}
