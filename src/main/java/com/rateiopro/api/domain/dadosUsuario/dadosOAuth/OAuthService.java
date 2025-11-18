package com.rateiopro.api.domain.dadosUsuario.dadosOAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OAuthService {

    private final String clientId = "Ov23linyAKO82cuQJtUn";

    private final String clientSecret = "1b7d6d3e630fbd39f0587a96d16beb1409aefd36";

    private final String redirect_uri = "http://localhost:8080/login/github/autorizado";

    @Autowired
    private RestClient client;

    public String gerarUrl(){
        return "https://github.com/login/oauth/authorize"+
                "?client_id="+clientId+
                "&redirect_uri="+redirect_uri+
                "&scope=read:user,user:email";
    }

    public String gerarToken(String code){

        var response = client.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("client_id",clientId,"client_secret",clientSecret,
                        "code",code,"redirect_uri",redirect_uri))
                .retrieve()
                .body(Map.class);

        return response.get("access_token").toString();
    }

    public String buscarEmail(String code){
        var token = gerarToken(code);

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
}
