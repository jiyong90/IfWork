package com.isu.ifw.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * SHA 256 단방향 암호화 클래스
 * @author admin
 *
 */
public class Sha256 {

	public static void main(String[] args){
		try {
			System.out.println(getHash("1234","mgate_dev",10));
			//System.out.println(getHash("1234","1111",1));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 암호화 된 데이터를 얻는다.
	 * 
	 * @param password
	 * @param strSalt
	 * @param ITERATION_NUMBER
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String getHash(String password, String strSalt, int ITERATION_NUMBER) throws NoSuchAlgorithmException, IOException {

		/* ----------------------------------------------------------------------
		 * 1차) salt를 사용하지 않는 SHA-256(SHA2)는 이 소스만으로 가능
		 * ---------------------------------------------------------------------- */
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();

		/* ----------------------------------------------------------------------
		 * 2차) 패스워드의 복잡도를 임의로 높이기 위해 salt라는 dummy값(64bit long 난수) 추가 
		 *     ( 원 패스워드가 복잡해져도 최종 변환된 길이는 항상 동일 )
		 *     권장사항 : salt값을 12자리 이상으로 하고, 이것 또한 암호화해서 저장할 것.
		 * ---------------------------------------------------------------------- */
		byte[] salt = null;
		
		if(strSalt != null)
			strSalt = strSalt.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
		
		salt = base64ToByte(strSalt);
		
		digest.update(salt);

		/* ----------------------------------------------------------------------
		 * 3차) 변환 작업 수행
		 * ---------------------------------------------------------------------- */
		byte[] input = null;
		input = digest.digest(password.getBytes("UTF-8"));

		/* ----------------------------------------------------------------------
		 * 4차) 이 정도로도 안심이 되지 않아, 최소 digest를 1000번 이상 반복할 것을 권장.
		 *      반복횟수를 1000 이상의 숫자로 변수로 저장하여 반복 수행함.
		 *      https://www.owasp.org/index.php/Hashing_Java
		 *      A minimum of 1000 operations is recommended in RSA PKCS5 standard.
		 *      The stored password looks like this :
		 *           Hash(hash(hash(hash(……….hash(password||salt)))))))))))))))
		 * ---------------------------------------------------------------------- */
		for(int i=0; i<ITERATION_NUMBER;i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return byteToBase64(input);
	}
	
	/**
	 * 암호화 키 생성의 보안성 강화를 위한 임의 바이트 반환
	 * @param strTmp
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String getNewSalt(String strTmp) throws NoSuchAlgorithmException, IOException {

		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");  // Uses a secure Random not a simple Random
		byte[] bSalt = new byte[8]; // Salt generation 64 bits long
		random.nextBytes(bSalt);
		
		return byteToBase64(bSalt) ;
	}
	
	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException {
		
		Decoder decoder = Base64.getDecoder();
		return decoder.decode(data);
	}
	
	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) throws IOException {
		Encoder encoder = Base64.getEncoder();
		return new String(encoder.encode(data));
	}
}