package com.pool.config.writer;

import com.pool.entity.TitleEntity;
import com.pool.repository.TitleRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service("sboTitleWriter")
public class HboTitleWriter implements ItemWriter<TitleEntity> {

    private final TitleRepository titleRepository;

    public HboTitleWriter(TitleRepository titleRepository) {
        this.titleRepository = titleRepository;
    }

    @Override
    public void write(Chunk<? extends TitleEntity> chunk) throws Exception {
        titleRepository.saveAll(chunk.getItems());
    }
}
