package com.login.client;

public class Validators{
	public boolean firstnameValidator(String firstname) throws ValidationException{
		if(firstname.equals("")){
			throw new ValidationException("Please enter a  first name");
		}
		else if(!firstname.matches("[a-zA-Z\\s]+")){
			throw new ValidationException("Only alphabets allowed");
		}
		return true;
	}

	public boolean lastnameValidator(String lastname) throws ValidationException{
		if(!lastname.matches("[a-zA-Z\\s]+")){
			throw new ValidationException("Only alphabets allowed");
		}
		else if(lastname.equals("")){
			throw new ValidationException("Please enter a last name");
		}
		return true;
	}

	public boolean phonenoValidator(String text) throws ValidationException{
		if(!text.matches("[0-9]+")){ //([+-]?\\d*\\.\\d+)(?![-+0-9\\.])
			throw new ValidationException("Invalid Phone Number");
		}
		else if(text.equals("")){
			throw new ValidationException("Please enter a phone number");
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
}