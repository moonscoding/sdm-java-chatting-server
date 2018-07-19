package model;

import java.util.List;
import java.util.Vector;

public class RoomManager {

    public List<Client> clients;
    public List<Room> rooms;
    public String roomStatus;

    public RoomManager(List<Client> clients) {
        this.clients = clients;
        this.rooms = new Vector<>();
        this.roomStatus = "[]";
    }

    /**
     * [ Method :: updateRoomStatus ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     * */
    void updateRoomStatus() {
        System.out.println("updateRoomStatus call");
        roomStatus = "[";
        for (Room room : rooms) {
            roomStatus += String.format("{\"id\":\"%s\",\"title\":\"%s\"}", room.id, room.title);
        }
        roomStatus += "]";
        System.out.println(roomStatus);
    }

    /**
     * [ Method :: addRoom ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     * */
    public void createRoom( String title, Client client ) {
        Room newRoom = new Room(this, rooms.size() + title, rooms.size(), title);
        rooms.add(newRoom);

        // #전달 - 모든 Client에게 상황보고
        updateRoomStatus();
        for (int i = 0; i < clients.size(); i++) { clients.get(i).sendStatus(); }

        // #입장
        newRoom.entryRoom(client);
    }

    /**
     * [ Method :: addRoom ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     * */
    public void destroyRoom(Room room) {
        rooms.remove(room);

        // #전달 - 모든 Client에게 상황보고
        updateRoomStatus();
        for (int i = 0; i < clients.size(); i++) { clients.get(i).sendStatus(); }
    }

}
