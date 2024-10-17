package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.SkierServicesImpl;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SkierServicesImplTest {

    @InjectMocks
    private SkierServicesImpl skierServices; // Remplacez par votre implémentation réelle

    @Mock
    private ISkierRepository skierRepository; // Remplacez par votre dépôt réel

    @Mock
    private IRegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddSkier() {
        // Créez un abonnement valide
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL); // Assurez-vous de définir le type d'abonnement
        subscription.setStartDate(LocalDate.now()); // Initialisez la date de début
        subscription.setEndDate(LocalDate.now().plusYears(1)); // Initialisez la date de fin si nécessaire

        // Créez un skieur avec un abonnement
        Skier skier = new Skier();
        skier.setFirstName("John");
        skier.setLastName("Doe");
        skier.setSubscription(subscription); // Assurez-vous que l'abonnement n'est pas nul

        // Simulez le comportement du dépôt
        when(skierRepository.save(skier)).thenReturn(skier);

        // Appelez la méthode à tester
        Skier result = skierServices.addSkier(skier);

        // Vérifiez les résultats
        assertNotNull(result);
        assertEquals(skier, result);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testUnregisterSkierFromCourse() {
        Long numSkier = 1L;
        Long numCourse = 101L;

        // Créer un skieur sans inscription pour le cours
        Skier skier = new Skier();
        skier.setNumSkier(numSkier);
        skier.setRegistrations(new HashSet<>()); // Aucune inscription

        // Simuler que le skieur existe
        when(skierRepository.findById(numSkier)).thenReturn(Optional.of(skier));

        // Vérifier que l'exception est levée pour l'inscription non trouvée
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            skierServices.unregisterSkierFromCourse(numSkier, numCourse);
        });

        assertEquals("Registration not found for course id: " + numCourse, exception.getMessage());

        // Maintenant, ajouter une inscription pour vérifier le cas de désinscription réussie
        Course course = new Course();
        course.setNumCourse(numCourse);

        Registration registration = new Registration();
        registration.setCourse(course);
        registration.setSkier(skier); // Associer l'inscription au skieur
        skier.getRegistrations().add(registration); // Ajouter l'inscription

        // Simuler que le skieur existe avec l'inscription
        when(skierRepository.findById(numSkier)).thenReturn(Optional.of(skier));

        // Simuler que l'inscription est trouvée et préparer la suppression
        doNothing().when(registrationRepository).delete(any(Registration.class));

        // Appeler la méthode à tester
        skierServices.unregisterSkierFromCourse(numSkier, numCourse);

        // Vérifier que l'inscription a été supprimée
        assertTrue(skier.getRegistrations().isEmpty());
        verify(registrationRepository, times(1)).delete(registration);
        verify(skierRepository, times(2)).findById(numSkier); // Vérifiez combien de fois findById a été appelé
    }

    @Test
    void testCalculateTotalRegistrationFees() {
        Long numSkier = 1L;

        // Créer un skieur avec des inscriptions
        Skier skier = new Skier();
        skier.setNumSkier(numSkier);

        // Créer des cours avec des prix
        Course course1 = new Course();
        course1.setPrice(100f); // Prix du cours 1
        Course course2 = new Course();
        course2.setPrice(150f); // Prix du cours 2

        // Créer des inscriptions pour les cours
        Registration registration1 = new Registration();
        registration1.setCourse(course1);
        Registration registration2 = new Registration();
        registration2.setCourse(course2);

        // Ajouter les inscriptions au skieur
        skier.setRegistrations(new HashSet<>(Arrays.asList(registration1, registration2)));

        // Test lorsque le skieur existe
        when(skierRepository.findById(numSkier)).thenReturn(Optional.of(skier));

        // Appeler la méthode à tester
        Float totalFees = skierServices.calculateTotalRegistrationFees(numSkier);

        // Vérifier que le montant total est correct
        Float expectedFees = course1.getPrice() + course2.getPrice();
        assertEquals(expectedFees, totalFees);

        // Vérifier que la méthode findById a été appelée exactement une fois
        verify(skierRepository, times(1)).findById(numSkier);

        // Test lorsque le skieur n'existe pas
        when(skierRepository.findById(numSkier)).thenReturn(Optional.empty());

        // Vérifier que l'exception est lancée lorsque le skieur n'est pas trouvé
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            skierServices.calculateTotalRegistrationFees(numSkier);
        });

        assertEquals("Skier not found with id: " + numSkier, exception.getMessage());

        // Vérifier que la méthode findById a été appelée exactement une fois
        verify(skierRepository, times(2)).findById(numSkier);
    }

    @Test
    void testAnalyzeSkierEngagement() {
        // Test avec des skieurs et enregistrements
        Skier skier1 = new Skier();
        skier1.setNumSkier(1L);
        skier1.setRegistrations(new HashSet<>(Arrays.asList(new Registration(), new Registration()))); // 2 enregistrements

        Skier skier2 = new Skier();
        skier2.setNumSkier(2L);
        skier2.setRegistrations(new HashSet<>(Collections.singletonList(new Registration()))); // 1 enregistrement

        // Simulation du comportement du repository
        List<Skier> allSkiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findAll()).thenReturn(allSkiers);

        // Appel de la méthode à tester
        Map<String, Object> result = skierServices.analyzeSkierEngagement();

        // Assertions pour le cas avec des skieurs
        assertEquals(1.5, result.get("averageCoursesPerSkier"));
        assertEquals(skier1, result.get("mostActiveSkier"));

        // Test avec une liste vide
        when(skierRepository.findAll()).thenReturn(new ArrayList<>());

        // Appel de la méthode à tester
        result = skierServices.analyzeSkierEngagement();

        // Assertions pour le cas vide
        assertEquals(0.0, result.get("averageCoursesPerSkier"));
        assertNull(result.get("mostActiveSkier"));
    }

    @Test
    void testNombreSkiersParColorPiste() {
        // Préparation des données de test
        Color color1 = Color.BLUE; // Assurez-vous que cette couleur existe dans votre enum
        Color color2 = Color.RED;

        // Simulation des résultats renvoyés par le repository
        when(skierRepository.skiersByColorPiste(color1)).thenReturn(Arrays.asList(new Skier(), new Skier())); // 2 skieurs
        when(skierRepository.skiersByColorPiste(color2)).thenReturn(Arrays.asList(new Skier())); // 1 skieur

        // Appel de la méthode à tester
        HashMap<Color, Integer> result = skierServices.nombreSkiersParColorPiste();

        // Vérification des résultats
        assertEquals(2, result.get(color1)); // Vérifie que la couleur BLEUE a 2 skieurs
        assertEquals(1, result.get(color2)); // Vérifie que la couleur ROUGE a 1 skieur

        // Vérification que les autres couleurs ont 0 skieurs
        for (Color c : Color.values()) {
            if (c != color1 && c != color2) {
                assertEquals(0, result.get(c)); // Vérifie que les autres couleurs ont 0 skieurs
            }
        }
    }

    @Test
    void testGetSkiersBySubscriptionExpiry() {
        // Préparer les dates de test
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1);

        // Créer des skieurs avec des abonnements
        Subscription validSubscription = new Subscription();
        validSubscription.setEndDate(today.plusDays(10)); // Abonnement valide

        Subscription expiredSubscription = new Subscription();
        expiredSubscription.setEndDate(today.minusDays(1)); // Abonnement expiré

        // Skieur avec un abonnement valide
        Skier skier1 = new Skier();
        skier1.setSubscription(validSubscription);

        // Skieur avec un abonnement expiré
        Skier skier2 = new Skier();
        skier2.setSubscription(expiredSubscription);

        // Simuler le comportement du repository
        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));

        // Appeler la méthode à tester
        List<Skier> result = skierServices.getSkiersBySubscriptionExpiry();

        // Vérifier les résultats
        assertEquals(1, result.size()); // S'attendre à un seul skieur
        assertEquals(skier1, result.get(0)); // Vérifier que c'est le bon skieur
    }
}
