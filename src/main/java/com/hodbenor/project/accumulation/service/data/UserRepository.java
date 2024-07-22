package com.hodbenor.project.accumulation.service.data;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodbenor.project.accumulation.service.data.beans.User;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

@Repository
public class UserRepository {
    private static final String USER_PREFIX = "user:";
    private static final String USER_POINTS_PREFIX = "user:points:";
    private static final String USER_SPINS_PREFIX = "user:spins:";
    private static final String USER_COINS_PREFIX = "user:coins:";
    private static final String LOCK_KEY_POINTS = "resource_lock_points";
    private static final String LOCK_KEY_SPINS = "resource_lock_spins";
    private static final String LOCK_KEY_COINS = "resource_lock_coins";
    private final JedisPool jedisPool;

    public UserRepository() {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
    }

    public void saveUser(User user) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String userJson = objectMapper.writeValueAsString(user);
            jedis.set(USER_PREFIX + user.userId(), userJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(long userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String userJson = jedis.get(USER_PREFIX + userId);
            if (userJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(userJson, User.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public int getPointsBalance(long userId) {
        return getBalance(userId, USER_POINTS_PREFIX);
    }

    public int getSpinsBalance(long userId) {
        return getBalance(userId, USER_SPINS_PREFIX);
    }

    public int getCoinsBalance(long userId) {
        return getBalance(userId, USER_COINS_PREFIX);
    }

    public void incrUserPointsBalance(long userId, long incrBy) {
        incrUserBalance(userId, incrBy, USER_POINTS_PREFIX, LOCK_KEY_POINTS);
    }

    public void incrUserSpinsBalance(long userId, long incrBy) {
        incrUserBalance(userId, incrBy, USER_SPINS_PREFIX, LOCK_KEY_SPINS);
    }

    public void incrUserCoinsBalance(long userId, long incrBy) {
        incrUserBalance(userId, incrBy, USER_COINS_PREFIX, LOCK_KEY_COINS);
    }

    public void decrUserPointsBalance(long userId, long decrBy) {
        decrUserBalance(userId, decrBy, USER_POINTS_PREFIX, LOCK_KEY_POINTS);
    }

    public void decrUserSpinsBalance(long userId, long decrBy) {
        decrUserBalance(userId, decrBy, USER_SPINS_PREFIX, LOCK_KEY_SPINS);
    }

    public void decrUserCoinsBalance(long userId, long decrBy) {
        decrUserBalance(userId, decrBy, USER_COINS_PREFIX, LOCK_KEY_COINS);
    }

    private void incrUserBalance(long userId, long incrBy, String keyPrefix, String lockKey) {
        RedisLock lock = null;
        try (Jedis jedis = jedisPool.getResource()) {
            lock = new RedisLock(jedis, lockKey);
            if (lock.acquireLock()) {
                jedis.incrBy(keyPrefix + userId, incrBy);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }

    private int getBalance(long userId, String keyPrefix) {
        try (Jedis jedis = jedisPool.getResource()) {
            String balance = jedis.get(keyPrefix + userId);
            if (balance != null) {
                return Integer.parseInt(balance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    private void decrUserBalance(long userId, long decrBy, String lockKey, String keyPrefix) {
        RedisLock lock = null;
        try (Jedis jedis = jedisPool.getResource()) {
            lock = new RedisLock(jedis, lockKey);
            if (lock.acquireLock()) {
                jedis.decrBy(keyPrefix + userId, decrBy);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
        }
    }

    public void getUserItemsBalance(long userId, Map<String, Integer> userItemsBalance) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String userItemsBalanceJson = objectMapper.writeValueAsString(userItemsBalance);
            jedis.set(USER_BALANCE_PREFIX + userId, userItemsBalanceJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
