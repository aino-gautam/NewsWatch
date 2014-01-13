package com.lighthouse.report.client.ui;

import com.google.gwt.user.client.ui.Image;

public class ImageDownload {


		private Image image;
		private String id;

		public ImageDownload(Image img , String reportId) {
		//	this.image = new Image("images/downloadimg.png");
			this.image = img;
			image.setStylePrimaryName("clickable");
			image.setTitle("Download report");
			this.id = reportId;
		}
		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

}
