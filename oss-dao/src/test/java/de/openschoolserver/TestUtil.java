package de.openschoolserver;

import java.util.Date;

import de.extis.core.util.UserUtil;

public class TestUtil {
	public static void main (String[] args) {
		System.out.println("Random Password 8 digits: " + UserUtil.createRandomPassword(8));
		System.out.println("Random Password 8 digits with given chars: " + UserUtil.createRandomPassword(8,"ABCabc897.!"));
	
		String userid = UserUtil.createUserId("Péter","Varkoly",new Date(), true, true, "G4N2Y2");
		System.out.println("CreateUserId:"+userid);
	}
}
