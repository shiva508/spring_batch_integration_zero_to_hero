package com.pool.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("beforeChunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("afterChunk");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.info("afterChunkError");
    }
}
