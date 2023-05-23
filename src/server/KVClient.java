package server;

import model.ManagerLoadException;
import model.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    final private URI url;
    private String apiToken;

    public KVClient(URI url) throws IOException {
        this.url = url;
        register();
    }

    private void register(){
        try {
            URI newUrl = URI.create(url + "/register");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(newUrl)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println(response.statusCode());
                System.out.println(response.body());
                throw new ManagerSaveException();
            }
            apiToken = response.body();
        }catch (IOException | InterruptedException e){
            throw  new ManagerSaveException();
        }
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI urlForPost = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(urlForPost)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode()!=200){
            System.out.println("Что-то пошло не так при сохранении. Сервер вернул код состояния: " + response.statusCode());
            throw new ManagerSaveException();
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI urlForGet = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlForGet)
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode()!=200){
            System.out.println("Что-то пошло не так при загрузки. Сервер вернул код состояния: " + response.statusCode());
            throw new ManagerLoadException();
        }
        return response.body();
    }
}
