package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 서버의 엔트리 포인트.
// 환영 소켓(ServerSocket) 을 열어두고, 연결이 들어올 때마다 연결 소켓을 RequestHandler 에 넘긴다.
public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;

        // 고정 크기 스레드 풀 — 동시 요청을 병렬 처리한다
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓 — 연결 요청만 받아들이는 역할
        try (ServerSocket welcomeSocket = new ServerSocket(port)) {

            // 연결이 수락될 때마다 새로운 연결 소켓이 생성된다
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {
                // 연결 소켓 처리를 스레드 풀로 위임
                service.submit(new RequestHandler(connection));
            }
        }
    }
}
