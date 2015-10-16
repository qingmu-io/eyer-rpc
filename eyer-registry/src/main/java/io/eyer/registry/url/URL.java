package io.eyer.registry.url;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

/**
 * Created by 青木 on 2015/8/24.
 */
public class URL {
    private String name;
    private String host;
    private int port;
    private String service;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    // name/host:port/service
    public URL toURL(String url) {
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        String[] split = url.split("/");
        this.name = split[0];
        this.service = split[2];
        this.port = Integer.parseInt(split[1].split(":")[1]);
        this.host = split[1].split(":")[0];
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URL url = (URL) o;

        if (port != url.port) return false;
        if (name != null ? !name.equals(url.name) : url.name != null) return false;
        if (host != null ? !host.equals(url.host) : url.host != null) return false;
        return !(service != null ? !service.equals(url.service) : url.service != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }

    public InetSocketAddress toAddress() {
        return new InetSocketAddress(this.getHost(), this.port);
    }
}
