package model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.Util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Client {

    public ExecutorService executorService;
    public SocketChannel socketChannel;
    public List<Client> clients;
    public RoomManager roomManager;
    public Room room;

    public Client(ExecutorService executorService, SocketChannel socketChannel, List<Client> clients, RoomManager roomManager) {
        this.executorService = executorService;
        this.socketChannel = socketChannel;
        this.clients = clients;
        this.roomManager = roomManager;
        this.room = null;

        receive(); // ### 수신시작 ###
        sendStatus(); // ### 현재방 상태값 ###
    }

    /**
     * [ Method :: receive ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     */
    void receive() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ByteBuffer bb = ByteBuffer.allocate(100);

                        int byteCount = socketChannel.read(bb);
                        if (byteCount == -1) throw new IOException(); // ### 정상종료 :: close() 호출 ###

                        Util.log("[채팅서버] 수신 " + socketChannel.getRemoteAddress());

                        // ### 메세지 ###
                        bb.flip();
                        Charset cs = Charset.forName("UTF-8");
                        String strJson = cs.decode(bb).toString();

                        try {
                            JSONParser jsonParser = new JSONParser();
                            JSONObject token = (JSONObject) jsonParser.parse(strJson);
                            String method = token.get("method").toString();

                            System.out.println("[채팅서버] 요청매소드 " + method);
                            switch (method) {
                                case "/room/create":
                                    if (room == null) {
                                        roomManager.createRoom( token.get("title").toString(), Client.this);
                                        Util.log("[채팅서버] 채팅방 개설 " + socketChannel.getRemoteAddress());
                                        Util.log("[채팅서버] 현재 채팅방 갯수 " + roomManager.rooms.size());
                                    }
                                    break;
                                case "/room/entry":
                                    if (room == null) {
                                        for (int i = 0; i < roomManager.rooms.size(); i++) {
                                            if (roomManager.rooms.get(i).id.equals(token.get("id").toString())) {
                                                roomManager.rooms.get(i).entryRoom(Client.this);
                                                Util.log("[채팅서버] 채팅방 입장" + socketChannel.getRemoteAddress());
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                case "/room/leave":
                                    if (room != null) {
                                        Client.this.room.leaveRoom(Client.this);
                                        Util.log("[채팅서버] 채팅방 나감" + socketChannel.getRemoteAddress());
                                    }
                                    break;
                                case "/chat/send":
                                    if (room != null) {
                                        for (Client c : room.clients) {
                                            if (c != Client.this) c.sendEcho("message");
                                        }
                                    }
                                    break;
                                default:
                                    Util.log("[채팅서버] 메소드가 올바르지 않습니다. : " + socketChannel.getRemoteAddress());
                            }
                        } catch (Exception e) {
                            // Todo 올바른 데이터 형식이 아닙니다.
                            System.out.println(e);
                            Util.log("[채팅서버] 올바른 데이터 형식이 아닙니다. : " + socketChannel.getRemoteAddress());
                        }
                    } catch (IOException errA) {
                        terminate();
                        break;
                    }
                }
            }
        };
        executorService.submit(runnable);
    }

    /**
     * [ Method :: sendStatus ]
     *
     * @DES :: entry & leave 의 상태에 따라 계속적으로 클라이언트에게 인원수 전송
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     */
    void sendStatus() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    // ### write() ###
                    String packet = String.format("{\"method\":\"%s\",\"rooms\":%s}", "/room/status", roomManager.roomStatus);
                    Charset cs = Charset.forName("UTF-8");
                    ByteBuffer bb = cs.encode(packet);
                    socketChannel.write(bb);

                    Util.log("[채팅서버] 송신 /room/status");

                } catch (IOException e) {
                    terminate();
                }
            }
        };
        executorService.submit(runnable);
    }

    /**
     * [ Method :: sendEcho ]
     *
     * @DES :: 채팅내용을 같은 방에 있는 모든 유저에게 전달
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     */
    void sendEcho(String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    // ### write() ###
                    String packet = String.format("{\"method\":\"%s\",\"message\":\"%s\"}", "/chat/echo", message);
                    Charset cs = Charset.forName("UTF-8");
                    ByteBuffer bb = cs.encode(packet);
                    socketChannel.write(bb);

                    Util.log("[채팅서버] 송신 /chat/echo : " + socketChannel.getRemoteAddress());

                } catch (IOException e) {
                    terminate();
                }
            }
        };
        executorService.submit(runnable);
    }

    void terminate() {
        try {
            Util.log("[채팅서버] 통신두절 : " + socketChannel.getRemoteAddress());
            if (room != null) Client.this.room.leaveRoom(Client.this);
            clients.remove(Client.this);
            socketChannel.close();
        } catch (IOException e) {}
    }
}
