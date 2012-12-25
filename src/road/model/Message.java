package road.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import util.Tool;

public class Message {

	private final String source;

	private final String dest;

	private final Type type;

	private final long time;

	private Object content;

	public Message(String source, Type type) {
		this.source = source == null ? "" : source;
		this.dest = Tool.getGate(this.source);
		this.type = type;
		this.time = DateTime.now().getMillis();
	}

	public Message(String source, String dest, Type type) {
		this.source = source == null ? "" : source;
		this.dest = dest == null ? "" : dest;
		this.type = type;
		this.time = DateTime.now().getMillis();
	}

	public String getSource() {
		return source;
	}

	public String getDest() {
		return dest;
	}

	public Type getType() {
		return type;
	}

	public long getTime() {
		return time;
	}

	public Object getContent() {
		return content;
	}

	public Message setContent(Object content) {
		this.content = content;
		return this;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public enum Type {

		WIA, WIA_ACK,

		CL, CLD,

		NE, NE_ACK, AA, AA_ACK,

		NA, NA_ACK,

		ALIVE, ALIVE_ACK, AGENT_CONFLICT;

	}

}
