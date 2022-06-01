package com.pmart5a.csapplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerNio {

    private static final int PORT = 24325;
    private static final String HOST = "localhost";
    private static final int bufferCapacity = 2 << 10;

    private static void log(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        System.out.printf("[%s] %s\n", dtf.format(LocalDateTime.now()), message);
    }

        private static String formResponse(String clientMessage) {
            String[] fragments = clientMessage.split(" ");
            StringBuilder builder = new StringBuilder();
            for (String fragment : fragments) {
                builder.append(fragment.trim());
            }
            String serverResponse = builder.toString();
            if(serverResponse.isEmpty()) {
                return "Исходная строка содержала только пробелы.";
            } else {
                return serverResponse;
            }
        }

    private static void writeInBuffer(SocketChannel socketChannel, String serverMessage) {
        try {
            socketChannel.write(ByteBuffer.wrap(serverMessage.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }

        public static void main (String[]args){
            try (final ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
                serverChannel.bind(new InetSocketAddress(HOST, PORT));
                log("Server start");
                while (Thread.currentThread().isInterrupted()) {
                    try (SocketChannel socketChannel = serverChannel.accept()) {
                        log("Client connected: " + socketChannel.getRemoteAddress());
                        final ByteBuffer inputBuffer = ByteBuffer.allocate(bufferCapacity);
                        writeInBuffer(socketChannel, "Вас приветствует сервис 'Долой пробелы!'");
                        while (socketChannel.isConnected()) {
                            int bytesCount = socketChannel.read(inputBuffer);
                            if (bytesCount == -1) break;
                            final String clientMessage = new String(inputBuffer.array(), 0, bytesCount,
                                    StandardCharsets.UTF_8);
                            inputBuffer.clear();
                            writeInBuffer(socketChannel, "Обработанная строка:\n" +
                                    formResponse(clientMessage));
                        }
                        log("Client disconnected: " + socketChannel.getRemoteAddress());
                    } catch (IOException err) {
                        log(err.getMessage());
                    }
                }
            } catch (IOException err) {
                log(err.getMessage());
            }
        }
    }