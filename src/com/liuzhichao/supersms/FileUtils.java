package com.liuzhichao.supersms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {

	
	/**
	 * 从文中读取手机号码
	 * 
	 * @param file
	 * @return
	 */
	public static ArrayList<String> readNumbers(File file) {
		ArrayList<String> numberList = new ArrayList<String>();
		FileReader in = null;
		BufferedReader br = null;
		try {
			in = new FileReader(file);
			br = new BufferedReader(in);
			String number;
			while ((number = br.readLine()) != null) {
				 if (StringUtils.isPhoneNumber(number.trim())) {
				numberList.add(number.trim());
				 }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return numberList;
	}
	
	/**
	 * 读取短信内容
	 * 
	 * @param file
	 * @return
	 */
	public static String readSMSContent(File file) {
		StringBuffer buffer = new StringBuffer();
		FileReader in = null;
		BufferedReader br = null;
		try {
			in = new FileReader(file);
			br = new BufferedReader(in);
			String str = "";
			while ((str = br.readLine()) != null) {
				buffer.append(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer.toString();
	}
	
}
