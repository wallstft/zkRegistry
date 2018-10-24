package com.wallstft.zookeeper.zkUtilities;

import org.apache.log4j.Logger;

import java.net.InetAddress;

public class zkUtil {

    final static Logger log = Logger.getLogger(zkUtil.class);

    public static String getHost() {
        String host = null;
        try {
            InetAddress.getLocalHost().getHostName();
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
        }
        return host;
    }
}
