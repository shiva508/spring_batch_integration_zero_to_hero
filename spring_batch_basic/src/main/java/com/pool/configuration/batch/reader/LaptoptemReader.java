package com.pool.configuration.batch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class LaptoptemReader<T> implements ItemReader<T> {

    private ItemReader<T> delegate;

    private List<T> readerItems;

    private Iterator<T> iterator;

    public LaptoptemReader() {

    }

    @Override
    // @Nullable
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return iterator.hasNext() ? iterator.next() : null;
    }

    public void setReaderItems(List<T> readerItems) {
        this.readerItems = readerItems;
        iterator = readerItems.iterator();

    }

    public void setDelegate(ItemReader<T> delegate) {
        this.delegate = delegate;
    }

    public ItemReader<T> getDelegate() {
        return delegate;
    }

    public List<T> getReaderItems() {
        return readerItems;
    }

}
