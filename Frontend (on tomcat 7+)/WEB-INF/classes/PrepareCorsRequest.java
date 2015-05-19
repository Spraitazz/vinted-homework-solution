package main;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/PrepareCorsRequest")
public class PrepareCorsRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    public PrepareCorsRequest() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL dribbble = new URL("https://dribbble.com/oauth/token");
		HttpURLConnection connection = (HttpURLConnection) dribbble.openConnection();
		String code = request.getParameter("code");
		connection.setDoInput(true);
		connection.setDoOutput(true);			
		connection.setRequestMethod("POST");		
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");	
		DataOutputStream connectionWriter  = new DataOutputStream(connection.getOutputStream());
		connectionWriter.writeBytes("client_id=5d13135275ba32e64efb20f18bb1d99e8aaf3a2bccbb06818ba12974c21ceb22&client_secret=b50ee2bf1202dca5da419f656cf3bc40b63fefec2767c4a886e09137026a6a6e&code="+code);
		connectionWriter.flush();
		connectionWriter.close();
		connection.connect();		
		BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String readString = "";
		StringBuffer responseBuffer = new StringBuffer();
		while((readString=connectionInput.readLine())!=null){
			responseBuffer.append(readString);
		}
		connection.disconnect();
		response.getWriter().print(responseBuffer.toString());	
	}	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}