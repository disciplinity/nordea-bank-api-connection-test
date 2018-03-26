import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;


public class NordeaBankAPI {

    private String url = "https://api.nordeaopenbanking.com/v1/authentication";
    private String state = "123";
    private String clientId = "5f5ed94f-fd86-477a-b0f6-207dcd733492";
    private String clientSecret = "oM6fR8xH7dQ2uC7fV1qA5yQ8dL4nP3bK5uA1iA8cU0bL7lY6hR";
    private String redirectUri = "http://httpbin.org/get&X-Response-Scenarios=AuthenticationSkipUI";

    private String initiateAuthFlowQuery = String.format("%s?client_id=%s&redirect_uri=%s&state=%s",
            url, clientId, redirectUri, state);

    private OkHttpClient client = new OkHttpClient();


    public String getAuthorizationRedirectUrl() throws IOException{

        // Initiating authentication flow by using the following request
        Request request = new Request.Builder()
                .url(initiateAuthFlowQuery)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), AuthFlowInitiationResponse.class).url;
    }

    public String generateCode() throws IOException{

        Request request = new Request.Builder()
                .url(getAuthorizationRedirectUrl())
                .get()
                .build();

        Response response = client.newCall(request).execute();

        JsonObject jObject = new Gson().fromJson(response.body().string(), JsonObject.class);

        return ((JsonObject) jObject.get("args")).get("code").getAsString();
    }

    public String generateAccessToken() throws IOException{

        String code = generateCode();
        RequestBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("redirect_uri", "http://httpbin.org/get")
                .build();


        Request request = new Request.Builder()
                .url("https://api.nordeaopenbanking.com/v1/authentication/access_token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-IBM-Client-Id", clientId)
                .addHeader("X-IBM-Client-Secret", clientSecret)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        JsonObject jsonObject = new Gson().fromJson(response.body().string(), JsonObject.class);
        String accessToken = jsonObject.get("access_token").getAsString();
        long expiresIn = jsonObject.get("expires_in").getAsLong();

        return response.body().string();
    }
    



    private class AuthFlowInitiationResponse {

        private String url;

        public String getUrl() {
            return url;
        }
    }

}
