package webserver;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// 연결 소켓 하나를 처리하는 워커.
// InputStream 으로 HttpRequest 를 만들고, OutputStream 으로 HttpResponse 를 쓰며,
// 실제 URL 분기와 응답 처리는 RequestMapper 에 위임한다.
public class RequestHandler implements Runnable {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, () -> String.format("New Client Connect! Connected IP : %s, Port : %s",
                connection.getInetAddress(), connection.getPort()));

        // try-with-resources 로 InputStream/OutputStream 자원 누수 방지
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {

            // 1) 소켓 스트림을 BufferedReader / DataOutputStream 으로 감싼다 (데코레이터 패턴)
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // 2) HttpRequest 메시지 파싱 + HttpResponse 작성기 준비
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            // 3) URL 에 맞는 컨트롤러 실행을 RequestMapper 에 위임
            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
