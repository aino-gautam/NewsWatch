package com.appUtils.server.helper;

import java.io.File;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.newscenter.server.db.DBHelper;

public class AppUtilsHelper extends DBHelper {
	Logger logger = Logger.getLogger(AppUtilsHelper.class.getName());

	public void removeFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.removeAttribute("userInfo");
	}

	public boolean alreadyExists(String pathOfFile) {
		File file = new File(pathOfFile);
		if (file.exists())
			if (!file.isDirectory())
				return true;
			else
				return false;
		else
			return false;
	}
	
	public boolean isDirExists(String pathOfFile) {
		File file = new File(pathOfFile);
		if (file.exists())
			if (file.isDirectory())
				return true;
			else
				return false;
		else
			return false;
	}
}
