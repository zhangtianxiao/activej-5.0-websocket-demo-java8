package dem.activej;

import io.activej.eventloop.Eventloop;
import io.activej.http.AsyncHttpServer;
import io.activej.http.RoutingServlet;
import io.activej.http.StaticServlet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Run, open http://localhost:10000/static/index.html in chrome
 * */
public class WebSocketDemo {
  public static void main(String[] args) throws IOException {
    Eventloop eventloop = Eventloop.create().withCurrentThread();
    RoutingServlet router = RoutingServlet.create();
    ExecutorService pool = Executors.newFixedThreadPool(8);
    router.map("/static/*", StaticServlet.ofPath(pool, Paths.get("").toAbsolutePath()));
    router.mapWebSocket("/ws", new WebSocketDemoServlet(pool));
    AsyncHttpServer.create(eventloop, router).withListenPort(10000).listen();

    eventloop.run();
  }
}
