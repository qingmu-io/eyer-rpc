package io.eyer.rpc.balance;

import io.eyer.registry.url.URL;

import java.util.List;

/**
 * Created by 青木 on 2015/8/25.
 */
public interface Balance {
    URL select(List<URL> urls);
}
