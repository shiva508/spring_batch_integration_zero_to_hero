package com.pool.config.writer;

import com.pool.entity.CreditEntity;
import com.pool.repository.CreditRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Service;

@Service("hboCreditWriter")
@AllArgsConstructor
public class HboCreditWriter implements ItemWriter<CreditEntity> {

    private final CreditRepository creditRepository;

    @Override
    public void write(Chunk<? extends CreditEntity> chunk) throws Exception {
        creditRepository.saveAll(chunk.getItems());
    }

}
