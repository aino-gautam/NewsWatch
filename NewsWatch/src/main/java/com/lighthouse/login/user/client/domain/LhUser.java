package com.lighthouse.login.user.client.domain;

import com.login.client.UserInformation;

public class LhUser extends UserInformation{

	/**
	 * 
	 */
	private static final long serialVersionUID = 257539213881837905L;
	
	private LhUserPermission userPermission;

	public LhUserPermission getUserPermission() {
		return userPermission;
	}

	public void setUserPermission(LhUserPermission userPermission) {
		this.userPermission = userPermission;
	}
	
}
