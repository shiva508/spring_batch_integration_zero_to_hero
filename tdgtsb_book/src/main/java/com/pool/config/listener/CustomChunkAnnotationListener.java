package com.pool.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomChunkAnnotationListener {
    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        log.info("beforeChunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        log.info("afterChunk");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        log.info("afterChunkError");
    }
}
