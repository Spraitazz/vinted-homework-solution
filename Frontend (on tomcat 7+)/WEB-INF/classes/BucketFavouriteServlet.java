package main;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
@WebServlet("/BucketFavouriteServlet")
public class BucketFavouriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L; 
    public BucketFavouriteServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String favouriteBucketId = request.getParameter("bucket_id");
		String shotId = request.getParameter("shot_id");
	    String accessToken = request.getParameter("access_token");
	    JSONObject jo = new JSONObject();	    
	    jo.put("access_token", accessToken);
	    jo.put("shot_id", Integer.valueOf(shotId));
		URL favouriteBucket = new URL("https://api.dribbble.com/v1/buckets/"+favouriteBucketId+"/shots");
		HttpURLConnection connection = (HttpURLConnection) favouriteBucket.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);			
		connection.setRequestMethod("PUT");		
		connection.setRequestProperty("Content-Type", "application/json");
		DataOutputStream connectionWriter  = new DataOutputStream(connection.getOutputStream());		
		connectionWriter.writeBytes(jo.toString());
		connectionWriter.flush();
		connectionWriter.close();
		connection.connect();
		System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());

	}	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}