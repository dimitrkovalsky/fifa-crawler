package com.liberty.common;

import java.util.logging.Level;

/**
 * Created by Dmytro_Kovalskyi on 12.11.2015.
 */
public class LoggingUtil {
    public static void info(String message) {
        System.out.println(message);
        sendLoggingMessage(Level.INFO, message);
    }

    /**
     * Logging without notification send.
     */
    public static void localInfo(String message) {
        System.out.println(message);
    }

    /**
     * Logging without notification send.
     */
    public static void localInfo(Object caller, String message) {
        String finalMessage = "[" + caller.getClass().getSimpleName() + "] " + message;
        System.out.println(finalMessage);
    }

    public static void info(Object caller, String message) {
        String finalMessage = "[" + caller.getClass().getSimpleName() + "] " + message;
        System.out.println(finalMessage);
        sendLoggingMessage(Level.INFO, finalMessage);
    }

    /**
     * Logging without notification send.
     */
    public static void localError(Object caller, String message) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + message;
        System.err.println(finalMessage);
    }

    public static void error(Object caller, String message) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + message;
        System.err.println(finalMessage);
        sendLoggingMessage(Level.SEVERE, finalMessage);
    }

    public static void error(Object caller, String message, Exception e) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + message + ". " + e.getMessage();
        System.err.println(finalMessage);
        sendLoggingMessage(Level.SEVERE, finalMessage);
    }

    /**
     * Logging without notification send.
     */
    public static void localError(Object caller, String message, Exception e) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + message + ". " + e.getMessage();
        System.err.println(finalMessage);
    }

    /**
     * Logging without notification send.
     */
    public static void localError(Object caller, Exception e) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + e.getMessage();
        System.err.println(finalMessage);
    }

    public static void error(Object caller, Exception e) {
        String finalMessage = "ERROR [" + caller.getClass().getSimpleName() + "] " + e.getMessage();
        System.err.println(finalMessage);
        sendLoggingMessage(Level.SEVERE, finalMessage);
    }

    private static void sendLoggingMessage(Level level, String message) {

    }
}