package com.imservices.im.bmm.utils;


import java.util.regex.Pattern;

public class ValidateUtil
{
  private static ValidateUtil instance = null;
  public static final String regexPhonenumb = "^1[3-9][0-9]{9}$";
  public static final String regexEmail = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
  public static final String regexAccount = "^[a-zA-Z_][a-zA-Z0-9]{5,15}$";
  public static final String regexPwd = "^[a-zA-Z_][a-zA-Z0-9]{5,15}$";
  public static final String allNumber = "^[0-9]{1,15}$";
  private Pattern pPhonenumb = Pattern.compile("^1[3-9][0-9]{9}$");
  private Pattern pEmail = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
  private Pattern pAccount = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9]{5,15}$");
  private Pattern pPwd = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9]{5,15}$");
  
  public static ValidateUtil instance()
  {
    if (instance == null) {
      instance = new ValidateUtil();
    }
    return instance;
  }
  
  public boolean validatePhone(String value)
  {
    return this.pPhonenumb.matcher(value).matches();
  }
  
  public boolean validateEmail(String value)
  {
    return this.pEmail.matcher(value).matches();
  }
  
  public boolean validateAccount(String value)
  {
    return this.pAccount.matcher(value).matches();
  }
  
  public boolean isChinese(String value)
  {
    for (int i = 0; i < value.length(); i++)
    {
      int c = value.charAt(i);
      if ((c < 19968) || (c > 171941)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean validatePwd(String value)
  {
    return this.pPwd.matcher(value).matches();
  }
}
