package util;

import org.joda.time.DateTime;

public class Tool {

	public static int ipCompare(String ip1, String ip2) {
		int ret = 0;
		if (Tool.checkIp(ip1) && Tool.checkIp(ip2)) {
			String[] parts1 = ip1.split("\\.");
			String[] parts2 = ip2.split("\\.");
			int length = parts1.length <= parts2.length ? parts1.length
					: parts2.length;
			for (int i = 0; i < length; i++) {
				int num1 = Integer.parseInt(parts1[i]);
				int num2 = Integer.parseInt(parts2[i]);
				if (num1 != num2) {
					ret = num1 - num2;
					break;
				}
			}
		}
		return ret;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkIp(String ip) {
		boolean ret = false;
		if (ip != null) {
			ret = ip.matches("^(([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d?\\d|2[0-4]\\d|25[0-5])$");
		}
		return ret;
	}

	public static boolean hasSameGate(String ip1, String ip2) {
		boolean ret = false;
		if (checkIp(ip1) && checkIp(ip2)) {
			String gate1 = getGate(ip1);
			String gate2 = getGate(ip2);
			if (gate1.equals(gate2)) {
				ret = true;
			}
		}
		return ret;
	}

	public static boolean isInteger(String number) {
		boolean ret = false;
		try {
			Integer.parseInt(number);
			ret = true;
		} catch (NumberFormatException e) {
		}
		return ret;
	}

	public static boolean isLong(String number) {
		boolean ret = false;
		try {
			Long.parseLong(number);
			ret = true;
		} catch (NumberFormatException e) {
		}
		return ret;
	}
	
	public static boolean isDouble(String number) {
		boolean ret = false;
		try {
			Double.parseDouble(number);
			ret = true;
		} catch (NumberFormatException e) {
		}
		return ret;
	}

	public static String getCurrentTime() {
		return new DateTime().toString("yyyy-MM-dd HH:mm:ss");
	}

	public static String getGate(String ip) {
		String gate = "";
		if (Tool.checkIp(ip)) {
			int lastIndex = ip.lastIndexOf(".");
			gate = ip.substring(0, lastIndex + 1) + "0";
		}
		return gate;
	}

}
