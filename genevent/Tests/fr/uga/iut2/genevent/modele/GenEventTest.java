package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//class GenEventTest {
//
//    @Test
//    void nouvelEvenement() {
//        GenEvent genEvent = new GenEvent();
//        Map<String,Evenement> evenementMap=new HashMap<>();
//        Utilisateur user=new Utilisateur("gatien@gmail.com","Gatien","GatienTheBest");
//        genEvent.ajouteUtilisateur("gatien@gmail.com","Gatien","GatienTheBest");
//        Evenement evenement= new Evenement(genEvent, "GasEvent", LocalDate.of(22, 12, 12),LocalDate.of(22,12,13),"Présentation du nouveau produit de Gatien","4 chemin des roses","gatien@gmail.com",50);
//        evenementMap.put("GasEvent",evenement);
//        genEvent.nouvelEvenement("GasEvent", LocalDate.of(22, 12, 12),LocalDate.of(22,12,13),
//                "gatien@gmail.com","Présentation du nouveau produit de Gatien","4 chemin des roses",50);
//        genEvent.nouvelEvenement("GasEvent", LocalDate.of(22, 12, 12),LocalDate.of(22,12,13),
//                "gatien@gmail.com","Présentation du nouveau produit de Gatien","4 chemin des roses",50);
//        assertEquals(genEvent.getEvenements(),evenementMap,"On ne peut pas ajouter deux fois le même événement");
//
//    }
//}