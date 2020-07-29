package com.lody.virtual.helper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

/**
 * @author Lody
 *
 *
 */
public class MD5Utils {

	private static final int ROUND_8BITS = 0x100;
	private static final int ROUND_4BITS = 0x10;
	private static final int RADIX_16 = 16;
	/**
	 * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f'};
	protected static MessageDigest MESSAGE_DIGEST_5 = null;

	static {
		try {
			MESSAGE_DIGEST_5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对字符串进行md5加密处理
	 * @param string 原始字符串
	 * @return 加密后的字符串 16位
	 */
	public static String get16MD5String(String string) {
		byte[] md5 = stringToMD5(string);
		return to16HexString(md5);
	}

	/**
	 * 对字符串进行md5加密处理
	 * @param string 原始字符串
	 * @return 加密后的字符串 32位
	 */
	public static String get32MD5String(String string) {
		byte[] md5 = stringToMD5(string);
		return to32HexString(md5);
	}

	private static byte[] stringToMD5(String string) {
		if (string == null) {
			return null;
		}
		try {
			MESSAGE_DIGEST_5.update(string.getBytes());
			return MESSAGE_DIGEST_5.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String to16HexString(byte[] md5) {
		if (md5 == null) {
			return null;
		}

		final int beginConvertIndex = 4;
		final int endConvertIndex = 11;
		final int intValueFF = 0xFF;
		StringBuilder stringBuffer = new StringBuilder();
		for (int i = beginConvertIndex; i <= endConvertIndex; ++i) {
			stringBuffer.append(Integer.toHexString(intValueFF & md5[i]));
		}
		return stringBuffer.toString();
	}

	private static String to32HexString(byte[] md5) {
		if (md5 == null) {
			return null;
		}

		int val;
		StringBuilder stringBuffer = new StringBuilder("");
		for (byte b : md5) {
			val = b;
			if (val < 0) {
				val += ROUND_8BITS;
			}
			if (val < ROUND_4BITS) {
				stringBuffer.append("0");
			}
			stringBuffer.append(Integer.toHexString(val));
		}

		return stringBuffer.toString();
	}

	public static String getFileMD5String(File file) throws IOException {
		InputStream fis;
		fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int numRead;
		while ((numRead = fis.read(buffer)) > 0) {
			MESSAGE_DIGEST_5.update(buffer, 0, numRead);
		}
		fis.close();
		return bufferToHex(MESSAGE_DIGEST_5.digest());
	}

	public static String getFileMD5String(InputStream in) throws IOException {
		byte[] buffer = new byte[1024];
		int numRead;
		while ((numRead = in.read(buffer)) > 0) {
			MESSAGE_DIGEST_5.update(buffer, 0, numRead);
		}
		in.close();
		return bufferToHex(MESSAGE_DIGEST_5.digest());
	}
	private static String bufferToHex(byte[] bytes) {
		return bufferToHex(bytes, 0, bytes.length);
	}
	private static String bufferToHex(byte[] bytes, int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = HEX_DIGITS[(bt & 0xf0) >> 4];
		char c1 = HEX_DIGITS[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static boolean compareFiles(File one, File two) throws IOException {

		if (one.getAbsolutePath().equals(two.getAbsolutePath())) {
			// 是同一个文件
			return true;
		}
		String md5_1 = getFileMD5String(one);
		String md5_2 = getFileMD5String(two);
		return TextUtils.equals(md5_1, md5_2);
	}

}
