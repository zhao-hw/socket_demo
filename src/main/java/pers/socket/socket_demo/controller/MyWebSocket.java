package pers.socket.socket_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint("/chat/{username}")
public class MyWebSocket {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static int onlineNum = 0;

    private static Map<String, MyWebSocket> clients = new ConcurrentHashMap<>();

    private Session session;

    private String username;

    @RequestMapping("/chat/{username}")
    public String webSocket(@PathVariable String username, Model model) {
        try {
            logger.info("跳转到聊天的页面上");
            model.addAttribute("username", username);
            return "chat";
        } catch (Exception e) {
            logger.info("跳转到chat的页面上发生异常，异常信息是：" + e.getMessage());
            return "error";
        }
    }

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
        onlineNum++;
        logger.info(username + "已连接,id:" + session.getId());
        this.username = username;
        this.session = session;
        logger.info("有新连接加入! 当前在线人数:" + onlineNum);
        clients.put(username, this);
        for (MyWebSocket socket :clients.values()) {
            socket.session.getAsyncRemote().sendText(username + "加入聊天");
        }
    }

    @OnClose
    public void onColse() {
        onlineNum--;
        clients.remove(username);
        logger.info(username+"连接关闭,剩余"+onlineNum+"人");
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    public void sendMessageToAll() {

    }

}
