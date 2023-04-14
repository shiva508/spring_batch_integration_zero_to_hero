package com.pool.config.reader;

import com.pool.entity.CreditBackupEntity;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CreditBackupReader implements ItemReader<CreditBackupEntity> {
    @Override
    public CreditBackupEntity read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}
