import java.io.IOException;

public class ClientStart {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.init();
        client.start();
    }
}
