package com.pool.service;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import com.pool.configuration.batch.LaptopBatchConfiguration;
import com.pool.configuration.batch.reader.LaptoptemReader;
import com.pool.configuration.batch.reader.WeWishItemReader;
import com.pool.domin.Laptop;
import com.pool.domin.WeWish;
import com.pool.repository.LaptopRepository;

@Component
public class WeWishBatchService {
	// @Autowired
	// private WeWishBatchConfiguration weWishBatchConfiguration;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private LaptopRepository laptopRepository;

	@Qualifier("laptopJob")
	@Autowired
	private Job laptopJob;

	@Autowired
	private LaptopBatchConfiguration laptopBatchConfiguration;

	public void testBatch(List<WeWish> weWishs) {
		WeWishItemReader<WeWish> userItemReader = new WeWishItemReader<WeWish>();
		userItemReader.setReaderItems(weWishs);
		// weWishBatchConfiguration.setReader(userItemReader);
		// try {
		// JobExecution jobExecution =
		// jobLauncher.run(weWishBatchConfiguration.weWishJob(), new
		// JobParametersBuilder()
		// .addLong("uniqueness", System.nanoTime()).toJobParameters());
		// } catch (JobExecutionAlreadyRunningException | JobRestartException |
		// JobInstanceAlreadyCompleteException
		// | JobParametersInvalidException e) {

		// e.printStackTrace();
		// }
	}

	public void manualDataBuilder() {
		List<Laptop> laptops = IntStream.range(0, 1)
				.mapToObj(num -> new Laptop(String.valueOf(num), Integer.parseInt(
						String.valueOf(num))))
				.toList();

		// param.put("laptops", new JobParameter(laptops));

		LaptoptemReader<Laptop> userItemReader = new LaptoptemReader<Laptop>();
		userItemReader.setReaderItems(laptops);
		laptopBatchConfiguration.setLaptoptemReader(userItemReader);
		try {
			JobExecution jobExecution = jobLauncher.run(
					laptopBatchConfiguration.laptopJob(), new JobParametersBuilder()
							.addLong("laptop", System.nanoTime()).toJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {

			e.printStackTrace();
		}
	}

	public ItemReader<Laptop> laptopItemReader(List<Laptop> laptops) {
		Iterator<Laptop> iterator = laptops.iterator();
		ItemReader<Laptop> itemReader = new ItemReader<Laptop>() {

			@Override
			@Nullable
			public Laptop read()
					throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
				return iterator.hasNext() ? iterator.next() : null;
			}

		};
		return itemReader;
	}
}
