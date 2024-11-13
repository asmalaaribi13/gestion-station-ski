package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    // Common constants
    private static final String NOT_FOUND_SUFFIX = " not found";
    private static final String SKIER_NOT_FOUND = "Skier" + NOT_FOUND_SUFFIX;
    private static final String SUBSCRIPTION_NOT_FOUND = "Subscription" + NOT_FOUND_SUFFIX;
    private static final String PISTE_NOT_FOUND = "Piste" + NOT_FOUND_SUFFIX;

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
        Skier skier = skierRepository.findById(numSkier)
                .orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));

        Subscription subscription = subscriptionRepository.findById(numSubscription)
                .orElseThrow(() -> new IllegalArgumentException(SUBSCRIPTION_NOT_FOUND));

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
        Skier skier = skierRepository.findById(numSkieur)
                .orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));

        Piste piste = pisteRepository.findById(numPiste)
                .orElseThrow(() -> new IllegalArgumentException(PISTE_NOT_FOUND));

        if (skier.getPistes() == null) {
            skier.setPistes(new HashSet<>());
        }
        skier.getPistes().add(piste);
        return skierRepository.save(skier);
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Map<String, Double> analyzePisteUsageByAgeGroup() {
        LocalDate currentDate = LocalDate.now();
        Map<String, List<Integer>> ageGroups = new HashMap<>();
        ageGroups.put("Children (0-12)", new ArrayList<>());
        ageGroups.put("Teens (13-19)", new ArrayList<>());
        ageGroups.put("Adults (20-59)", new ArrayList<>());
        ageGroups.put("Seniors (60+)", new ArrayList<>());

        // Group skiers by age
        skierRepository.findAll().forEach(skier -> {
            int age = currentDate.getYear() - skier.getDateOfBirth().getYear();
            if (age <= 12) {
                ageGroups.get("Children (0-12)").add(skier.getPistes().size());
            } else if (age <= 19) {
                ageGroups.get("Teens (13-19)").add(skier.getPistes().size());
            } else if (age <= 59) {
                ageGroups.get("Adults (20-59)").add(skier.getPistes().size());
            } else {
                ageGroups.get("Seniors (60+)").add(skier.getPistes().size());
            }
        });

        // Calculate average piste usage per age group
        Map<String, Double> averagePisteUsage = new HashMap<>();
        ageGroups.forEach((group, usage) -> {
            double average = usage.stream().mapToInt(i -> i).average().orElse(0.0);
            averagePisteUsage.put(group, average);
        });

        return averagePisteUsage;
    }

    @Override
    public Map<String, Object> analyzeSkierEngagement() {
        List<Skier> skiers = skierRepository.findAll();
        Map<String, Object> statistics = new HashMap<>();

        double averageCoursesPerSkier = skiers.stream()
                .mapToInt(skier -> skier.getRegistrations().size())
                .average()
                .orElse(0.0);

        Skier mostActiveSkier = skiers.stream()
                .max(Comparator.comparingInt(skier -> skier.getRegistrations().size()))
                .orElse(null);

        statistics.put("averageCoursesPerSkier", averageCoursesPerSkier);
        statistics.put("mostActiveSkier", mostActiveSkier);

        return statistics;
    }

    @Override
    public List<Skier> findTopSpendingSkiers(int topN) {
        return skierRepository.findAll().stream()
                .sorted((skier1, skier2) -> {
                    Float totalSpend1 = calculateTotalSpendingBySkier(skier1.getNumSkier());
                    Float totalSpend2 = calculateTotalSpendingBySkier(skier2.getNumSkier());
                    return totalSpend2.compareTo(totalSpend1); // Tri en ordre décroissant
                })
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public Map<TypeSubscription, Double> getAverageAgeBySubscriptionType() {
        LocalDate today = LocalDate.now();
        return Arrays.stream(TypeSubscription.values())
                .collect(Collectors.toMap(
                        type -> type,
                        type -> skierRepository.findBySubscription_TypeSub(type).stream()
                                .mapToInt(skier -> today.getYear() - skier.getDateOfBirth().getYear())
                                .average().orElse(0.0)
                ));
    }

    @Override
    public Float calculateTotalSpendingBySkier(Long numSkier) {
        Skier skier = skierRepository.findById(numSkier)
                .orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));

        Float courseCost = skier.getRegistrations().stream()
                .map(registration -> registration.getCourse().getPrice())
                .reduce(0f, Float::sum);

        Float subscriptionCost = skier.getSubscription() != null ? skier.getSubscription().getPrice() : 0f;

        return courseCost + subscriptionCost;
    }
}








































