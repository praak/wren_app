
package io.backgroundtask.wren;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Praak on 2/10/17.
 */

public class HttpManager {

    private final static String USER_AGENT = "Mozilla/5.0";

    public static String getData(String uri) {

        // BufferedReader reader = null;
        //
        // try {
        // URL url = new URL(uri);
        // HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        //
        // StringBuilder stringBuilder = new StringBuilder();
        // reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
        //
        // String line;
        //
        // while ((line = reader.readLine()) != null) {
        // stringBuilder.append(line + "\n");
        // }
        //
        // return stringBuilder.toString();
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // return null;
        // } finally {
        // if (reader != null) {
        // try {
        // reader.close();
        // } catch (Exception e) {
        // e.printStackTrace();
        // return null;
        // }
        // }
        // }

        // String url = "http://www.google.com/search?q=mkyong";

        String url = uri;
        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        try {
            int responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("\nSending 'GET' request to URL : " + url);
        // System.out.println("Response Code : " + responseCode);

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // print result
        // System.out.println(response.toString());

        return response.toString();
    }
}
