package com.pool.configuration.batch.reader;

import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.stereotype.Component;

@Component
public class WeWishItemReader<T> implements ItemReader<T> {

	private ItemReader<T> delegate;
	
	private List<T> readerItems;

	public WeWishItemReader() {

	}

	public ItemReader<T> getDelegate() {
		return delegate;
	}

	public void setDelegate(ItemReader<T> delegate) {
		this.delegate = delegate;
	}

	public List<T> getReaderItems() {
		return readerItems;
	}

	public void setReaderItems(List<T> readerItems) {
		this.readerItems = readerItems;
	}

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if (delegate == null) {
			delegate = new IteratorItemReader(readerItems);
		}
		return delegate.read();
	}

	@Override
	public String toString() {
		return "WeWishItemReader [delegate=" + delegate + ", readerItems=" + readerItems + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(delegate, readerItems);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeWishItemReader other = (WeWishItemReader) obj;
		return Objects.equals(delegate, other.delegate) && Objects.equals(readerItems, other.readerItems);
	}

}
