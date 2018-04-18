package com.tantum.app.tantum.helper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.buf.HexUtils;
import org.hashids.Hashids;

public class TokenHelper {

	private static final String initVector = "xS5Qm39GG@MWFa86"; // 16 bytes IV
	private static final String key = "p0fp#Tn5y8732O!L";
	private static final Hashids hash = new Hashids(key);

	public static synchronized boolean validateToken(String token) {
		try {
			String Key = "Something";
			byte[] KeyData = Key.getBytes();
			SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");
			Cipher cipher;
			cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, KS);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static String encrypt(String value) {
		try {
			/* Derive the key, given password and salt. */
			// SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			// KeySpec spec = new PBEKeySpec(password.toCharArray(), salt2, 65536, 256);
			// SecretKey tmp = factory.generateSecret(spec);
			// SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

			/* Encrypt the message. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			for (byte b : encrypted) {
				System.out.print(Integer.toHexString(b));
			}
			System.out.println("####");

			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String decrypt(String encrypted) {
		try {
			// /* Derive the key, given password and salt. */
			// SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			// KeySpec spec = new PBEKeySpec(password.toCharArray(), salt2, 65536, 256);
			// SecretKey tmp = factory.generateSecret(spec);
			// SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			/* Decrypt the message, given derived key and initialization vector. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;

	}

	public static synchronized String encode(String plainMessage) {
		return hash.encodeHex(HexUtils.toHexString(plainMessage.getBytes()));
	}

	public static synchronized String decode(String hashedMessage) {
		return new String(HexUtils.fromHexString(hash.decodeHex(hashedMessage)));
	}

	public static void main(String[] args) {
		System.out.println(decode(encode("senha")));
	}

}
