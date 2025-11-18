package com.transport.subscription.repository;

import com.transport.subscription.entity.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

    List<SubscriptionHistory> findBySubscription_SubscriptionIdOrderByEventDateDesc(Integer subscriptionId);
}

