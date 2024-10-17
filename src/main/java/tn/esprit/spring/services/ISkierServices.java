package tn.esprit.spring.services;

import tn.esprit.spring.entities.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISkierServices {

	List<Skier> retrieveAllSkiers();
	Skier  addSkier(Skier  skier);
	Skier assignSkierToSubscription(Long numSkier, Long numSubscription);
	Skier addSkierAndAssignToCourse(Skier skier, Long numCourse);
	void removeSkier (Long numSkier);
	Skier retrieveSkier (Long numSkier);
	Skier assignSkierToPiste(Long numSkieur, Long numPiste);
	List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);


	//Méthode pour désinscrire un skieur d'un cours spécifique
	void unregisterSkierFromCourse(Long numSkier, Long numCourse);

	//Méthode pour calculer le total des frais d'inscription pour un skieur
	Float calculateTotalRegistrationFees(Long numSkier);

	//éthode permet d'analyser l'engagement des skieurs en fonction de leurs inscriptions
	// et de fournir des statistiques comme le nombre moyen de cours par skieur,
	// insi que le skieur le plus actif.
	Map<String, Object> analyzeSkierEngagement();

	// Compte le nombre de skieurs par couleur de piste
	HashMap<Color, Integer> nombreSkiersParColorPiste();

	// Retourne les skieurs dont l'abonnement expire bientôt (dans un mois)
	List<Skier> getSkiersBySubscriptionExpiry();

	// Calcule le nombre moyen de skieurs par type d'abonnement
	Map<TypeSubscription, Double> calculateAverageSkiersPerSubscription();










}
