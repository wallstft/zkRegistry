package com.wallstft.zookeeper.zkUtilities;

import com.wallstft.zookeeper.zkEnum.zkAction;
import com.wallstft.zookeeper.zkRegistry;

public interface zkListener {
    public void handler (zkRegistry registry, zkAction action, String path, String data );
}
