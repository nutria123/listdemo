package list.pack;

import java.io.Serializable;


	public class ImageAndText implements Serializable{
	     /**
		 * 
		 */
		private static final long serialVersionUID = 8581388373289787867L;
		private String imageUrl;
	     private String text;
	     private String albumurl;

	     public ImageAndText(String imageUrl, String text,String albumurl) {
	         this.imageUrl = imageUrl;
	         this.text = text;
	         this.albumurl=albumurl;
	     }
	     public ImageAndText(String imageUrl, String text) {
	         this.imageUrl = imageUrl;
	         this.text = text;
	     }	     
	     public String getImageUrl() {
	         return imageUrl;
	     }
	     public String getText() {
	         return text;
	     }
	     public String getAlbumurl() {
	         return albumurl;
	     }	     
	}

