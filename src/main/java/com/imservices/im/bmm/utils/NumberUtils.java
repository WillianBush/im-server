package com.imservices.im.bmm.utils;

import java.util.Random;

public class NumberUtils {
	
	public static String generateRandomNumber(int n){
	    String randomNumString ="";
	    Random r = new Random();
	    //Generate the first digit from 1-9
	    randomNumString += (r.nextInt(9) + 1);
	    //Generate the remaining digits between 0-9
	    for(int x = 1; x < n; x++){
	        randomNumString += r.nextInt(9);
	    }
	    //Parse and return
	    return randomNumString;
	}
	
}
