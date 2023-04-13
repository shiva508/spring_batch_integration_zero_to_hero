package com.pool.config.writer;

import com.pool.entity.TitleEntity;
import com.pool.repository.TitleRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Service;

@Service("hboTitleWriter")
@AllArgsConstructor
public class HboTitleWriter implements ItemWriter<TitleEntity> {

    private final TitleRepository titleRepository;

    @Override
    public void write(Chunk<? extends TitleEntity> chunk) throws Exception {
        titleRepository.saveAll(chunk.getItems());
    }

}
