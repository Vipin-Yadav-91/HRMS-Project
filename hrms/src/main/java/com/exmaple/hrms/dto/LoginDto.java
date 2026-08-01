package com.exmaple.hrms.dto;

import org.springframework.web.bind.annotation.GetMapping;

public class LoginDto {
	private String eamiladdress;
	private String password;
	public String getEamiladdress() {
		return eamiladdress;
	}
	public void setEamiladdress(String eamiladdress) {
		this.eamiladdress = eamiladdress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@GetMapping("/user/dashboard")
	public String userDashboard() {
		return "user/dashboard";
	}

}
