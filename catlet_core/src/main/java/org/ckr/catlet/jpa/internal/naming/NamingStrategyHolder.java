package org.ckr.catlet.jpa.internal.naming;

import org.ckr.catlet.jpa.NamingStrategy;

public class NamingStrategyHolder {
    private static NamingStrategy instance = new NamingStrategyImpl();
    public static NamingStrategy getStrategy(){
        return instance;
    }
}
