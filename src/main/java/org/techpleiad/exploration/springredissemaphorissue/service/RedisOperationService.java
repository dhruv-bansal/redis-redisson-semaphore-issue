package org.techpleiad.exploration.springredissemaphorissue.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.techpleiad.exploration.springredissemaphorissue.model.CacheOperation;

@Slf4j
@Service
@AllArgsConstructor
public class RedisOperationService {

    public PlatformCache platformCache;


    public Object doOperation(CacheOperation operation) throws InterruptedException {

        Object response = null;
        switch (operation.getName()) {
            case ACQUIRE:
                acquirePermissionOnSemaphore(operation);
                break;
            case RELEASE:
                releasePermissionOnSemaphore(operation);
                break;
            case SETOBJECT:
                Object responseValue = setObject(operation);
                operation.setCacheValue(responseValue);
                break;
            case GETOBJECT:
                getObject(operation);
                break;
            default:
                log.error("Invalid operation");
        }
        return response;
    }

    String acquirePermissionOnSemaphore(CacheOperation operation) throws InterruptedException {
        platformCache
                .createPermitExpirableSemaphore(operation.getCache(), operation.getInitialPermit());
        log.info("Acquiring lock on cache key {}", operation.getCache());
        String permitID = platformCache.acquirePermissionOnSemaphore(operation.getCache(), operation.getLeaseTime());

        operation.setPermitId(permitID);
        final long startTime = System.currentTimeMillis();

        log.info("Acquired lock with permit id {}", permitID);
        final String secondPermitId = platformCache.acquirePermissionOnSemaphore(operation.getCache(), -1);
        platformCache.releasePermissionOnSemaphore(operation.getCache(), secondPermitId);
        final long endTime = System.currentTimeMillis();
        final long totalTime = endTime - startTime;
        operation.setLockReleaseTime(totalTime);

        log.info("Thread unblocked for the permit id {} in {} seconds", permitID, totalTime / 1000);
        return permitID;
    }

    void releasePermissionOnSemaphore(CacheOperation operation) {
        log.info("release semaphor for cache {} and permit {}", operation.getCache(), operation.getPermitId());
        platformCache.releasePermissionOnSemaphore(operation.getCache(), operation.getPermitId());
    }

    Object getObject(CacheOperation operation) {
        Object val = platformCache.getObject(operation.getCache());
        operation.setCacheValue(val);
        return val;
    }

    Object setObject(CacheOperation operation) {
        Object val = platformCache.setObject(operation.getCache(), operation.getCacheValue());
        log.info("Set operation performed with value {} and key {}", operation.getCache(), operation.getCacheValue());
        return val;
    }
}
