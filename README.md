# springboot整合mybatis redis实现分布式锁

## 如果是单机情况下（单JVM），线程之间共享内存，只要使用线程锁就可以解决并发问题。
## 如果是分布式情况下（多JVM），线程A和线程B很可能不是在同一JVM中，这样线程锁就无法起到作用了，这时候就要用到分布式锁来解决。

### redis 实现思想：

（1）获取锁的时候，使用setnx加锁，并使用expire命令为锁添加一个超时时间，超过该时间则自动释放锁，锁的value值为一个随机生成的UUID，通过此在释放锁的时候进行判断。

（2）获取锁的时候还设置一个获取的超时时间，若超过这个时间则放弃获取锁。

（3）释放锁的时候，通过UUID判断是不是该锁，若是该锁，则执行delete进行锁释放

### 几个要用到的redis命令：

setnx(key, value)：“set if not exits”，若该key-value不存在，则成功加入缓存并且返回1，否则返回0。</br>
get(key)：获得key对应的value值，若不存在则返回nil。</br>
getset(key, value)：先获取key对应的value值，若不存在则返回nil，然后将旧的value更新为新的value。</br>
expire(key, seconds)：设置key-value的有效期为seconds秒。</br>



### 基于ZooKeeper的实现方式
ZooKeeper是一个为分布式应用提供一致性服务的开源组件，它内部是一个分层的文件系统目录树结构，规定同一个目录下只能有一个唯一文件名。基于ZooKeeper实现分布式锁的步骤如下：

（1）创建一个目录mylock； </br>
（2）线程A想获取锁就在mylock目录下创建临时顺序节点； </br>
（3）获取mylock目录下所有的子节点，然后获取比自己小的兄弟节点，如果不存在，则说明当前线程顺序号最小，获得锁； </br>
（4）线程B获取所有节点，判断自己不是最小节点，设置监听比自己次小的节点； </br>
（5）线程A处理完，删除自己的节点，线程B监听到变更事件，判断自己是不是最小的节点，如果是则获得锁。</br>

这里推荐一个Apache的开源库Curator，它是一个ZooKeeper客户端，Curator提供的InterProcessMutex是分布式锁的实现，acquire方法用于获取锁，release方法用于释放锁。

优点：具备高可用、可重入、阻塞锁特性，可解决失效死锁问题。

缺点：因为需要频繁的创建和删除节点，性能上不如Redis方式。

## PS: nginx 负载均衡实现分布式部署及测试代码

```javascript
http {

    # ... 省略其它配置

    upstream tomcats {
        server 192.168.0.100:8080;
        server 192.168.0.101:8080;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://tomcats;
        }
    }

    # ... 省略其它配置
}

public class Test {

    public static void main(String[] args){

        MyThread r = new MyThread();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i=0;i<30;i++){
            executorService.execute(r);
        }

    }

    static class MyThread implements Runnable{

        public void run() {
            HttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet("http://127.0.0.1:8080/lock/data/test");
            try {
                HttpResponse response = client.execute(request);
                System.out.print(EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

