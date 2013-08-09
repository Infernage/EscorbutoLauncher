package elr.modules.authentication;

import com.google.gson.Gson;
import elr.core.Loader;
import elr.core.util.Util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * New authenticate method.
 * @author Infernage
 */
final class YggdrasilAuthenticator {
    
    static Response login(String username, String password) throws MalformedURLException, IOException{
        LoginRequest request = new LoginRequest(username, password, Loader.getConfiguration().getUUID()
                .toString());
        return makeRequest(new URL(Util.YGGDRASILAUTHENTICATION_URL), request, Response.class);
    }
    
    static Response refreshLogin(String accessToken, String uuid, ProfileResponse profile)
            throws MalformedURLException, IOException{
        RefreshRequest request = new RefreshRequest(uuid, accessToken, profile);
        return makeRequest(new URL(Util.YGGDRASILAUTHENTICATION_REFRESH), request, Response.class);
    }
    
    private static Response makeRequest(URL url, Object input, Class type) throws IOException{
        Gson gson = new Gson();
        String post = doPost(url, gson.toJson(input), "application/json");
        return (Response) gson.fromJson(post, type);
    }
    
    private static String doPost(URL url, String json, String content) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        byte[] parameters = json.getBytes(Charset.forName("UTF-8"));
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", content + "; charset=utf-8");
        connection.setRequestProperty("Content-Lenght", "" + parameters.length);
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.write(parameters);
            writer.flush();
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String lane;
            while((lane = reader.readLine()) != null){
                builder.append(lane).append('\r');
            }
        }
        return builder.toString();
    }
}
