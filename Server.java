package com.javarush.test.level30.lesson15.big01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yehor on 26.02.2016.
 */
public class Server
{
    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread
    {
        private Socket socket;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        public void run()
        {
            String clientName = null;
            ConsoleHelper.writeMessage("New connection established via " + socket.getRemoteSocketAddress());
            try(Connection connection = new Connection(socket)){
                clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                sendListOfUsers(connection, clientName);
                serverMainLoop(connection, clientName);

            }catch (IOException e)
            {
                ConsoleHelper.writeMessage("произошла ошибка при обмене данными с удаленным адресом");
            }
            catch (ClassNotFoundException e)
            {
                ConsoleHelper.writeMessage("произошла ошибка при обмене данными с удаленным адресом");
            }

            {
                if (clientName != null)
                {
                    connectionMap.remove(clientName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, clientName));
                    ConsoleHelper.writeMessage("Connection lost with " + clientName);
                }
            }
        }

        private String serverHandshake(Connection connection) throws IOException,
                ClassNotFoundException
        {
            String name = null;
            while (true)
            {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message clientAnswerName = connection.receive();
                if (clientAnswerName.getType() == (MessageType.USER_NAME)
                        && clientAnswerName.getData() != null
                        && !clientAnswerName.getData().isEmpty())
                {
                    String clientName = clientAnswerName.getData();
                    if (connectionMap.containsKey(clientName))
                        continue;
                    name = clientName;
                    break;
                }
            }
            connectionMap.put(name, connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return name;
        }

        private void sendListOfUsers(Connection connection, String userName) throws
                IOException
        {
            for (String name : connectionMap.keySet())
            {
                if (name.equals(userName))
                    continue;
                else
                {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws
                IOException, ClassNotFoundException
        {
            while(true)
            {
                Message message = connection.receive();
                if(message.getType() == MessageType.TEXT)
                {
                    Message newMessage = new Message(MessageType.TEXT, userName + ": " + message.getData());
                    sendBroadcastMessage(newMessage);
                }
                else
                    ConsoleHelper.writeMessage("Message is not TEXT");
            }
        }
    }

    public static void main(String[] args)
    {
        ConsoleHelper.writeMessage("Enter port number:");
        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)
        )
        {
            ConsoleHelper.writeMessage("Server started . . .");
            while (true)
            {
                new Handler(serverSocket.accept()).start();
            }
        }
        catch (Exception e)
        {
            ConsoleHelper.writeMessage("error");
        }
    }

    public static void sendBroadcastMessage(Message message)
    {
        for (Map.Entry<String, Connection> connect : connectionMap.entrySet())
        {
            try
            {
                connect.getValue().send(message);
            }
            catch (IOException e)
            {
                System.out.println("Can't send message");
            }
        }
    }

}
