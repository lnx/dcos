package base;

public class Setting {

	public static final int MESSAGE_RETRY_COUNT = 3;
	public static final long ALIVE_CHECK_INTERVAL = 1000;
	public static final long ALIVE_CHECK_LOST = 1000 * 3;
	public static final long THREAD_SLEEP_INTERVAL = 100;
	public static final long WAITING_ACK_INTERVAL = 300;

	public static long AGENT_TENURE = 600000;

	private static final long AGENT_TENURE_DEFAULT = 600000;

	public static void updateTenure(long tenure) {
		if (tenure >= 1000) {
			AGENT_TENURE = tenure;
		}
	}

	public static void resetTenure() {
		AGENT_TENURE = AGENT_TENURE_DEFAULT;
	}

}
