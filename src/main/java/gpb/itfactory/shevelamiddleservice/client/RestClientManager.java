package gpb.itfactory.shevelamiddleservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientManager implements ClientManager<RestClient> {

    private final String BASE_URL;

    public RestClientManager(@Value("${backend.service.url}") String baseUrl) {
        BASE_URL = baseUrl;
    }

    @Override
    public RestClient getClient() {
        return RestClient.create(BASE_URL);
    }

}