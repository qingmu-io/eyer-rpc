package io.eyer.registry;

import io.eyer.registry.url.URL;

import java.util.List;

/**
 * Created by 青木 on 2015/8/24.
 */
public interface ChildListener {

    void notify(List<URL> urls);
}
