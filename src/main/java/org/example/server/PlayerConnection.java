package org.example.server;

import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PlayerConnection implements IPlayerConnection {
    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final BlockingQueue<Packet> packetQueue = new ArrayBlockingQueue<>(10, true);
    private final Socket socket;
    private final String version;
    private final ReentrantLock LOCK = new ReentrantLock();
    private volatile boolean isActive = false;

    /**
     * Class constructor.
     *
     * @param version Program version
     * @param socket  connection to a game.
     */
    public PlayerConnection(Socket socket, String version) {
        this.socket = socket;
        this.version = version;
    }

    /**
     * Blocking call that initialized player connection
     *
     * @return true if initialization was successful, false otherwise
     */
    @Override
    public boolean init() {
        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.STREAM_START, version));
            input.set(new ObjectInputStream(this.socket.getInputStream()));
            ConnectionHelper c = (ConnectionHelper) input.get().readUnshared();
            if (c.message == ConnectionHelper.Message.REQUEST_RESPONSE && c.version.equals(version)) {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MATCH, version));
            } else {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MISMATCH, version));
                input.get().close();
                output.get().close();
                socket.close();
                return false;
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return false;
        }

        isActive = true;
        new Thread(this::handleOutput).start();
        new Thread(this::handleInput).start();
        return true;
    }

    private void handleInput() {
        while (!socket.isClosed() && socket.isConnected() && isActive) {
            try {
                Packet packet = (Packet) input.get().readObject();
                handlePacket(packet);
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                if (isActive) {
                    isActive = false;
                    handlePacket(new Packet.PacketBuilder()
                            .code(Packet.Codes.CONNECTION_LOST)
                            .message("Lost connection on receive").build());
                }
            }
        }
    }

    private void handleOutput() {
        while (!socket.isClosed() && socket.isConnected() && isActive) {
            try {
                LOCK.lock();
                Packet p = packetQueue.poll(2, TimeUnit.SECONDS);
                if (p == null)
                    continue;
                output.get().reset();
                output.get().writeObject(p);
                output.get().flush();
                LOCK.unlock();
            } catch (IOException e) {
                LOCK.unlock();
                if (isActive) {
                    isActive = false;
                    handlePacket(new Packet.PacketBuilder()
                            .code(Packet.Codes.CONNECTION_LOST).build());
                }
            } catch (InterruptedException ignored) {

            }
        }
    }

    protected abstract void handlePacket(Packet packet);

    @Override
    public synchronized void send(Packet packet) {
        packetQueue.add(packet);
    }
}
