package com.sky.lock;

@SuppressWarnings("all")
public class DistributedLockUtil {

    private static int expireMsecs  = 10 * 1000; //锁超时，防止线程在入锁以后，无限的执行等待
    private static int timeoutMsecs = 10 * 1000; //锁等待，防止线程饥饿
    private static boolean locked = false; //是否已经获取锁

    private DistributedLockUtil(){}

    /**
     * 获取指定键值的锁,同时设置获取锁超时时间和锁过期时间
     * @param lockName 锁的键值
     * @param timeoutMsecs 获取锁超时时间
     * @param expireMsecs 锁失效时间
     */
    public synchronized static boolean acquire(String lockName){ //lockName可以为共享变量名，也可以为方法名，主要是用于模拟锁信息
        int timeout = timeoutMsecs;
        try {
            System.out.println(Thread.currentThread() + "开始尝试加锁！");
            while (timeout>=0){
                //System.out.println(Thread.currentThread() + "开始尝试加锁！");
                // set if not exits，若该key-value不存在，则成功加入缓存并且返回1，否则返回0
                Long result = RedisPoolUtil.setnx(lockName,String.valueOf(System.currentTimeMillis()+ expireMsecs + 1));
                if (result!=null&&result.intValue()==1){
                    System.out.println(Thread.currentThread() + "加锁成功！");
                    RedisPoolUtil.expire(lockName, expireMsecs/1000);
                    System.out.println(Thread.currentThread() + "执行业务逻辑！");
                    locked = true;
                    return true;
                }else{
                    String lockValueA = RedisPoolUtil.get(lockName);
                    if (lockValueA != null && Long.parseLong(lockValueA) < System.currentTimeMillis()){
                        //获取上一个锁到期时间，并设置现在的锁到期时间，
                        //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                        String lockValueB = RedisPoolUtil.getSet(lockName, String.valueOf(System.currentTimeMillis() + expireMsecs + 1));
                        //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                        if (lockValueB != null && lockValueB.equals(lockValueA)){
                            System.out.println(Thread.currentThread() + "加锁成功！");
                            RedisPoolUtil.expire(lockName, expireMsecs/1000);
                            System.out.println(Thread.currentThread() + "执行业务逻辑！");
                            locked = true;
                            return true;
                        }
                    }
                }
                timeout -= 100;
                Thread.sleep(100);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁
     */
    public static void release(String lockName) {

        try {
            if (locked) {
                String currentValueStr = RedisPoolUtil.get(lockName);; //redis里的时间
                //校验是否超过有效期，如果不在有效期内，那说明当前锁已经失效，不能进行删除锁操作
                if (currentValueStr != null) {
                    RedisPoolUtil.del(lockName);
                    locked = false;
                }
            }
        } catch (Exception e) {
        }
    }

}
