package com.admin.client;

import java.io.Serializable;

import com.common.client.PopUpForForgotPassword;

public class DateValidator{
	String dateString;
	private static final long serialVersionUID = 1L;

	public boolean validate(Serializable data){
		dateString=(String)data;
		String dateFormat = "yyyy-mm-dd";
		int dateFormatLength = dateFormat.length();

		if (dateString.length() != dateFormatLength){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("The date should be in yyyy-mm-dd format");
			popup.setPopupPosition(600, 300);
			popup.show();
			ManageNewsItems.flag = false;
			EditNewsItems.flag = false;
			return false;
		}
		else{
			try{
				if (dateString.length() != dateFormatLength){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The date should be in yyyy-mm-dd format");
					popup.setPopupPosition(600, 300);
					popup.show();
					return false;
				}
				else{
					String spilttedDateArray[]=dateString.split("-");
					int yyyy=Integer.parseInt(spilttedDateArray[0]);
					int mm=Integer.parseInt(spilttedDateArray[1]);
					int dd=Integer.parseInt(spilttedDateArray[2]);
					if(mm > 0 && mm < 13){
						int chk=0;
						if(mm == 02)
						{
							if((yyyy % 4 == 0) && ((!(yyyy % 100 == 0)) || (yyyy % 400 == 0)))//checking if the year is a leap year
								chk=29;
							else
								chk=28;
							if(dd > 0 && dd > chk){
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Invalid Date");
								popup.setPopupPosition(600, 300);
								popup.show();
								return false;
							}
						}
						else 
						{
							if (mm==4 || mm==6 || mm==9 || mm==11) //months having 30 days
								chk = 30;
							else
								chk=31;
							if(dd < 0 || dd > chk)
							{
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Invalid Date");
								popup.setPopupPosition(600, 300);
								popup.show();
								return false;
							}
						}
					}
					else 
					{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Invalid Date");
						popup.setPopupPosition(600, 300);
						popup.show();
						return false;
					}
				}
			}
			catch(Exception e)
			{
				PopUpForForgotPassword popup = new PopUpForForgotPassword("The date should be in yyyy-mm-dd format");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
		}
		return true;
	}
	
	
	
	
}
