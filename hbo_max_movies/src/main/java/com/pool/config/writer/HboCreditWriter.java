package com.pool.config.writer;

import com.pool.entity.CreditEntity;
import com.pool.repository.CreditRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("hboCreditWriter")
@AllArgsConstructor
@StepScope
public class HboCreditWriter implements ItemWriter<CreditEntity> {

    private final CreditRepository creditRepository;

    @Override
    public void write(Chunk<? extends CreditEntity> chunk) throws Exception {
        creditRepository.saveAll(chunk.getItems());
        //creditRepository.flush();
    }

}
