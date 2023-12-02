package com.imservices.im.bmm.utils;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupMethod
{

	public static final String Data_Chain_Separate_For_Group = "!@#&";
	public static final String Data_Chain_Separate = ",";

	public GroupMethod()
	{
	}

	public static String[] group(String patten, String matcherSource, int DataChainForGroupLen)
	{
		String rs = "";
		if (patten == null || "".equals(patten.trim()))
			return null;
		Pattern p = Pattern.compile(patten, 2);
		for (Matcher m = p.matcher(matcherSource); m.find();)
		{
			for (int i = 1; i <= DataChainForGroupLen; i++)
				try
				{
					String str = m.group(i);
					str = str.replaceAll(",", " ");
					if (i == 1)
						rs = (new StringBuilder(String.valueOf(rs))).append(str).toString();
					else
						rs = (new StringBuilder(String.valueOf(rs))).append("!@#&").append(str).toString();
				}
				catch (Exception exception) { }

			rs = (new StringBuilder(String.valueOf(rs))).append(",").toString();
		}

		if ("".equals(rs))
			return null;
		else
			return rs.substring(0, rs.length() - 1).split(",");
	}
}
