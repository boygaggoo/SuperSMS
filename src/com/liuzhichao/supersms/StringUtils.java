package com.liuzhichao.supersms;

import java.util.regex.Pattern;

public class StringUtils {

	
	//判断字符串是否为空
	public static boolean isNullOrEmpty(String str){
		return null==str || "".equals(str);
	}
	
	/**
	 * 判断是否为手机号
	 * 
	 * @param number
	 * @return 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 *         联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
	 */
	public static boolean isPhoneNumber(String number) {
		String phoneEx = "[1]{1}[3,5,8]{1}[0-9]{9}"; // 手机号码，以1开始，13,15,18,为合法，后跟9位数字
		return Pattern.matches(phoneEx, number);
	}
}
