package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private ISkierRepository skierRepository;
    private IPisteRepository pisteRepository;
    private ICourseRepository courseRepository;
    private IRegistrationRepository registrationRepository;
    private ISubscriptionRepository subscriptionRepository;

    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    @Override
    public Skier addSkier(Skier skier) {
        switch (skier.getSubscription().getTypeSub()) {
            case ANNUAL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                break;
            case SEMESTRIEL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                break;
            case MONTHLY:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                break;
        }
        return skierRepository.save(skier);
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElse(null);
        skier.setSubscription(subscription);
        return skierRepository.save(skier);
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.getById(numCourse);
        Set<Registration> registrations = savedSkier.getRegistrations();
        for (Registration r : registrations) {
            r.setSkier(savedSkier);
            r.setCourse(course);
            registrationRepository.save(r);
        }
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier).orElse(null);
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        try {
            skier.getPistes().add(piste);
        } catch (NullPointerException exception) {
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }
        return skierRepository.save(skier);
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }

    /*---------------------------------------------------------------------------------------*/

    //Méthode pour désinscrire un skieur d'un cours spécifique
    /*@Override
    public void unregisterSkierFromCourse(Long numSkier, Long numCourse) {
        Skier skier = skierRepository.findById(numSkier).orElseThrow(() ->
                new EntityNotFoundException("Skier not found with id: " + numSkier)
        );
        Registration registrationToRemove = skier.getRegistrations().stream()
                .filter(r -> r.getCourse().getNumCourse().equals(numCourse))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Registration not found for course id: " + numCourse)
                );
        registrationRepository.delete(registrationToRemove);
    }*/
    //Méthode pour désinscrire un skieur d'un cours spécifique
    @Override
    public void unregisterSkierFromCourse(Long numSkier, Long numCourse) {
        Skier skier = skierRepository.findById(numSkier).orElseThrow(() ->
                new EntityNotFoundException("Skier not found with id: " + numSkier)
        );

        Registration registrationToRemove = skier.getRegistrations().stream()
                .filter(r -> r.getCourse().getNumCourse().equals(numCourse))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Registration not found for course id: " + numCourse)
                );

        // Retirer l'enregistrement de la collection du skieur
        skier.getRegistrations().remove(registrationToRemove);

        // Supprimer l'enregistrement de la base de données
        registrationRepository.delete(registrationToRemove);
    }

    //Méthode pour calculer le total des frais d'inscription pour un skieur
    @Override
    public Float calculateTotalRegistrationFees(Long numSkier) {
        Skier skier = skierRepository.findById(numSkier).orElseThrow(() ->
                new EntityNotFoundException("Skier not found with id: " + numSkier)
        );
        return skier.getRegistrations().stream()
                .map(r -> r.getCourse().getPrice())
                .reduce(0f, Float::sum);
    }

    //éthode permet d'analyser l'engagement des skieurs en fonction de leurs inscriptions
    // et de fournir des statistiques comme le nombre moyen de cours par skieur,
    // insi que le skieur le plus actif.
    @Override
    public Map<String, Object> analyzeSkierEngagement() {
        List<Skier> allSkiers = skierRepository.findAll();

        int totalCourses = 0;
        Skier mostActiveSkier = null;
        int maxRegistrations = 0;
        for (Skier skier : allSkiers) {
            int registrationCount = skier.getRegistrations().size();
            totalCourses += registrationCount;
            if (registrationCount > maxRegistrations) {
                maxRegistrations = registrationCount;
                mostActiveSkier = skier;
            }
        }
        double averageCourses = allSkiers.isEmpty() ? 0 : (double) totalCourses / allSkiers.size();
        Map<String, Object> engagementStats = new HashMap<>();
        engagementStats.put("averageCoursesPerSkier", averageCourses);
        engagementStats.put("mostActiveSkier", mostActiveSkier);
        return engagementStats;
    }

    // Compte le nombre de skieurs par couleur de piste
    @Override
    public HashMap<Color, Integer> nombreSkiersParColorPiste() {
        HashMap<Color, Integer> nombreSkiersParColorPiste = new HashMap<>();
        Color[] colors = Color.values();
        for (Color c : colors) {
            nombreSkiersParColorPiste.put(c, skierRepository.skiersByColorPiste(c).size());
        }
        return nombreSkiersParColorPiste;
    }

    // Retourne les skieurs dont l'abonnement expire bientôt (dans un mois)
    @Override
    public List<Skier> getSkiersBySubscriptionExpiry() {
        LocalDate today = LocalDate.now(); //La date actuelle
        LocalDate nextMonth = today.plusMonths(1); //La date un mois plus tard
        return skierRepository.findAll().stream()
                .filter(skier -> {
                    Subscription subscription = skier.getSubscription(); //Pour chaque skieur, on récupère son abonnement
                    return subscription != null && //On vérifie si l'abonnement n'est pas null
                            subscription.getEndDate().isBefore(nextMonth) && //La date de fin de l'abonnement (endDate) est avant nextMonth
                            subscription.getEndDate().isAfter(today); //La date de fin de l'abonnement est après today.
                })
                .collect(Collectors.toList());
    }

    // Calcule le nombre moyen de skieurs par type d'abonnement
    @Override
    public Map<TypeSubscription, Double> calculateAverageSkiersPerSubscription() {
        List<Skier> skiers = skierRepository.findAll();
        Map<TypeSubscription, Integer> countMap = new HashMap<>();
        Map<TypeSubscription, Integer> totalMap = new HashMap<>();

        for (Skier skier : skiers) {
            if (skier.getSubscription() != null) {
                TypeSubscription type = skier.getSubscription().getTypeSub();
                countMap.put(type, countMap.getOrDefault(type, 0) + 1);
                totalMap.put(type, totalMap.getOrDefault(type, 0) + skier.getPistes().size()); // Nombre total de pistes
            }
        }
        Map<TypeSubscription, Double> averageMap = new HashMap<>();
        for (TypeSubscription type : countMap.keySet()) {
            double average = (double) totalMap.get(type) / countMap.get(type);
            averageMap.put(type, average);
        }
        return averageMap;
    }


}
