package com.javarush.test.level30.lesson15.big01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yehor on 26.02.2016.
 */
public class ConsoleHelper
{
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message)
    {
        System.out.println(message);
    }

    public static String readString()
    {
        String result = null;
        while (result == null)
            try
            {
                result = reader.readLine();
            }
            catch (IOException e)
            {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        return result;
    }

    public static int readInt()
    {
        Integer result = null;
        while (result == null)
        {
            try{
                result = Integer.parseInt(readString());
            }catch (NumberFormatException e)
            {
                System.out.println("Произошла ошибка " +
                        "при попытке ввода числа. Попробуйте еще раз.");
            }
        }
        return result;
    }
}
