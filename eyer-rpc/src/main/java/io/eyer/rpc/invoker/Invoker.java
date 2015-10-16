package io.eyer.rpc.invoker;

import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;

import java.io.Closeable;

/**
 * Created by 青木 on 2015/8/25.
 */
public interface Invoker extends Closeable {


     Response invoke(Request request) throws Exception;

}
