package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Controller
@SuppressWarnings("all")
@CrossOrigin
public class SecurityCodeController extends HttpServlet
{

	private static final String SECURITY_CODE_SESSION = "SECURITY_CODE_SESSION";
	@RequestMapping(value = "/securityCodeImages",method={RequestMethod.GET})
	public String createImages(HttpServletRequest request,HttpServletResponse response) {
		try
		{
			//
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0L);
			int width = 60;
			int height = 20;
			BufferedImage image = new BufferedImage(width, height, 1);
			Graphics g = image.getGraphics();
			Random random = new Random();
			g.setColor(getRandColor(200, 250));
			g.fillRect(0, 0, width, height);
			g.setFont(new Font("Times New Roman", 0, 18));
			g.setColor(getRandColor(160, 200));
			for (int i = 0; i < 155; i++)
			{
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				int xl = random.nextInt(12);
				int yl = random.nextInt(12);
				g.drawLine(x, y, x + xl, y + yl);
			}

			String sRand = "";
			for (int i = 0; i < 4; i++)
			{
				String rand = String.valueOf(random.nextInt(10));
				sRand = (new StringBuilder(String.valueOf(sRand))).append(rand).toString();
				g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
				g.drawString(rand, 13 * i + 6, 16);
			}

			request.getSession().setAttribute("SECURITY_CODE_SESSION", sRand);
			g.dispose();
			ImageIO.write(image, "JPEG", response.getOutputStream());
			response.getWriter().flush();
		}
		catch (Exception exception) { }
		return null;
	}

	private Color getRandColor(int fc, int bc)
	{
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
	
	
	@RequestMapping(value = "/validateResponse",method = {RequestMethod.GET})
	public String validateResponse(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String ccode = request.getParameter("ccode");
		if(StringUtils.isEmpty(ccode)) {
			ResponseUtils.json(response, ResponseUtils.STATUS.error, "验证码不能为空",null);
		}

		ResponseUtils.json(response, ResponseUtils.STATUS.success, null,null);
//		if(validateResponse(ccode,request)) {
//			ResponseUtils.json(response, ResponseUtils.STATUS.success, null,null);
//		} else {
//			ResponseUtils.json(response, ResponseUtils.STATUS.error, "验证码错误",null);
//		}
//
		return null;
	}
	

	public static Boolean validateResponse(String ccode,HttpServletRequest request)
	{
		String SECURITY_CODE = (String)request.getSession().getAttribute("SECURITY_CODE_SESSION");
		if (SECURITY_CODE == null || "".equals(SECURITY_CODE.trim()))
			return Boolean.valueOf(false);
		if (ccode.toLowerCase().equals(SECURITY_CODE.toLowerCase()))
			return Boolean.valueOf(true);
		else
			return Boolean.valueOf(false);
	}
}
