package com.pmart5a.csapplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientNio {

    private static final int PORT = 24325;
    private static final String HOST = "localhost";
    private static final int bufferCapacity = 2 << 10;

    private static void readFromBuffer(SocketChannel socketChannel, ByteBuffer inputBuffer) {
        try {
            int bytesCount = socketChannel.read(inputBuffer);
            System.out.println(new String(inputBuffer.array(), 0, bytesCount,
                    StandardCharsets.UTF_8));
            inputBuffer.clear();
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }

    private static void writeInBuffer(SocketChannel socketChannel, String clientMessage) {
        try {
            socketChannel.write(ByteBuffer.wrap(clientMessage.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }

    public static void main(String[] args) {
        try (final SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            try (Scanner scanner = new Scanner(System.in)) {
                final ByteBuffer inputBuffer = ByteBuffer.allocate(bufferCapacity);
                String clientMessage;
                readFromBuffer(socketChannel, inputBuffer);
                while (true) {
                    System.out.println(("Введите исходную строку или 'выход' для выхода:"));
                    clientMessage = scanner.nextLine();
                    if ("выход".equals(clientMessage)) {
                        break;
                    } else if (!"".equals(clientMessage)) {
                        writeInBuffer(socketChannel, clientMessage);
                        readFromBuffer(socketChannel, inputBuffer);
                    }
                }
            }
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }
}