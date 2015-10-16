package io.eyer;

import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.rpc.proxy.Client;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ClientHandler;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.service.User;
import io.eyer.service.UserService;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class Task extends Thread {
    private final Client client;
    private final AtomicLong counter;
    private final long startTime;
    private final long N;

    public Task(Client client, AtomicLong counter, long startTime, long N) {
        this.client = client;
        this.counter = counter;
        this.startTime = startTime;
        this.N = N;
    }

    @Override
    public void run() {
        for (int i = 0; i < N; i++) {
            Request msg = new Request();
            msg.setInterfaceClass(UserService.class);
            msg.setParams(new Object[]{new User()});
            msg.setPatamType(new Class[]{User.class});
            msg.setMethodName("save");
            msg.setId(UUID.randomUUID().toString());
            try {
                client.invoker(msg);
                counter.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (counter.get() % 5000 == 0) {
                double qps = counter.get() * 1000.0 / (System.currentTimeMillis() - startTime);
                System.out.format("QPS: %.2f\n", qps);
            }
        }
    }
}

public class RemotingPerf {

    public static void main(String[] args) throws Exception {

        final long N = 1000000;
        final int threadCount = 50;


        final AtomicLong counter = new AtomicLong(0);

        Client[] clients = new Client[threadCount];
        NioConfig.registered(new KryoSerializer());
        NioConfig.registered(new ClientHandler());
        NioConfig.initSessionHandler();
        ReactorPool reactorPool = new ReactorPool(1);
        Connector connector = new Connector("connector",reactorPool);
        NioConfig.registered(connector);


        for (int i = 0; i < clients.length; i++) {
            clients[i] = new Client(new InetSocketAddress("127.0.0.1",6161));
        }
        System.out.println("init success");
        final long startTime = System.currentTimeMillis();
        Task[] tasks = new Task[threadCount];
        for (int i = 0; i < threadCount; i++) {
            tasks[i] = new Task(clients[i], counter, startTime, N);
        }
        for (Task task : tasks) {
            task.start();

        }

        for (Task task : tasks) {
            task.join();
        }

    }
}
