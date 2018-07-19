package model;

import java.util.List;
import java.util.Vector;

public class Room {

    public RoomManager roomManager;
    public String id;
    public int index;
    public String title;
    public List<Client> clients;

    public Room(
            RoomManager roomManager,
            String id,
            int index,
            String title ) {
        this.roomManager = roomManager;
        this.id = id;
        this.index = index;
        this.title = title;
        clients = new Vector<>();
    }

    /**
     * [ Method ::  ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     * */
    public void entryRoom(Client client) {
        clients.add(client);
        client.room = this;
    }

    /**
     * [ Method ::  ]
     *
     * @DES ::
     * @IP1 ::
     * @O.P ::
     * @S.E ::
     * */
    public void leaveRoom(Client client) {
        this.clients.remove(client);
        client.room = null;
        if(this.clients.size() < 1) {
            roomManager.destroyRoom(this);
        }
    }
}
