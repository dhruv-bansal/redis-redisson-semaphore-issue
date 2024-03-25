package org.techpleiad.exploration.springredissemaphorissue.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CacheOperation {

    private String cache;
    private Object cacheValue;
    private int leaseTime;
    private int initialPermit;
    private String permitId;
    private String operationName;
    private long lockReleaseTime;
    private Object responseValue;

    public enum Name {
        ACQUIRE, RELEASE, SETOBJECT, GETOBJECT;

        public static Name fromString(String name) {
            for (Name operation : Name.values()) {
                if (operation.name().equalsIgnoreCase(name)) {
                    return operation;
                }
            }
            throw new IllegalArgumentException("Invalid operation name");
        }
    }

    public Name getName() {
        return Name.fromString(operationName);
    }


}
