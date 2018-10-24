package com.wallstft.zookeeper.zkUtilities;

import com.wallstft.zookeeper.zkRegistry;

public abstract class zkLeader {
    Boolean isLeader = false;
    abstract public void takeTheLead ( zkRegistry registry );

    public Boolean isTheLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }
}
