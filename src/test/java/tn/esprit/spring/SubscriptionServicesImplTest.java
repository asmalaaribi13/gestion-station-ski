package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.SubscriptionServicesImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SubscriptionServicesImplTest {

    @Autowired
    private SubscriptionServicesImpl subscriptionService;

    private Long testSubscriptionId;

    @BeforeEach
    public void setup() {
        // Créer des données de test
        Subscription subscription1 = new Subscription();
        subscription1.setStartDate(LocalDate.of(2023, 1, 1));
        subscription1.setEndDate(LocalDate.of(2023, 12, 31));
        subscription1.setPrice(500f);
        subscription1.setTypeSub(TypeSubscription.ANNUAL);
        Subscription savedSubscription1 = subscriptionService.addSubscription(subscription1);
        testSubscriptionId = savedSubscription1.getNumSub(); // Initialize the testSubscriptionId

        Subscription subscription2 = new Subscription();
        subscription2.setStartDate(LocalDate.of(2023, 6, 1));
        subscription2.setEndDate(LocalDate.of(2024, 5, 31));
        subscription2.setPrice(300f);
        subscription2.setTypeSub(TypeSubscription.MONTHLY);
        subscriptionService.addSubscription(subscription2);
    }

    @Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(500f);

        Subscription savedSubscription = subscriptionService.addSubscription(subscription);
        assertNotNull(savedSubscription);
        assertEquals(TypeSubscription.ANNUAL, savedSubscription.getTypeSub());
        assertEquals(500f, savedSubscription.getPrice());
    }

    @Test
    public void testUpdateSubscription() {
        // Récupérer l'abonnement existant
        Subscription subscriptionToUpdate = subscriptionService.retrieveSubscriptionById(testSubscriptionId);
        assertNotNull(subscriptionToUpdate, "Subscription should not be null");

        // Modifier l'abonnement
        subscriptionToUpdate.setPrice(600f);
        Subscription updatedSubscription = subscriptionService.updateSubscription(subscriptionToUpdate);

        // Vérifier que les modifications ont été appliquées
        assertNotNull(updatedSubscription, "Updated subscription should not be null");
        assertEquals(600f, updatedSubscription.getPrice(), "Price should be updated to 600");
    }

    @Test
    public void testRetrieveSubscriptionById() {
        Subscription retrievedSubscription = subscriptionService.retrieveSubscriptionById(testSubscriptionId);
        assertNotNull(retrievedSubscription, "Subscription should not be null");
        assertEquals(testSubscriptionId, retrievedSubscription.getNumSub(), "Should return the correct subscription by ID");
    }








    @Test
    public void testGetSubscriptionByType() {
        Set<Subscription> subscriptions = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);
        assertNotNull(subscriptions, "Subscriptions set should not be null");
        assertTrue(subscriptions.size() > 0, "Should return at least one subscription");
    }

    @Test
    public void testRetrieveSubscriptionsByDates() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        List<Subscription> subscriptions = subscriptionService.retrieveSubscriptionsByDates(startDate, endDate);
        assertNotNull(subscriptions, "Subscriptions list should not be null");
        assertTrue(subscriptions.size() > 0, "Should return at least one subscription within the date range");
    }

    @Test
    public void testDeleteSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(500f);
        Subscription savedSubscription = subscriptionService.addSubscription(subscription);

        subscriptionService.deleteSubscription(savedSubscription.getNumSub());
        assertNull(subscriptionService.retrieveSubscriptionById(savedSubscription.getNumSub()), "Subscription should be null after deletion");
    }

    @Test
    public void testGetAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        assertNotNull(subscriptions, "Subscriptions list should not be null");
        assertTrue(subscriptions.size() > 0, "Should return at least one subscription");
    }

    @Test
    public void testCalculateTotalRevenue() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Float totalRevenue = subscriptionService.calculateTotalRevenue(startDate, endDate);
        assertNotNull(totalRevenue, "Total revenue should not be null");
        assertTrue(totalRevenue > 0, "Total revenue should be greater than 0");
    }

    @Test
    public void testFindSubscriptionsExpiringSoon() {
        List<Subscription> subscriptions = subscriptionService.findSubscriptionsExpiringSoon();
        assertNotNull(subscriptions, "Subscriptions list should not be null");
        assertTrue(subscriptions.size() > 0, "Should return at least one subscription expiring soon");
    }

    @Test
    public void testCalculateAverageSubscriptionDuration() {
        Float averageDuration = subscriptionService.calculateAverageSubscriptionDuration();
        assertNotNull(averageDuration, "Average duration should not be null");
        assertTrue(averageDuration > 0, "Average duration should be greater than 0");
    }
}