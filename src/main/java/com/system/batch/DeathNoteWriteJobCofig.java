package com.system.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DeathNoteWriteJobCofig {

  @Bean
  public Job deathNoteWriteJob(JobRepository jobRepository, Step deathNotedWriteStep) {
    return new JobBuilder("deathNotedWriteJob", jobRepository)
        .start(deathNoteWriteStep)
        .build();
  }

  @Bean
  public Step deathNotedWriteStep(JobRepository jobRepository, 
                                  PlatformTransactionManager transactionManager, 
                                  ListItemReader<String> deathNotedListReader,
                                  FlatFileItemWriter<DeathNote> deathNoteWriter) {
    return new StepBuilder("deathNoteWriteStep", jobRepository)
        .<DeathNoted, DeathNode>chunk(10, taransationManager)
        .reader(deathNotedListReader)
        .writer(deathNotedWriter)
        .build();
 }
    
  
}
