 package in.chat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.google.gson.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ChatbotServlet extends HttpServlet {

    private static final String API_KEY = "4225801af58cbb95f62fe10f48517e1042e2ec10e64576382d183e7d87c0d929";
    private static final String SEARCH_ENGINE = "google";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        // Get the response from the backend
        String chatbotResponse = getSearchResults(query);

        // Set response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Send the response back to the frontend
        PrintWriter out = response.getWriter();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("response", chatbotResponse);
        out.print(responseJson.toString());
    }

    private String getSearchResults(String query) {
        try {
            // Handle specific questions with custom responses
            if (query == null) {
                return "Error: No query provided!";
            } else if (query.equalsIgnoreCase("what is your name")) {
                return "I am your friendly chatbot!";
            } else if (query.equalsIgnoreCase("where are you from")) {
                return "I am from the digital world, here to help you!";
            } else if (query.equalsIgnoreCase("are you fine")) {
                return "I'm doing great, thank you for asking!";
            }

            // Encode the query for the API request
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlString = "https://serpapi.com/search.json?q=" + encodedQuery + "&engine=" + SEARCH_ENGINE + "&api_key=" + API_KEY;

            // Make the API request and parse the result
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            // Handle errors
            if (jsonResponse.has("error")) {
                return "API Error: " + jsonResponse.get("error").getAsString();
            }

            // Check for "organic_results" and extract the snippet
            if (jsonResponse.has("organic_results")) {
                JsonArray results = jsonResponse.getAsJsonArray("organic_results");

                if (results.size() > 0) {
                    JsonObject firstResult = results.get(0).getAsJsonObject();
                    String title = firstResult.get("title").getAsString();
                    String snippet = firstResult.get("snippet").getAsString();
                    return title + ": " + snippet;
                }
            }

            return "No results found.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
