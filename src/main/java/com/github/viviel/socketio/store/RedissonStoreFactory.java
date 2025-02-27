/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.viviel.socketio.store;

import com.github.viviel.socketio.store.pubsub.BaseStoreFactory;
import com.github.viviel.socketio.store.pubsub.PubSubStore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.UUID;

public class RedissonStoreFactory extends BaseStoreFactory {

    private final RedissonClient redisClient;
    private final RedissonClient redisPub;
    private final RedissonClient redisSub;

    private final PubSubStore pubSubStore;

    public RedissonStoreFactory() {
        this(Redisson.create());
    }

    public RedissonStoreFactory(RedissonClient redisson) {
        this.redisClient = redisson;
        this.redisPub = redisson;
        this.redisSub = redisson;

        this.pubSubStore = new RedissonPubSubStore(redisPub, redisSub, getNodeId());
    }

    public RedissonStoreFactory(Redisson redisClient, Redisson redisPub, Redisson redisSub) {
        this.redisClient = redisClient;
        this.redisPub = redisPub;
        this.redisSub = redisSub;

        this.pubSubStore = new RedissonPubSubStore(redisPub, redisSub, getNodeId());
    }

    @Override
    public Store createStore(UUID sessionId) {
        return new RedissonStore(sessionId, redisClient);
    }

    @Override
    public PubSubStore pubSubStore() {
        return pubSubStore;
    }

    @Override
    public void shutdown() {
        redisClient.shutdown();
        redisPub.shutdown();
        redisSub.shutdown();
    }

    @Override
    public <K, V> Map<K, V> createMap(String name) {
        return redisClient.getMap(name);
    }
}
