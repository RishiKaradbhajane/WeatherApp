package Mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		//API Setup
		String apiKey="d07a7afbacb70442f990322bf0c113b0";
		//get the data from the city
		String city = request.getParameter("city");
		
		//Creating the URL for the OpenWeather API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		
		//URL integration
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection .setRequestMethod("GET");
		
		//to read data from the network
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		//want to store in stringBuilder
		StringBuilder responseContent = new StringBuilder();
		
		//to take input from the reader, wi'll create scanner
		Scanner scanner = new Scanner(reader);
		
		while(scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
		}
		scanner.close();
		System.out.println(responseContent);
		
		//TypeCasting or parsing data from String to JSON
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(),JsonObject.class);
		
		//Date and Time
		long dateTimeStamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new java.util.Date(dateTimeStamp).toString();
		
		//Temperature
		double tempKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int tempCelsius = (int)(tempKelvin - 273.15);
		
		//Humidity
		int humid = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		
		//wind Speed
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//weather condition
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

		//set the data as request attribures (for sending to the jsp)
		request.setAttribute("date",date);
		request.setAttribute("city",city);
		request.setAttribute("temperature",tempCelsius);
		request.setAttribute("weatherCondition",weatherCondition);
		request.setAttribute("humidity",humid);
		request.setAttribute("windSpeed",windSpeed);
		request.setAttribute("weatherData",responseContent.toString());
		
		//to disconnect with API
		connection.disconnect();
		
		//forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
		
		}catch(IOException e) {
			e.printStackTrace();
		}
	} 

}
