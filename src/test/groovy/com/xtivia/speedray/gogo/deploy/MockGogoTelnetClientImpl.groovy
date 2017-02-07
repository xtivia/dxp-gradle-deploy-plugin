package com.xtivia.speedray.gogo.deploy

/**
 * Created by don on 2/7/2017.
 */
class MockGogoTelnetClientImpl implements GogoTelnetClient {

    MockGogoTelnetClientImpl(String host, int port) {

    }

    public String send(String command) throws IOException {
        return command;
    }


}
