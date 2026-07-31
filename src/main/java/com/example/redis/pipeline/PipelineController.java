package com.example.redis.pipeline;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis/pipeline")
public class PipelineController {

    private final RedisPipelineService pipelineService;

    public PipelineController(RedisPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping("/set")
    public ResponseEntity<Pair<String, List<Object>>> set() {
        var result= pipelineService.pipelineExample();
        return ResponseEntity.ok(Pair.of(
                "result", result
        ));
    }


}
