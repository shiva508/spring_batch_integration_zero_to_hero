package com.pool.config.writer;

import com.pool.entity.TitleEntity;
import com.pool.repository.TitleRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("hboTitleWriter")
@AllArgsConstructor
@StepScope
public class HboTitleWriter implements ItemWriter<TitleEntity> {

    private final TitleRepository titleRepository;

    @Override
    public void write(Chunk<? extends TitleEntity> chunk) throws Exception {
        titleRepository.saveAll(chunk.getItems());
        //titleRepository.flush();
    }

}
