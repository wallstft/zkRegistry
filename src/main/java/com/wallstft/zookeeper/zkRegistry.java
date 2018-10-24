package com.wallstft.zookeeper;

import com.wallstft.zookeeper.zkUtilities.zkBuilder;
import com.wallstft.zookeeper.zkUtilities.zkUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class zkRegistry {

    private final static Logger log = Logger.getLogger(zkUtil.class);

    private String zookeeper_connection_string = null;

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 30);
    private CuratorFramework client = null;

    public zkRegistry ( String zookeeper_connection_string ) {
        this.zookeeper_connection_string = zookeeper_connection_string;
    }

    public void connect() {
        client = CuratorFrameworkFactory.newClient(zookeeper_connection_string, retryPolicy);
        client.start();
    }

    public zkBuilder create (CreateMode mode, String path, String data )
    {
        zkBuilder builder =new zkBuilder(client, this, path, mode, data);
        if ( !doesNodeExist(path)) {
            builder.create();
        }
        return builder;
    }

    public void delete (String path)
    {
        if ( client != null ) {
            try {
                client.delete().forPath(path);
            }
            catch ( Exception ex ) {
                log.error(ex);
                ex.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if ( client != null ) {
            client.close();
            client = null;
        }
    }

    public boolean doesNodeExist ( String path )
    {
        Stat stat = null;

        try {
            stat = client.checkExists().forPath(path);
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
            stat = null;
        }
        return stat != null;
    }


}
