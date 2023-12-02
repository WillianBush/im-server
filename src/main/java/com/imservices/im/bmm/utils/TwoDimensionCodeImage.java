package com.imservices.im.bmm.utils;


import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

// TwoDimensionCodeImage 类：二维码图片对象
public class TwoDimensionCodeImage implements QRCodeImage {

	 	BufferedImage bufImg;  
     
	    public TwoDimensionCodeImage(BufferedImage bufImg) {  
	        this.bufImg = bufImg;  
	    }  
	      
	    public int getHeight() {  
	        return bufImg.getHeight();  
	    }  
	  
	    public int getPixel(int x, int y) {  
	        return bufImg.getRGB(x, y);  
	    }  
	  
	    public int getWidth() {  
	        return bufImg.getWidth();  
	    }  
	
}
