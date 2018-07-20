package model;

import util.Define;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public ExecutorService executorService;
    public ServerSocketChannel serverSocketChannel;
    public List<Client> clients;
    public RoomManager roomManager;

    public Server() {
        this.clients = new Vector<>();
        this.roomManager = new RoomManager(clients);
    }

    /**
     * [ Method :: startServer ]
     *
     * @DES :: 서버실행함수
     * @S.E :: connect()로 연결
     * */
    public void startServer() {

        // ### 가용한 프로세서만큼 스레드 생성 ###
        executorService = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() ); // 4
        executorService = Executors.newFixedThreadPool( 16 ); // 16

        try {
            serverSocketChannel = ServerSocketChannel.open();

            // ### 블로킹설정 ### (기본적으로블로킹방식으로동작)
            serverSocketChannel.configureBlocking(true);

            // ### bind(port) ###
            serverSocketChannel.bind( new InetSocketAddress(Define.port) );
            System.out.println("[채팅서버] 서버실행");

            // 스레드생성후 새로운소켓 계속감시
            connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * [ Method :: stopServer ]
     *
     * @DES :: 서버종료함수
     * @S.E :: 없음
     * */
    public void stopServer() {
        try {
            Iterator<Client> iterator = clients.iterator();
            while(iterator.hasNext()) {
                Client client = iterator.next();
                client.socketChannel.close();
                iterator.remove();
            }
            if(serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel .close();
            }
            if(executorService != null && executorService.isShutdown()) {
                executorService.shutdown();
            }
            Util.log("[채팅서버] 서버종료");
        } catch (IOException e) {}
    }

    /**
     * [ Method :: connect ]
     *
     * @DES :: 스레드생성후 새로운소켓 계속감시 (스레드풀)
     * @S.E :: accept()를 처리하는 함수
     * */
    public void connect() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {

                        // ### accept() ###
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();

                        // (확인할수있는데이터)
                        Util.log("[채팅서버] 새로운 클라이언트접속 " + isa.getHostName() );

                        // ### client추가 ###
                        Client client = new Client(executorService, socketChannel, clients, roomManager); // receive() 내장
                        clients.add(client);

                        Util.log("[채팅서버] 현재접속 클라이언트 수 : " + clients.size());

                    } catch (IOException e) {
                        if(serverSocketChannel.isOpen()) stopServer();
                        break;
                    }
                }
            }
        };
        executorService.submit(runnable);
    }
}
