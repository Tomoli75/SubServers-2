package net.ME1312.SubServers.Sync.Server;

import net.ME1312.Galaxi.Library.Util;
import net.ME1312.SubData.Client.DataSender;
import net.ME1312.SubData.Client.Library.ForwardedDataSender;
import net.ME1312.SubData.Client.SubDataClient;
import net.ME1312.SubData.Client.SubDataSender;
import net.ME1312.SubServers.Sync.SubAPI;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Server Class
 */
public class ServerImpl extends BungeeServerInfo {
    private HashMap<Integer, UUID> subdata = new HashMap<Integer, UUID>();
    private List<UUID> whitelist = new ArrayList<UUID>();
    private String nick = null;
    private boolean hidden;
    private final String signature;

    public ServerImpl(String signature, String name, String display, InetSocketAddress address, Map<Integer, UUID> subdata, String motd, boolean hidden, boolean restricted, Collection<UUID> whitelist) {
        super(name, address, motd, restricted);
        if (Util.isNull(name, address, motd, hidden, restricted)) throw new NullPointerException();
        this.signature = signature;
        this.whitelist.addAll(whitelist);
        this.hidden = hidden;
        setDisplayName(display);

        for (int channel : subdata.keySet())
            setSubData(subdata.get(channel), channel);
    }

    /**
     * Gets the SubData Client Channel IDs
     *
     * @return SubData Client Channel ID Array
     */
    public DataSender[] getSubData() {
        LinkedList<Integer> keys = new LinkedList<Integer>(subdata.keySet());
        LinkedList<SubDataSender> channels = new LinkedList<SubDataSender>();
        Collections.sort(keys);
        for (Integer channel : keys) channels.add((subdata.getOrDefault(channel, null) == null)?null:new ForwardedDataSender((SubDataClient) SubAPI.getInstance().getSubDataNetwork()[0], subdata.get(channel)));
        return channels.toArray(new SubDataSender[0]);
    }

    /**
     * Link a SubData Client to this Object
     *
     * @param client Client to Link
     * @param channel Channel ID
     */
    public void setSubData(UUID client, int channel) {
        if (channel < 0) throw new IllegalArgumentException("Subchannel ID cannot be less than zero");
        if (client != null || channel == 0) {
            if (!subdata.keySet().contains(channel) || (channel == 0 && (client == null || subdata.get(channel) == null))) {
                subdata.put(channel, client);
            }
        } else {
            subdata.remove(channel);
        }
    }

    /**
     * Get the Display Name of this Server
     *
     * @return Display Name
     */
    public String getDisplayName() {
        return (nick == null)?getName():nick;
    }

    /**
     * Sets the Display Name for this Server
     *
     * @param value Value (or null to reset)
     */
    public void setDisplayName(String value) {
        if (value == null || value.length() == 0 || getName().equals(value)) {
            this.nick = null;
        } else {
            this.nick = value;
        }
    }

    /**
     * See if a player is whitelisted
     *
     * @param player Player
     * @return Whitelisted Status
     */
    public boolean canAccess(CommandSender player) {
        return (player instanceof ProxiedPlayer && whitelist.contains(((ProxiedPlayer) player).getUniqueId())) || super.canAccess(player);
    }

    /**
     * Add a player to the whitelist (for use with restricted servers)
     *
     * @param player Player to add
     */
    public void whitelist(UUID player) {
        if (Util.isNull(player)) throw new NullPointerException();
        whitelist.add(player);
    }

    /**
     * Remove a player to the whitelist
     *
     * @param player Player to remove
     */
    public void unwhitelist(UUID player) {
        whitelist.remove(player);
    }

    /**
     * If the server is hidden from players
     *
     * @return Hidden Status
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Set if the server is hidden from players
     *
     * @param value Value
     */
    public void setHidden(boolean value) {
        if (Util.isNull(value)) throw new NullPointerException();
        this.hidden = value;
    }

    /**
     * Sets the MOTD of the Server
     *
     * @param value Value
     */
    public void setMotd(String value) {
        if (Util.isNull(value)) throw new NullPointerException();
        try {
            Util.reflect(BungeeServerInfo.class.getDeclaredField("motd"), this, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets if the Server is Restricted
     *
     * @param value Value
     */
    public void setRestricted(boolean value) {
        if (Util.isNull(value)) throw new NullPointerException();
        try {
            Util.reflect(BungeeServerInfo.class.getDeclaredField("restricted"), this, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the Signature of this Object
     *
     * @return Object Signature
     */
    public final String getSignature() {
        return signature;
    }
}
