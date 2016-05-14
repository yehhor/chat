package com.javarush.test.level30.lesson15.big01.client;


import com.javarush.test.level30.lesson15.big01.ConsoleHelper;
import com.javarush.test.level30.lesson15.big01.Message;
import com.javarush.test.level30.lesson15.big01.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by yehor on 26.02.2016.
 */
public class BotClient extends Client
{
    private static int count = 0;

    public class BotSocketThread extends SocketThread
    {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: " +
                    "дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
            String[] str = message.split(": ");
            if (str.length >= 2)
            {
                String name = str[0];
                String text = str[1];
                String message1 = "";

                if (text.equals("дата"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("d.MM.YYYY").format(Calendar.getInstance().getTime());

                else if (text.equals("день"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("d").format(Calendar.getInstance().getTime());

                else if (text.equals("месяц"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());

                else if (text.equals("год"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("YYYY").format(Calendar.getInstance().getTime());

                else if (text.equals("время"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("H:mm:ss").format(Calendar.getInstance().getTime());

                else if (text.equals("час"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("H").format(Calendar.getInstance().getTime());

                else if (text.equals("минуты"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("m").format(Calendar.getInstance().getTime());

                else if (text.equals("секунды"))
                    message1 = "Информация для " + name + ": " + new SimpleDateFormat("s").format(Calendar.getInstance().getTime());

                if (!message1.isEmpty())
                    sendTextMessage(message1);
            }
        }

    }

    public static void main(String[] args)
    {
        BotClient bot = new BotClient();
        bot.run();
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole()
    {
        return false;
    }

    @Override
    protected String getUserName()
    {
        if (count == 100)
            count = 0;
        return String.format("date_bot_%d", count++);
    }
}
