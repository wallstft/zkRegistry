package com.wallstft.zookeeper.zkUtilities;

import com.wallstft.zookeeper.zkEnum.zkAction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import com.wallstft.zookeeper.zkRegistry;

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

public class zkBuilder {

    private final static Logger log = Logger.getLogger(zkBuilder.class);

    private zkRegistry registry = null;
    private String path = null;
    private CreateMode mode = null;
    private String data = null;
    private CuratorFramework client = null;
    private String node_path = null;


    public zkBuilder (CuratorFramework client, zkRegistry registry, String path, CreateMode mode, String data ) {
        this.path = path;
        this.mode = mode;
        this.data = data;
        this.client = client;
        this.registry = registry;
    }

    public zkBuilder create ()
    {
        try {
            if ( data != null )
                node_path = client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data.getBytes());
            else
                node_path = client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
        }
        return this;
    }


    public zkBuilder addLeaderCandidate ( final zkLeader handler )
    {
        LeaderSelector leaderSelector= new LeaderSelector(client, path, new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                if ( handler != null ) {
                    handler.setLeader(true);
                    handler.takeTheLead(registry);
                }
            }
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if ( connectionState != null ) {
                    handler.setLeader(connectionState.isConnected());
                }
            }
        });
        leaderSelector.autoRequeue();
        leaderSelector.start();
        return this;
    }

    public zkBuilder nodeListener (final zkListener handler)
    {
        NodeCache cache = new NodeCache(client, path);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                if ( handler != null ) {
                    byte[] data= client.getData().forPath(path);
                    String data_str = null;
                    if ( data != null )
                        data_str = new String ( data );
                    handler.handler( registry, zkAction.Update, path, data_str );
                }
            }
        });
        return this;
    }

    public zkBuilder pathListener (final zkListener handler)
    {
        PathChildrenCache cache = new PathChildrenCache(client, path, false );
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if ( pathChildrenCacheEvent != null ) {
                    if ( pathChildrenCacheEvent.getData()!=null ) {
                        String data_str = null;
                        String path = pathChildrenCacheEvent.getData().getPath();
                        byte[] data = pathChildrenCacheEvent.getData().getData();
                        if ( data != null )
                            data_str = new String ( data );
                        if ( path != null ) {
                            PathChildrenCacheEvent.Type action = pathChildrenCacheEvent.getType();
                            switch (action) {
                                case CHILD_ADDED:
                                    if (handler != null) {
                                        handler.handler(registry, zkAction.Add, path, data_str);
                                    }
                                    break;
                                case CHILD_REMOVED:
                                    if ( handler != null ) {
                                        handler.handler(registry, zkAction.Delete, path, null);
                                    }
                                    break;
                                case CHILD_UPDATED:
                                    if ( handler != null ) {
                                        handler.handler( registry, zkAction.Update, path, data_str );
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
        try {
            cache.start();
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
        }
        return this;
    }

    public zkBuilder treeListener (final zkListener handler)
    {
        TreeCache cache = new TreeCache(client, path);
        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                if ( treeCacheEvent != null ) {
                    if ( treeCacheEvent.getData()!=null ) {
                        String data_str = null;
                        String path = treeCacheEvent.getData().getPath();
                        byte[] data = treeCacheEvent.getData().getData();
                        if ( data != null )
                            data_str = new String ( data );
                        if ( path != null ) {
                            TreeCacheEvent.Type action = treeCacheEvent.getType();
                            switch (action) {
                                case NODE_ADDED:
                                    if (handler != null) {
                                        handler.handler(registry, zkAction.Add, path, data_str);
                                    }
                                    break;
                                case NODE_REMOVED:
                                    if ( handler != null ) {
                                        handler.handler(registry, zkAction.Delete, path, null);
                                    }
                                    break;
                                case NODE_UPDATED:
                                    if ( handler != null ) {
                                        handler.handler( registry, zkAction.Update, path, data_str );
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
        try {
            cache.start();
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
        }
        return this;
    }
}
