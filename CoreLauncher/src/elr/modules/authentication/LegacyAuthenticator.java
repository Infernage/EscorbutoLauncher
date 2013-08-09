package elr.modules.authentication;

import elr.core.util.Util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Second method to authenticate in Minecraft.
 * @author Infernage
 */
final class LegacyAuthenticator {
    
    /**
     * Authenticates in Minecraft.net.
     * @param username The username.
     * @param password The password.
     * @return The response.
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    static LegacyResponse login(String username, String password) throws UnsupportedEncodingException,
            IOException{
        Map parameters = new HashMap();
        parameters.put("user", username);
        parameters.put("password", password);
        parameters.put("version", Integer.valueOf(14));
        String postResult = doPost(buildQuery(parameters)).trim();
        if (postResult == null) throw new RuntimeException("Can't connect to minecraft.net");
        String[] response = postResult.split(":");
        /*
         * [0]: Serial number. Not used in authenticator.
         * <li>[1]: Status String. Not used in authenticator.
         * <li>[2]: Username String.
         * <li>[3]: Session token number.
         * <li>[4]: Session ID String.</ul>
         */
        return new LegacyResponse(response[2], response[3], response[4], response[0]);
    }
    
    private static String doPost(String query) throws MalformedURLException, IOException{
        HttpURLConnection connection = (HttpURLConnection) new URL(Util.LEGACYAUTHENTICATION_URL)
                .openConnection();
        byte[] paramAsBytes = query.getBytes(Charset.forName("UTF-8"));
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.write(paramAsBytes);
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while((line = reader.readLine()) != null){
                response.append(line).append('\r');
            }
        }
        return response.toString();
    }
    
    private static String buildQuery(Map<String, Object> parameters) throws UnsupportedEncodingException{
        StringBuilder queryResult = new StringBuilder();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (queryResult.length() > 0) queryResult.append('&');
            queryResult.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            if (entry.getValue() != null){
                queryResult.append('=').append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
        }
        return queryResult.toString();
    }
}
