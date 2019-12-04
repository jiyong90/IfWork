package com.isu.ifo.handler;

//@Component
public class CustomAuthenticationManager { /*implements AuthenticationManager{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		System.out.println(" CustomAuthenticationManager ::: ");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials() , authorities);
		System.out.println("authentication.toString() : " + authentication.toString());
		// TODO Auto-generated method stub
		return auth;
	}*/
	
}
