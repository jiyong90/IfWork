package com.isu.ifw.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 양방향 암호화 지원을 위한 AES 256 암호화
 * @author admin
 *
 */
public class Aes256 {

	
		public static void main (String[] args){
			Aes256 a256;
			try {
				a256 = new Aes256("thisisaencryptionkey");
				String str = a256.encrypt("특정문자열");
				System.out.println(str);
				String dstr = a256.decrypt(str);
				System.out.println(dstr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	    private String iv;
	    private Key keySpec;

	    /**
	     * 16자리의 키값을 입력하여 객체를 생성한다.
	     * @param key 암/복호화를 위한 키값
	     * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
	     */
	    public Aes256(String key) throws UnsupportedEncodingException {
	        this.iv = key.substring(0, 16);
	        byte[] keyBytes = new byte[16];
	        byte[] b = key.getBytes("UTF-8");
	        int len = b.length;
	        if(len > keyBytes.length){
	            len = keyBytes.length;
	        }
	        System.arraycopy(b, 0, keyBytes, 0, len);
	        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

	        this.keySpec = keySpec;
	    }

	    /**
	     * AES256 으로 암호화 한다.
	     * @param str 암호화할 문자열
	     * @return
	     * @throws NoSuchAlgorithmException
	     * @throws GeneralSecurityException
	     * @throws UnsupportedEncodingException
	     */
	    public String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{
	        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
	        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	        String enStr = new String(Base64.getEncoder().encode(encrypted));
	        return enStr;
	    }

	    /**
	     * AES256으로 암호화된 txt 를 복호화한다.
	     * @param str 복호화할 문자열
	     * @return
	     * @throws NoSuchAlgorithmException
	     * @throws GeneralSecurityException
	     * @throws UnsupportedEncodingException
	     */
	    public String decrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
	        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
	        byte[] byteStr = Base64.getDecoder().decode(str.getBytes());
	        return new String(c.doFinal(byteStr), "UTF-8");
	    }
	
}