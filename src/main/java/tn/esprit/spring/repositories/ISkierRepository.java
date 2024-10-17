package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;
import java.util.List;

public interface ISkierRepository extends JpaRepository<Skier, Long> {
   List<Skier> findBySubscription_TypeSub(TypeSubscription typeSubscription);
   Skier findBySubscription(Subscription subscription);

   /*--------------------------------------------------------------------------------------------*/

   List<Skier> findByRegistrations_Course_NumCourse(Long numCourse);
   List<Skier> findByPistes_NumPiste(Long numPiste);

   @Query("SELECT s FROM Skier s ORDER BY SIZE(s.registrations) DESC")
   Skier findMostActiveSkier();

   List<Skier> findBySubscription_EndDateAfter(LocalDate currentDate);@Query("select s from Skier s JOIN s.pistes p where  p.color=:color ")
   List<Skier> skiersByColorPiste(@Param("color") Color color);








}
