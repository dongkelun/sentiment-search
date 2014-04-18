package zx.soft.spider.cache.factory;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisConnectionException;
import zx.soft.spider.cache.dao.Cache;
import zx.soft.spider.cache.redis.impl.RedisCache;
import zx.soft.spider.cache.utils.Config;
import zx.soft.spider.cache.utils.RetryHandler;

/**
 * 缓存工厂类
 * @author wanggang
 *
 */
public class CacheFactory {

	private static Logger logger = LoggerFactory.getLogger(CacheFactory.class);

	private static Cache instance;

	static {
		try {
			instance = (Cache) Proxy.newProxyInstance(Cache.class.getClassLoader(), new Class[] { Cache.class },
					new RetryHandler<Cache>(new RedisCache(Config.get("redisServers"), Integer.parseInt(Config
							.get("port")), Config.get("password")), 5000, 10) {
						@Override
						protected boolean isRetry(Throwable e) {
							return e instanceof JedisConnectionException;
						}
					});
		} catch (Exception e) {
			logger.error("CacheFactory Exception is " + e);
			throw new RuntimeException(e);
		}
	}

	public static Cache getInstance() {
		return instance;
	}

}
