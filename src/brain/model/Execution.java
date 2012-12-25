package brain.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Execution {

	private boolean success;

	private String feedback;

	public Execution(boolean success, String feedback) {
		this.success = success;
		this.feedback = feedback;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getFeedback() {
		return feedback;
	}

	public void update(boolean success, String feedback) {
		this.success = success;
		this.feedback = feedback == null ? "" : feedback;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
