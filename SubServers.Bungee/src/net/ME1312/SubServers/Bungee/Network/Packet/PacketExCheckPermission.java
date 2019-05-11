package net.ME1312.SubServers.Bungee.Network.Packet;

import net.ME1312.Galaxi.Library.Callback.Callback;
import net.ME1312.Galaxi.Library.Map.ObjectMap;
import net.ME1312.Galaxi.Library.Util;
import net.ME1312.SubData.Server.Protocol.PacketObjectIn;
import net.ME1312.SubData.Server.Protocol.PacketObjectOut;
import net.ME1312.SubData.Server.SubDataClient;

import java.util.HashMap;
import java.util.UUID;

/**
 * Packet Check Permission
 */
public class PacketExCheckPermission implements PacketObjectIn<Integer>, PacketObjectOut<Integer> {
    private static HashMap<UUID, Callback<Boolean>[]> callbacks = new HashMap<UUID, Callback<Boolean>[]>();
    private UUID player;
    private String permission;
    private UUID tracker;

    /**
     * New PacketCheckPermission (In)
     */
    public PacketExCheckPermission() {}

    /**
     * New PacketCheckPermission (Out)
     *
     * @param player Player to check on
     * @param permission Permission to check
     * @param callback Callbacks
     */
    @SafeVarargs
    public PacketExCheckPermission(UUID player, String permission, Callback<Boolean>... callback) {
        this.player = player;
        this.permission = permission;
        this.tracker = Util.getNew(callbacks.keySet(), UUID::randomUUID);
        callbacks.put(tracker, callback);
    }

    @Override
    public ObjectMap<Integer> send(SubDataClient client) throws Throwable {
        ObjectMap<Integer> data = new ObjectMap<Integer>();
        data.set(0x0000, tracker);
        data.set(0x0001, player);
        data.set(0x0002, permission);
        return data;
    }

    @Override
    public void receive(SubDataClient client, ObjectMap<Integer> data) throws Throwable {
        for (Callback<Boolean> callback : callbacks.get(data.getUUID(0x0000))) callback.run(data.getBoolean(0x0001));
        callbacks.remove(data.getUUID(0x0000));
    }

    @Override
    public int version() {
        return 0x0001;
    }
}
