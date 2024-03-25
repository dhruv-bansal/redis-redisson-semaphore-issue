## PREFACE
This project is reference to quicky reproduce the Semaphore release issue with redis multi shard cluster.

## Steps of reproduction
### Prerequisites
- Cluster of redis with more than one shard.
  - In our use case we are leveraging AWS Elasticache with Redis 7.1.0
- Java 17
- Checkout and build this codebase
  - Build using **skipping the test cases**.

### Steps
- Configure the redis details in the application.yml in the resources folder.
  - replace _hostname_ and cluster _port_.
- Run two instances of application
  - e.g. application-instance-1 at port **8082** and application-instance-2 at port **8083**
  - _For Intellj run configuration files has already been committed_
- Use following curl to take a lock from once application instance e.g. from **application-instance-1** running on port 8082
  - e.g I am taking lock for 40 secs
  ```shell
    curl --location 'http://localhost:8082/redis/operation' \
    --header 'Content-Type: application/json' \
    --data '{
    "cache":"123",
    "leaseTime": "40000",
    "initialPermit" : 1,
    "operationName": "ACQUIRE"
    }'
  ```
- Get the permitId from the logs of the application instance on which lock has been taken
  - log line reference
```text
2024-03-25T10:58:16.397-07:00  INFO 72718 --- [nio-8082-exec-2] o.t.e.s.service.RedisOperationService    : Acquired lock with permit id 34d3c8042dfe198386d5fc4b2d3f806f
```
- Use the other application instance to release the lock using the above fetched permitID and following command
  - e.g. using **application-instance-2** running on port 8083
```shell
curl --location 'http://localhost:8083/redis/operation' \
--header 'Content-Type: application/json' \
--data '{
    "cache":"123",
    "leaseTime": "3000",
    "operationName": "RELEASE",
    "permitId": "<above fetched permitId>"
}'
```
#### Expected Behaviour
The expected behaviour is application-instance-1 thread should be unblocked and response should be returned immediately the lock is released from application-instance-2

#### Actual Behaviour
But application-instance-1 lock is getting released after the lease time of the lock is expired not before.

### Note
This behaviour works fine when are using sentinel mode and also when the lock is released from the same instance from which lock is taken.