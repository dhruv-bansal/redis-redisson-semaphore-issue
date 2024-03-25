package org.techpleiad.exploration.springredissemaphorissue.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class PlatformCache {

    private RedissonClient client;

    public void createPermitExpirableSemaphore(final String cachekey, final int initialPermits) {
        log.debug("Creating semaphore for key - {}", cachekey);
        final RPermitExpirableSemaphore semaphore = client.getPermitExpirableSemaphore(cachekey);
        semaphore.trySetPermits(initialPermits);
    }

    public String acquirePermissionOnSemaphore(final String cachekey, final int leaseTime) throws InterruptedException {
        log.debug("Acquiring permission on semaphore for key - {}", cachekey);
        final RPermitExpirableSemaphore semaphore = client.getPermitExpirableSemaphore(cachekey);
        return semaphore.acquire(leaseTime, TimeUnit.MILLISECONDS);
    }

    public void releasePermissionOnSemaphore(final String cachekey, final String permit) {
        log.debug("Releasing permission on semaphore for key - {}", cachekey);
        final RPermitExpirableSemaphore semaphore = client.getPermitExpirableSemaphore(cachekey);
        semaphore.release(permit);
    }

    public Object getObject(final String cacheKey) {
        final RBucket<Object> obj = client.getBucket(cacheKey);
        return obj.get();
    }


    public Object setObject(final String cacheKey, final Object value) {
        final RBucket<Object> obj = client.getBucket(cacheKey);
        obj.set(value);
        return obj.get();
    }

}
