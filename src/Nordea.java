import java.io.IOException;

public class Nordea {






    public static void main(String[] args) throws IOException{

        NordeaBankAPI a = new NordeaBankAPI();
        System.out.println(a.getAuthorizationRedirectUrl());
        System.out.println(a.generateAccessToken());


    }
}
