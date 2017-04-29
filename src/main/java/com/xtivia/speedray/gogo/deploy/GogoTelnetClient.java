/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.speedray.gogo.deploy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ddavis on 1/2/17.
 */
public class GogoTelnetClient implements AutoCloseable {

    public GogoTelnetClient() throws IOException {
        this("localhost", 11311);
    }

    public GogoTelnetClient(String host, int port) throws IOException {
        _socket = new Socket(host, port);
        _inputStream = new DataInputStream(_socket.getInputStream());
        _outputStream = new DataOutputStream(_socket.getOutputStream());

        doHandshake();
    }

    private final Socket _socket;
    private final DataInputStream _inputStream;
    private final DataOutputStream _outputStream;

    private static void assertCond(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }
    private void doHandshake() throws IOException {
        // gogo server first sends 4 commands
        readOneCommand();
        readOneCommand();
        readOneCommand();
        readOneCommand();

        // first we negotiate terminal type
        // 255(IAC),251(WILL),24(terminal type)
        sendCommand(255, 251, 24);

        // server should respond
        // 255(IAC),250(SB),24,1,255(IAC),240(SE)
        readOneCommand();

        // send the terminal type
        //255(IAC),250(SB),24,0,'V','T','2','2','0',255(IAC),240(SE)
        sendCommand(255, 250, 24, 0, 'V', 'T', '2', '2', '0', 255, 240);

        // read gogo shell prompt
        readUntilNextGogoPrompt();
    }

    private String readUntilNextGogoPrompt() throws IOException {
        StringBuilder sb = new StringBuilder();

        int c = _inputStream.read();

        while (c != -1) {
            sb.append((char) c);

            if(sb.toString().endsWith("g! ")) {
                break;
            }

            c = _inputStream.read();
        }

        String output = sb.substring(0, sb.length() - 3);

        return output.trim();
    }

    public String send(String command) throws IOException {
        byte[] bytes = command.getBytes();

        int[] codes = new int[bytes.length + 2];

        for (int i = 0; i < bytes.length; i++) {
            codes[i] = bytes[i];
        }

        codes[bytes.length] = '\r';
        codes[bytes.length + 1] = '\n';

        sendCommand(codes);

        return readUntilNextGogoPrompt();
    }

    private void sendCommand(int... codes) throws IOException {
        for (int code : codes) {
            _outputStream.write(code);
        }
    }

    private int[] readOneCommand() throws IOException {
        List<Integer> bytes = new ArrayList<>();

        int iac = _inputStream.read();

        assertCond(iac == 255);

        bytes.add(iac);

        int second = _inputStream.read();

        bytes.add(second);

        if (second == 250) { // SB
            int option = _inputStream.read();

            bytes.add(option);

            int code = _inputStream.read(); // 1 or 0

            assertCond(code == 0 || code == 1);

            bytes.add(code);

            if (code == 0) {
                throw new IllegalStateException();
            }
            else if (code == 1) {
                iac = _inputStream.read();

                assertCond(iac == 255);

                bytes.add(iac);

                int se = _inputStream.read(); // SE

                assertCond(se == 240);

                bytes.add(se);
            }
        }
        else {
            bytes.add(_inputStream.read());
        }

        return toIntArray(bytes);
    }

    static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;

        for (Integer e : list) {
            ret[i++] = e.intValue();
        }

        return ret;
    }

    public void close() {
        try {
            _socket.close();
            _inputStream.close();
            _outputStream.close();
        }
        catch (IOException e) {
        }
    }

    public static boolean canConnect(String host, int port) {
        InetSocketAddress address = new InetSocketAddress(
                host, Integer.valueOf(port));
        InetSocketAddress local = new InetSocketAddress(0);

        InputStream in = null;

        try (Socket socket = new Socket()) {
            socket.bind(local);
            socket.connect(address, 3000);
            in = socket.getInputStream();

            return true;
        }
        catch (Exception e) {
        }

        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                }
            }
        }

        return false;
    }

}

