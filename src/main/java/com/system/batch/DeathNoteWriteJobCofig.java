package com.system.batch;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.RecordFieldExtractor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Configuration
public class DeathNoteWriteJobCofig {

  @Bean
  public Job deathNoteWriteJob(JobRepository jobRepository, Step deathNoteWriteStep) {
    return new JobBuilder("deathNoteWriteJob", jobRepository)
        .start(deathNoteWriteStep)
        .build();
  }

  @Bean
  public Step deathNoteWriteStep(JobRepository jobRepository, 
                                  PlatformTransactionManager transactionManager, 
                                  ListItemReader<DeathNote> deathNoteListReader,
                                  FlatFileItemWriter<DeathNote> deathNoteWriter) {
    return new StepBuilder("deathNoteWriteStep", jobRepository)
        .<DeathNote, DeathNote>chunk(10, transactionManager)
        .reader(deathNoteListReader)
        .writer(deathNoteWriter)
        .build();
  }

  @Bean
  public ListItemReader<DeathNote> deathNoteListReader(){
    List<DeathNote> victims = List.of(
      new DeathNote("KILL-001","김배치","2024-01-05","CPU 과부하"),
      new DeathNote("KILL-002","사브링","2024-01-26","JVM 스택오버플로우"),
      new DeathNote("KILL-003","박탐묘","2024-01-27","힙 메모리 고갈")
    );

    return new ListItemReader<>(victims);
  }
    
  @Bean
  @StepScope
  public FlatFileItemWriter<DeathNote> deathNoteWriter(@Value("#{jobParameters['outputDir']}") String outputDir) {
    // return new FlatFileItemWriterBuilder<DeathNote>()
    //   .name("deathNoteWriter")
    //   .resource(new FileSystemResource(outputDir + "/death_notes.csv"))
    //   .delimited()
    //   .delimiter(",")
    //   //.sourceType(DeathNote.class)  // 원하는 컬럼만 넣기 위해 sourceType과 names는 주석 처리
    //   //.names("victimId", "victimName", "executionDate", "causeOfDeath")
    //   //.fieldExtractor(fieldExtractor()) // 원하는 컬럼만 넣고 싶을때
    //   .names("victimId","victimName","executionDate","causeOfDeath")
    //   .headerCallback(writer -> writer.write("처형ID, 피해자명, 처형일자, 사인"))
    //   .build();

    return new FlatFileItemWriterBuilder<DeathNote>()
      .name("deathNoteWriter")
      .resource(new FileSystemResource(outputDir + "/death_note_report.txt"))
      .formatted()
      //.format("처형 ID: %s | 처형일자 : %s | 피해자: %s | 사인: %s")
      .format("처형 ID: %s | 처형일자 : %s | 피해자: %s")
      .sourceType(DeathNote.class)
      .names("victimId","executionDate","victimName","causeOfDeath")
      .headerCallback(writer -> writer.write("================ 처형 기록부 ========================="))
      .footerCallback(writer -> writer.write("================ 처형 완료 ==========================="))
      .build();
  }

  public RecordFieldExtractor<DeathNote> fieldExtractor(){  
    RecordFieldExtractor<DeathNote> fieldExtractor = new RecordFieldExtractor<>(DeathNote.class);
    fieldExtractor.setNames("victimId", "executionDate", "causeOfDeath");
    return fieldExtractor;
  }

  // @Data
  // @AllArgsConstructor
  // public static class DeathNote {
  //   private String victimId;
  //   private String victimName;
  //   private String executionDate;
  //   private String causeOfDeath;
  // }

  public record DeathNote(
    String victimId,
    String victimName,
    String executionDate,
    String causeOfDeath
) {}
}
