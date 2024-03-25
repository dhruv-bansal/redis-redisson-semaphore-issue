package org.techpleiad.exploration.springredissemaphorissue.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.techpleiad.exploration.springredissemaphorissue.model.CacheOperation;
import org.techpleiad.exploration.springredissemaphorissue.service.RedisOperationService;

@RestController
@AllArgsConstructor
@RequestMapping("/redis")
public class RedisClientController {

    private RedisOperationService redisOperationService;

    @PostMapping("/operation")
    ResponseEntity<String> doOperation(@RequestBody CacheOperation operation) throws InterruptedException {

        Object val = redisOperationService.doOperation(operation);
        StringBuilder response = new StringBuilder();
        response.append("Operation: ")
                .append(operation).
                append(" completed");
        if (val != null) {
            response.append(" with value: ")
                    .append(val);
        }
        return ResponseEntity.ok(response.toString());
    }

}
