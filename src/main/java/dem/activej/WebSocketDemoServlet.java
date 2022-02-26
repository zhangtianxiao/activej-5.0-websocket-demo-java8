package dem.activej;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufStrings;
import io.activej.common.exception.MalformedDataException;
import io.activej.eventloop.Eventloop;
import io.activej.http.WebSocket;
import io.activej.http.WebSocketServlet;

import java.util.concurrent.Executor;

public class WebSocketDemoServlet extends WebSocketServlet {
  final Executor executor;

  public WebSocketDemoServlet(Executor executor) {
    this.executor = executor;
  }

  @Override
  protected void onWebSocket(WebSocket client) {
    System.out.println("onConnected");
    WebSocketSession session = new WebSocketSession(executor, client);
    session.read();
  }
}

class WebSocketSession {
  final WebSocket client;
  final Executor executor;

  public WebSocketSession(Executor executor, WebSocket client) {
    this.executor = executor;
    this.client = client;
  }

  void read() {
    client.readMessage().whenResult(this::onMessage).whenException(this::onError);
  }

  private void onMessage(WebSocket.Message msg) throws MalformedDataException {
    this.read();

    System.out.println("received: " + ByteBufStrings.asUtf8(msg.getBuf()));
    // send
    Eventloop eventloop = Eventloop.getCurrentEventloop();
    Runnable runnable = () -> {
      Eventloop.initWithEventloop(eventloop
        , () -> client.writeMessage(WebSocket.Message.binary(ByteBuf.wrapForReading("sfsdfsadfsd".getBytes())))
      );
    };
    this.executor.execute(runnable);
    //runnable.run();
  }

  private void onError(Exception e) {
    e.printStackTrace();
  }
}
