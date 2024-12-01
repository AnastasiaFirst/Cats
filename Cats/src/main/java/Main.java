import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Main {

    public static final String REMOTE_SERVICE_URI = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build()).build();
        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        System.out.println("Ответ от сервера: " + response.getStatusLine());

        if (response.getStatusLine().getStatusCode() == 200) {
            List<Cat> cats = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Cat>>() {});
            List<Cat> filteredCats = cats.stream()
                    .filter(cat -> cat.getUpvotes() != null && cat.getUpvotes() > 0)
                    .collect(Collectors.toList());
            for (Cat cat : filteredCats) {
                System.out.println("ID: " + cat.getId());
                System.out.println("Text: " + cat.getText());
                System.out.println("Type: " + cat.getType());
                System.out.println("User: " + cat.getUser());
                System.out.println("Upvotes: " + cat.getUpvotes());
                System.out.println("-----------------------------------");
            }
        } else {
                System.out.println("Ошибка: " + response.getStatusLine().getStatusCode());
            }
    }
}
