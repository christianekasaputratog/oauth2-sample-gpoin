package org.gvm.product.gvmpoin.module.journalentry;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JournalEntryRepository extends BaseRepository<JournalEntry, Long> {

  @Query("SELECT je FROM JournalEntry je WHERE je.trialBalance.id = :trialBalanceId AND "
      + "((je.credit > 0 AND je.debit = 0) OR (je.credit = 0 AND je.debit > 0))")
  Page<JournalEntry> findByTrialBalanceId(@Param("trialBalanceId") Long trialBalanceId,
      Pageable pageable);

  Optional<JournalEntry> findOneById(Long id);

  Optional<JournalEntry> findOneByClientTransactionId(String clientTransactionId);

  @Query(value = "SELECT COUNT(a.id) FROM JournalEntry a "
      + " WHERE a.clientTransactionId = :ctid AND a.activity = 'ROLLBACK'")
  Long countRollbackTransaction(@Param("ctid") String clientTransactionId);

  @Query("SELECT SUM(je.credit) FROM JournalEntry je WHERE DATE(transactionTime) ="
      + " CURRENT_DATE AND je.consumer.psId = :psId AND je.activityObject != :activityObject")
  Integer sumConsumerCreditTodayExcludeActivityObject(@Param("psId") String psId,
      @Param("activityObject") String activityObject);
}
