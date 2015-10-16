### 一个简单的服务 (单机版)

        public class TestServer {
            public static void main(String[] args) throws IOException {

                NioConfig.registered(new KryoSerializer());
                final ServerHandler handler = new ServerHandler();
                NioConfig.registered(handler);
                NioConfig.initSessionHandler();
                //需要导出的服务接口 以及对应的实现类
                handler.services.put(UserService.class, new RemoteUserService());
                Server server = new Server(new InetSocketAddress("0.0.0.0",6161),new Acceptor("acceptor-1",new ReactorPool(1)));

            }
        }
### 服务引用

        public class ProxyClientTest {
            public static void main(String[] args) throws Exception {
                      NioConfig.registered(new KryoSerializer());
                      final Handler invocationHandler = new ClientHandler();
                      NioConfig.registered(invocationHandler);
                      NioConfig.registered(new Connector("connector",new ReactorPool(1)));
                      NioConfig.initSessionHandler();

                      URL url = new URL();
                      url.setHost("localhost");
                      url.setPort(6161);
                      Invoker invoker = new SimpleInvoker(url,50);

                      ProxyClient proxyClient = new ProxyClient(invoker);
                      final AtomicLong counter = new AtomicLong(0);
                      UserService userService = proxyClient.refService(UserService.class);
                      userService.save(new User());
                clientProxy.close();
            }
        }
### 一个简单的服务 (zookeeper注册中心集群版)
###### 集群版本采用和dubbo一样的基于注册中心无状态的模式。可以说是精简版的dubbo

        public class ZookeeperServer {

            public static void main(String[] args) throws IOException {

                NioConfig.registered(new KryoSerializer());
                final ServerHandler handler = new ServerHandler();
                NioConfig.registered(handler);
                NioConfig.initSessionHandler();

                handler.services.put(UserService.class, new RemoteUserService());
                Server server = new Server(new InetSocketAddress("0.0.0.0", 6161), new Acceptor("acceptor-1", new ReactorPool(1)));

                ZooKeeperClient zooKeeperClient = new DefaultZookeeperClient("192.168.1.66:2181");
                String format = "/eyer/" + URLEncoder.encode(String.format("rpc/%s:%s/Services", "localhost", 6161), "UTF-8");
                zooKeeperClient.create(format, true);
            }
        }
### 服务引用
        public class ZookeeperClient {
            public static void main(String[] args) throws Exception {
                      NioConfig.registered(new KryoSerializer());
                       final Handler invocationHandler = new ClientHandler();
                       NioConfig.registered(invocationHandler);
                       NioConfig.registered(new Connector("ZookeeperProxyServicePerf-connector-1",new ReactorPool(1)));
                       NioConfig.initSessionHandler();


                       Invoker invoker = new ZookeeperInvoker("192.168.1.66:2181",new PollingBalance(),50);

                       ProxyClient proxyClient = new ProxyClient(invoker);


                       UserService userService = proxyClient.refService(UserService.class);
                System.out.println(save);
                clientProxy.close();
            }
        }
