package com.isu.ifw.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtil {

	//static String signKey = "h0nda20200401k0readziSuinTerFace";
	static String SIGNATURE_ALGORITHM = "HmacSHA256";
	
	private static final List<Integer> REPLACE_IDX  = Arrays.asList(0,1,4,7,10,14,21,44,77,100);
	
	//static Long expiredTime = 60000l;
	
	public static String createJWT(String signKey, String id, String issuer, String subject, Map<String, Object> payloads, long ttlMillis) {
  	  
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(signKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setClaims(payloads)
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
      
        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }  
      
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
        
    }
	
	
	public static Claims decodeJWT(String signKey, String jwt) {
	    //This line will throw an exception if it is not a signed JWS (as expected)
	    Claims claims = Jwts.parser()
	            .setSigningKey(DatatypeConverter.parseBase64Binary(signKey))
	            .parseClaimsJws(jwt).getBody();
	    return claims;
	}
	
	/**
	 * REPLACE_IDX 배열에 등록된 자릿수 및 숫자와 특수문자를 제외 한
	 * 문자의 대소문자를 바꾼다 
	 * @param token
	 * @return
	 */
	public static String replaceJWT(String token) {

		String replaceToken = "";
		System.out.println("token : " + token);
		for(int idx=0; idx < token.length(); idx++) {
			System.out.print(token.charAt(idx));
			char tmp = token.charAt(idx);
			
			if(REPLACE_IDX.indexOf(idx) != -1) {
				replaceToken = replaceToken + tmp;
			}else {
				if(Character.isUpperCase(tmp)) {
					replaceToken = replaceToken + Character.toLowerCase(tmp);
				}else if(Character.isLowerCase(tmp)){
					replaceToken = replaceToken + Character.toUpperCase(tmp);
				}else {
					replaceToken = replaceToken + tmp;
				}
			}
		}
		System.out.println("token : " + replaceToken);
		return replaceToken;
	}
	
	public static String createReplaceJWT(String signKey, String id, String issuer, String subject, Map<String, Object> payloads, long ttlMillis) {
	  	  
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(signKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setClaims(payloads)
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
      
        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }  
        return replaceJWT(builder.compact());
        
    }
	
	public static Claims decodeReplaceJWT(String signKey, String jwt) {
	    //This line will throw an exception if it is not a signed JWS (as expected)
		String replaceToken = replaceJWT(jwt);
	    Claims claims = Jwts.parser()
	            .setSigningKey(DatatypeConverter.parseBase64Binary(signKey))
	            .parseClaimsJws(replaceToken).getBody();
	    return claims;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		Map<String, Object> payloads = new HashMap<String, Object>();
		payloads.put("CD_COMPANY", "BRS");
		payloads.put("user_name", "38@BRS@352134");
		String jwt = createReplaceJWT("br0se20200401k0reahrWtminTerFace", "id","issuer","subject", payloads,60000l);
		System.out.println(jwt);
		System.out.println("ssss");
		System.out.println(decodeReplaceJWT("br0se20200401k0reahrWtminTerFace", jwt).toString());
	}
	
	 
	
}
 
