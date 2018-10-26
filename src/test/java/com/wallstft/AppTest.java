package com.wallstft;

import static org.junit.Assert.assertTrue;

import com.wallstft.zookeeper.zkRegistry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import com.wallstft.zookeeper.zkEnum.zkAction;
import com.wallstft.zookeeper.zkUtilities.zkLeader;
import com.wallstft.zookeeper.zkUtilities.zkListener;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    private final static Logger log = Logger.getLogger(AppTest.class);

    String connection_string = "localhost:2181";

    zkListener myListenerInstance = new zkListener() {
        @Override
        public void handler(zkRegistry registry, zkAction action, String path, String data) {
            log.debug("handler path = "+path+" data="+data);
        }
    };

    class myListener implements zkListener {
        @Override
        public void handler(zkRegistry registry, zkAction action, String path, String data) {
            log.debug("handler path = "+path+" data="+data);
        }
    }

    myListener my_handler = new myListener();

    @Ignore
    @Test
    public void test_zk_builder()
    {
        zkRegistry registry = new zkRegistry(connection_string);
        registry.connect();

        String path = "/zkRegTest/test_node5678";
        String data = "test_data";
        registry.create(CreateMode.PERSISTENT, path, data).nodeListener(myListenerInstance);

        registry.delete( path );

        path = "/zkRegTest/test_node1234";
        data = "test_data";
        registry.create(CreateMode.PERSISTENT, path, data).nodeListener(my_handler);

        registry.delete( path );

        path = "/zkRegTest/myLeader";
        data = "test_data";
        registry.create(CreateMode.PERSISTENT, path, data).addLeaderCandidate(new zkLeader() {
            @Override
            public void takeTheLead(zkRegistry registry) {
                int cnt =0;
                System.out.println("I've been elected the leader");
                while ( isTheLeader() ) {
                    try {
                        System.out.println("Im the leader");
                        Thread.sleep(5 * 1000);
                        if (++cnt >= 3 ) {
                            setLeader(false);
                        }
                    }
                    catch ( Exception ex )
                    {
                        log.error(ex);
                        ex.printStackTrace();
                    }
                }
                System.out.println("I've stopped begin the leader");
            }
        });

        try {
            Thread.sleep(1 * 30 * 1000);
        }
        catch ( Exception ex ) {
            log.error(ex);
            ex.printStackTrace();
        }
    }
}
