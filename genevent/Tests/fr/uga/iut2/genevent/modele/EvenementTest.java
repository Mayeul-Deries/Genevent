package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EvenementTest {

    @Test
    void addParticipant() {
        GenEvent genEvent = new GenEvent();
        Evenement evenement = new Evenement(genEvent,"GasEvent", LocalDate.of(22, 12, 12),LocalDate.of(22,12,13),"Présentation du nouveau produit de Gatien","4 chemin des roses","gatien@gmail.com",50);
        Utilisateur user1 = new Utilisateur("gatien@gmail.com","Gatien","GatienTheBest");
        evenement.addParticipant(user1);
        evenement.addParticipant(user1);
        ArrayList<Utilisateur> participants=new ArrayList<>();
        participants.add(user1);
        assertEquals(evenement.getParticipants(),participants,"On ne peut pas ajouter deux fois le même utlisateur à un événement.");
    }



}