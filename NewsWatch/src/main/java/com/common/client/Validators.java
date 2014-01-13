package com.common.client;

import com.common.client.ValidationException;

public class Validators
{
	public boolean firstnameValidator(String firstname) throws ValidationException
	{
		if(!firstname.matches("[a-zA-Z\\s]+") || firstname.equals("")){
			throw new ValidationException("Invalid First Name");
		}
		return true;
	}

	public boolean lastnameValidator(String lastname) throws ValidationException{
		if(!lastname.matches("[a-zA-Z\\s]+") || lastname.equals("")){
			throw new ValidationException("Invalid Last Name");
		}
		return true;
	}

	public boolean phonenoValidator(String text) throws ValidationException{
		if(!text.matches("[0-9]+")){ //([+-]?\\d*\\.\\d+)(?![-+0-9\\.])
			throw new ValidationException("Invalid Phone Number");
		}
		else if(text.equals("")){
			throw new ValidationException("Phone number is mandatory");
		}
		return true;
	}

	public boolean companyValidator(String companynm) throws ValidationException{
		if(companynm.equals("")){
			throw new ValidationException("Invalid company name");
		}
		return true;
	}

	public boolean titleValidator(String title) throws ValidationException{
		if(title.matches("[0-9]+")){
			throw new ValidationException("Invalid title");
		}
		return true;
	}

	public boolean emailValidator(String email) throws ValidationException{
		if(!email.matches(("[\\w-+]+(?:\\.[\\w-+]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}")) || email.equals("")){
			throw new ValidationException("Invalid email address");
		}
		return true;
	}
	
	public boolean passwordValidator(String pass) throws ValidationException{
		if(pass.equals("")){
			throw new ValidationException("Please Enter a password");
		}
		else if(pass.length()<6){
			throw new ValidationException("Password should be minimum 6 characters long");
		}
		else if(pass.length()>16){
			throw new ValidationException("Password should be less than 16 characters");
		}
		return true;
	}

	public boolean blankfield()throws ValidationException{
		throw new ValidationException("Please Enter the blank field");
	}

	public boolean wrongpassword()throws ValidationException{
		throw new ValidationException("Your new and confirm password does not match");
	}
}