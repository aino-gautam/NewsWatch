package com.lighthouse.utils.server;

import java.net.URLEncoder;
import java.net.URLDecoder;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncrypterDecrypter {
	static EncrypterDecrypter encrypterDecrypter = new EncrypterDecrypter();

	public static EncrypterDecrypter getInstance() {
		return encrypterDecrypter;
	}

	final String DES_ENCRYPTION_SCHEME = "DES";
	final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	final String ENCRYPTIONKEY = "LightHouseEncryptionKey";
	final String UNICODE_FORMAT = "UTF8";
	final String ENCRYPTIONSCHEME = DES_ENCRYPTION_SCHEME;

	private EncrypterDecrypter() {

	}

	public String getDecryptedString(String stringToDecrypt) throws Exception {
		try {
			//Hex hex = new Hex();
			//stringToDecrypt = getUrlDecodedString(stringToDecrypt);
			//stringToDecrypt= hex.decode(stringToDecrypt).toString();
			byte[] keyAsBytes = ENCRYPTIONKEY.getBytes(UNICODE_FORMAT);
			KeySpec myKeySpec = new DESKeySpec(keyAsBytes);
			SecretKeyFactory mySecretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTIONSCHEME);

			Cipher cipher = Cipher.getInstance(ENCRYPTIONSCHEME);
			SecretKey key = mySecretKeyFactory.generateSecret(myKeySpec);
			cipher.init(Cipher.DECRYPT_MODE, key);

			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] encryptedText = base64decoder.decodeBuffer(stringToDecrypt);
			byte[] plainText = cipher.doFinal(encryptedText);

			String decryptedText = new String(plainText);
			return decryptedText;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public String getEncryptedString(String stringToEncrypt) throws Exception {
		
		try {
			byte[] keyAsBytes = ENCRYPTIONKEY.getBytes(UNICODE_FORMAT);
			KeySpec myKeySpec = new DESKeySpec(keyAsBytes);

			SecretKeyFactory mySecretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTIONSCHEME);
			Cipher cipher = Cipher.getInstance(ENCRYPTIONSCHEME);
			SecretKey key = mySecretKeyFactory.generateSecret(myKeySpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = stringToEncrypt.getBytes(UNICODE_FORMAT);
			byte[] encryptedText = cipher.doFinal(plainText);

			BASE64Encoder base64encoder = new BASE64Encoder();
			String encryptedString = base64encoder.encode(encryptedText);
			//encryptedString = getUrlEncodedString(encryptedString);
			//Hex hex = new Hex();
			//encryptedString = hex.encode(encryptedString).toString();
			return encryptedString;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public String getHMACEncodedSignature(String stringToEncode, String key) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(stringToEncode.getBytes());
		String signature = new String(Base64.encodeBase64(rawHmac));

		return signature;
	}

	public String getUrlEncodedString(String stringToUrlEncode) throws Exception {
		return URLEncoder.encode(stringToUrlEncode, UNICODE_FORMAT);
	}
	
	public String getUrlDecodedString(String stringToUrlDecode) throws Exception {
		return URLDecoder.decode(stringToUrlDecode, UNICODE_FORMAT);
	}
}

