package controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;

public class ImageDownloader {
	

	private static String _filePath = "files" + System.getProperty("file.separator") + "newCreation" 
	+ System.getProperty("file.separator") + "allImages";
	
	
	public static String getAPIKey(String key) throws Exception {
		// TODO fix the following based on where you will have your config file stored

		String config = System.getProperty("user.dir") 
				+ System.getProperty("file.separator")+ "resources" + System.getProperty("file.separator") + "flickr-api-keys.txt"; 
		

		File file = new File(config); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key)) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());
	}

	public static void getImages (String searchTerm, int numImages) throws Exception {
			String apiKey = getAPIKey("apiKey");
			String sharedSecret = getAPIKey("sharedSecret");

			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
			
			String query = searchTerm;
			int resultsPerPage = numImages;
			int page = 0;
			
	        PhotosInterface photos = flickr.getPhotosInterface();
	        SearchParameters params = new SearchParameters();
	        params.setSort(SearchParameters.RELEVANCE);
	        params.setMedia("photos"); 
	        params.setText(query);
	        
	        PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
	        
	        int i =0;
	        for (Photo photo: results) {
	        	
	        		BufferedImage image = photos.getImage(photo,Size.LARGE);
//		        	String filename = query.trim().replace(' ', '-')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
	        		String filename = i + ".jpg";
		        	File outputfile = new File(_filePath, filename);
		        	ImageIO.write(image, "jpg", outputfile);

		        	i++;
	        }
	}
}

