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

/*
   Copyright 2018 Wall Street Fin Tech

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


    */

public class zkRegistry {

    private final static Logger log = Logger.getLogger(zkUtil.class);

    //Used to hold the zookeeper connection string.
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
