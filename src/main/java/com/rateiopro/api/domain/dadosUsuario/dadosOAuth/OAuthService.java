package com.rateiopro.api.domain.dadosUsuario.dadosOAuth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OAuthService {

    @Value("${github.client-id}")
    private String githubClientId;

    @Value("${github.client-secret}")
    private String githubClientSecret;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    private final String redirectUriGithub = "https://rateiopro-api.onrender.com/login/github/autorizado";

    private final String redirectUriGoogle = "https://rateiopro-api.onrender.com/login/google/autorizado";

    @Autowired
    private RestClient client;

    public String gerarUrlGithub(){
        return "https://github.com/login/oauth/authorize"+
                "?client_id="+githubClientId+
                "&redirect_uri="+redirectUriGithub+
                "&scope=read:user,user:email";
    }

    public String gerarTokenGithub(String code){

        var response = client.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("client_id",githubClientId,"client_secret",githubClientSecret,
                        "code",code,"redirect_uri",redirectUriGithub))
                .retrieve()
                .body(Map.class);

        return response.get("access_token").toString();
    }

    public String buscarEmailGithub(String code){
        var token = gerarTokenGithub(code);

        var header = new HttpHeaders();
        header.setBearerAuth(token);

        var response = client.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(header))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        for(DadosEmail email:response){
            if (email.primary && email.verified()){
                return email.email;
            }
        }

        return null;
    }

    private record DadosEmail(
            String email,
            Boolean primary,
            Boolean verified,
            String visibility
    ){}

    public String gerarUrlGoogle(){
        return "https://accounts.google.com/o/oauth2/v2/auth"+
                "?client_id="+googleClientId+
                "&redirect_uri="+redirectUriGoogle+
                "&scope=https://www.googleapis.com/auth/userinfo.email"+
                "&response_type=code";
    }

    public String gerarTokenGoogle(String code){

        var response = client.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("client_id",googleClientId,"client_secret",googleClientSecret,
                        "code",code,"redirect_uri",redirectUriGoogle,"grant_type","authorization_code"))
                .retrieve()
                .body(Map.class);

        return response.get("id_token").toString();
    }

    public String buscarEmailGoogle(String code){
        var token = gerarTokenGoogle(code);

        DecodedJWT decodedJWT = JWT.decode(token);

        return decodedJWT.getClaim("email").asString();
    }
}
