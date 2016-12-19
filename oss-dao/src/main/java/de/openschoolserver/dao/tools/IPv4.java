package de.openschoolserver.dao.tools;

import java.util.ArrayList;

import java.util.List;
import java.util.Collections;

public class IPv4 {

	public IPv4() {
	}

	public String convertNumericIpToSymbolic(Integer ip) {
		StringBuffer sb = new StringBuffer(15);

		for (int shift = 24; shift > 0; shift -= 8) {

			// process 3 bytes, from high order byte down.
			sb.append(Integer.toString((ip >>> shift) & 0xff));

			sb.append('.');
		}
		sb.append(Integer.toString(ip & 0xff));

		return sb.toString();
	}

	/**
	 * Convert IP from symbolic into numeric form
	 *
	 * @return
	 */

	public int convertSymbolicIPToNumeric(String symbolicIP)
	{
		/* IP */
		String[] st = symbolicIP.split("\\.");

		if (st.length != 4)
			throw new NumberFormatException("Invalid IP address: " + symbolicIP);

		int i         = 24;
		int numericIP = 0;

		for (int n = 0; n < st.length; n++) {

			int value = Integer.parseInt(st[n]);

			if (value != (value & 0xff)) {

				throw new NumberFormatException("Invalid IP address: "+ symbolicIP);
			}

			numericIP += value << i;
			i -= 8;
		}
		return numericIP;
	}


	public String getBinary(Integer number) {
		String result = "";

		Integer ourMaskBitPattern = 1;
		for (int i = 1; i <= 32; i++) {

			if ((number & ourMaskBitPattern) != 0) {

				result = "1" + result; // the bit is 1
			} else { // the bit is 0

				result = "0" + result;
			}
			if ((i % 8) == 0 && i != 0 && i != 32)

				result = "." + result;
			ourMaskBitPattern = ourMaskBitPattern << 1;

		}
		return result;
	}

	public boolean validateIPAddress(String IPAddress) {

		if (IPAddress.startsWith("0")) {
			return false;
		}

		if (IPAddress.isEmpty()) {
			return false;
		}

		if (IPAddress
				.matches("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z")) {

			return true;
		}
		return false;
	}

	public List<String> sortIPAddresses(List<String> ipaddresses) {

		List<Integer> numericIPAddresses = new ArrayList<Integer>();
		List<String>  sortedIPAddresses  = new ArrayList<String>();
		for( String IP : ipaddresses ) {
			numericIPAddresses.add(convertSymbolicIPToNumeric(IP));
		}
		Collections.sort(numericIPAddresses);
		for( Integer IP : numericIPAddresses ) {
			sortedIPAddresses.add(convertNumericIpToSymbolic(IP));
		}
		return sortedIPAddresses;
	}
}


