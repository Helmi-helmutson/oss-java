package de.openschoolserver.dao.tools;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

public class StringToys {

	static CharSequence toReplace = "íéáűőúöüóÍÉÁŰŐÚÖÜÓß";
    static CharSequence replaceIn = "ieauououoIEAUOUOUOs";
    
	static public String createRandomPassword()
	{
		String[] salt = new String[3];
		salt[0] = "ABCDEFGHIJKLMNOPQRSTVWXYZ";
		salt[1] = "1234567890";
		salt[2] = "abcdefghijklmnopqrstvwxyz";
		Random rand = new Random();
		StringBuilder builder = new StringBuilder();
		int saltIndex  = 2;
		int beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
		builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
		saltIndex  = 1;
		beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
		builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
		saltIndex  = 0;
		beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
		builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
		for (int i = 3; i < 8; i++)
		{
			saltIndex  = Math.abs(rand.nextInt() % 3 );
			beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
			builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
		}
		return builder.toString();
	}
	
	static public String normalize(String input) {
		String output = Normalizer.normalize(input, Normalizer.Form.NFD); 
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(output).replaceAll("");
	}
}