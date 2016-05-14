package com.javarush.test.level30.lesson15.big01.client;

import com.javarush.test.level30.lesson15.big01.ConsoleHelper;
import com.javarush.test.level30.lesson15.big01.Connection;
import com.javarush.test.level30.lesson15.big01.Message;
import com.javarush.test.level30.lesson15.big01.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by yehor on 26.02.2016.
 */
public class Client
{
    private volatile boolean clientConnected = false;

    protected Connection connection;

    public class SocketThread extends Thread
    {

        public void run()
        {
            String address = getServerAddress();
            int port = getServerPort();
            try{
                Socket socket = new Socket(address, port);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            }catch (Exception e)
            {
                notifyConnectionStatusChanged(false);
            }
        }

        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(userName + " connected to chat");
        }

        protected void informAboutDeletingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(userName + " disconnected from chat . . .");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected)
        {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this)
            {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException,
                ClassNotFoundException
        {
            while(true)
            {
                Message message = connection.receive();
                switch (message.getType())
                {
                    case NAME_REQUEST:
                        connection.send(new Message(MessageType.USER_NAME, getUserName()));
                        break;
                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        return;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException,
                ClassNotFoundException
        {
            while(true)
            {
                Message message = connection.receive();
                switch (message.getType())
                {
                    case TEXT:
                        processIncomingMessage(message.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getData());
                        break;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }

    }

    public static void main(String[] args)
    {
        Client client = new Client();
        client.run();
    }

    public void run()
    {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this)
        {
            try
            {
                this.wait();
            }
            catch (InterruptedException e)
            {
                ConsoleHelper.writeMessage("Error ocured");
            }
        }
        if (clientConnected)
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода " +
                    "наберите команду 'exit'.");
        else
            ConsoleHelper.writeMessage("Произошла ошибка во время " +
                    "работы клиента.");
        while (clientConnected)
        {
            String input = ConsoleHelper.readString();
            if (input.equalsIgnoreCase("exit"))
                break;
            if (shouldSentTextFromConsole())
                sendTextMessage(input);
        }
    }

    protected String getServerAddress()
    {
        ConsoleHelper.writeMessage("Enter server adress . . .");
        String address = ConsoleHelper.readString();

        return address;
    }

    protected int getServerPort()
    {
        ConsoleHelper.writeMessage("Enter port . . .");
        return ConsoleHelper.readInt();
    }

    protected String getUserName()
    {
        ConsoleHelper.writeMessage("Enter client name . . .");
        String name = ConsoleHelper.readString();
        return name;
    }

    protected boolean shouldSentTextFromConsole()
    {
        return true;
    }

    protected SocketThread getSocketThread()
    {
        return new SocketThread();
    }

    protected void sendTextMessage(String text)
    {
        try
        {
            connection.send(new Message(MessageType.TEXT, text));
        }
        catch (IOException e)
        {
            ConsoleHelper.writeMessage("No connection");
            clientConnected = false;
        }
    }
}
