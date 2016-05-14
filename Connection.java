package com.javarush.test.level30.lesson15.big01;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by yehor on 26.02.2016.
 */
public class Connection implements Closeable
{
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public Connection(Socket socket) throws IOException
    {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) throws IOException
    {
        synchronized (out)
        {
            out.writeObject(message);
        }
    }

    public Message receive() throws IOException, ClassNotFoundException
    {
        synchronized (in)
        {
            Message object = (Message) in.readObject();
            return object;
        }
    }

    public SocketAddress getRemoteSocketAddress()
    {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException
    {
        socket.close();
        out.close();
        in.close();
    }
}
