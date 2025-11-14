package com.transport.ticket.repository;

import com.transport.ticket.model.PaymentMethod;
import com.transport.ticket.model.PaymentStatus;
import com.transport.ticket.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du TransactionRepository")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait sauvegarder une transaction")
    void shouldSaveTransaction() {
        Transaction transaction = Transaction.builder()
                .ticketId(1L)
                .montant(new BigDecimal("15.50"))
                .statut(PaymentStatus.COMPLETED)
                .methodePaiement(PaymentMethod.CREDIT_CARD)
                .transactionReference("TXN-TEST-123")
                .build();

        Transaction saved = transactionRepository.save(transaction);

        assertThat(saved).isNotNull();
        assertThat(saved.getIdTransaction()).isNotNull();
        assertThat(saved.getTransactionReference()).isEqualTo("TXN-TEST-123");
        assertThat(saved.getStatut()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Devrait trouver une transaction par sa référence")
    void shouldFindByReference() {
        Transaction transaction = Transaction.builder()
                .ticketId(1L)
                .montant(new BigDecimal("15.50"))
                .statut(PaymentStatus.COMPLETED)
                .methodePaiement(PaymentMethod.CREDIT_CARD)
                .transactionReference("TXN-REF-123")
                .build();

        entityManager.persist(transaction);
        entityManager.flush();

        Optional<Transaction> found = transactionRepository.findByTransactionReference("TXN-REF-123");

        assertThat(found).isPresent();
        assertThat(found.get().getMontant()).isEqualByComparingTo(new BigDecimal("15.50"));
        assertThat(found.get().getMethodePaiement()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    @DisplayName("Devrait trouver toutes les transactions d'un ticket")
    void shouldFindByTicketId() {
        Transaction tx1 = Transaction.builder()
                .ticketId(1L)
                .montant(new BigDecimal("10.00"))
                .statut(PaymentStatus.COMPLETED)
                .methodePaiement(PaymentMethod.CREDIT_CARD)
                .transactionReference("TXN-1")
                .build();

        Transaction tx2 = Transaction.builder()
                .ticketId(1L)
                .montant(new BigDecimal("5.50"))
                .statut(PaymentStatus.COMPLETED)
                .methodePaiement(PaymentMethod.MOBILE_MONEY)
                .transactionReference("TXN-2")
                .build();

        entityManager.persist(tx1);
        entityManager.persist(tx2);
        entityManager.flush();

        List<Transaction> transactions = transactionRepository.findByTicketId(1L);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getTicketId).containsOnly(1L);
    }

    @Test
    @DisplayName("Devrait compter les transactions réussies")
    void shouldCountSuccessfulTransactions() {
        Transaction success1 = createTransaction(PaymentStatus.COMPLETED, "TXN-1");
        Transaction success2 = createTransaction(PaymentStatus.COMPLETED, "TXN-2");
        Transaction failed = createTransaction(PaymentStatus.FAILED, "TXN-3");

        entityManager.persist(success1);
        entityManager.persist(success2);
        entityManager.persist(failed);
        entityManager.flush();

        long count = transactionRepository.countSuccessfulTransactions();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Ne devrait pas trouver de transaction avec une référence invalide")
    void shouldNotFindTransactionWithInvalidReference() {
        Optional<Transaction> found = transactionRepository.findByTransactionReference("INVALID");

        assertThat(found).isEmpty();
    }

    private Transaction createTransaction(PaymentStatus status, String ref) {
        return Transaction.builder()
                .ticketId(1L)
                .montant(new BigDecimal("15.50"))
                .statut(status)
                .methodePaiement(PaymentMethod.CREDIT_CARD)
                .transactionReference(ref)
                .build();
    }
}