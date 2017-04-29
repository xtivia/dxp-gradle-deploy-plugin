package com.xtivia.speedray.gogo.deploy;

import java.io.IOException;

/**
 * Created by don on 2/7/2017.
 */
interface GogoTelnetClient {
    public String send(String command) throws IOException;
}
