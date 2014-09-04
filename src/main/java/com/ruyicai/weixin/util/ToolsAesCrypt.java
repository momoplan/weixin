package com.ruyicai.weixin.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.weixin.consts.Const;

public class ToolsAesCrypt {

	private static Logger logger = LoggerFactory.getLogger(ToolsAesCrypt.class);
	
	/**
	 * 解密算法
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String Decrypt(String sSrc, String sKey) {
		try {
			byte[] raw = sKey.getBytes("GBK");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc.getBytes());
			try {
				byte[] original = cipher.doFinal(encrypted1);
				return new String(original);
			} catch (Exception e) {
				logger.error("解密异常1", e);
				return null;
			}
		} catch (Exception ex) {
			logger.error("解密异常2", ex);
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] decryptkjava(byte[] bytes) {
		int len = bytes.length;
		for (int i = 0; i < len; i++) {
			bytes[i] = (byte) (bytes[i] ^ 0x6e);
		}
		return bytes;
	}

	/**
	 * 加密
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String Encrypt(String sSrc, String sKey) {
		try
		{
			byte[] raw = sKey.getBytes("GBK");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes());
			return byte2hex(encrypted).toLowerCase();
		} catch (Exception e)
		{
			logger.error("加密异常", e);
			return null;
		}
		
	}

	/**
	 * 加密
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] ecryptkjava(byte[] bytes) {
		int len = bytes.length;
		for (int i = 0; i < len; i++) {
			bytes[i] = (byte) (bytes[i] ^ 0x6e);
		}
		return bytes;
	}

	/**
	 * 十六进制转化成byte数组
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	/**
	 * byte数组转化十六进制
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static byte[] encrypt2Bytes(byte[] src, String sKey) {
		try {
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(src);
			return encrypted;
		} catch (Exception e) {
			logger.error("encrypt2Bytes error", e);
			return null;
		}
	}

	public static byte[] decrypt2Bytes(byte[] src, String sKey)
			throws Exception {
		try {
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = src;
			try {
				byte[] original = cipher.doFinal(encrypted1);
				return original;
			} catch (Exception e) {
				logger.error("decrypt2Bytes error1", e);
				return null;
			}
		} catch (Exception ex) {
			logger.error("decrypt2Bytes error2", ex);
			return null;
		}
	}

	public static void main(String[] args) {
		String miwen = "abc062506210b249d33bf3a4d1bcb6c6";
		try {
//			System.out.println("加密：" + Encrypt(miwen, Const.PACKET_KEY));
			System.out.println("解密：" + Decrypt(miwen, Const.PACKET_KEY));
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
}
