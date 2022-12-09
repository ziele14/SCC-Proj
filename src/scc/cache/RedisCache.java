package scc.cache;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCache {
//    private static final String RedisHostname = "cacheprojekt.redis.cache.windows.net";
    private static final String RedisHostname = "matty-cache";

//    private static final String RedisKey = "";
//    private static final String RedisKey = "jzppIqO2I4VYMc4Kelxps00IXeg15GoQDAzCaADCOHs=";

    private static JedisPool instance;

    public synchronized static JedisPool getCachePool() {
        if( instance != null)
            return instance;
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        instance = new JedisPool(poolConfig, RedisHostname, 6379, 1000, false);
        return instance;

    }
}

