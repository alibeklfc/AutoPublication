package com.example.softwareeng.services;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class PersonRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Person> getPeople() {
        return em.createQuery("SELECT c FROM Person c").getResultList();
    }

    public void addPerson(Person person) {
        em.persist(person);
    }


    public void blockPublication(Long personid, Long publid) {
        Person p = getPerson(personid);
        List<Publication> publ = em.createQuery(
                "select p from Publication p where p.id=:publid").setParameter("publid", publid).getResultList();
        if(publ.size()==0){
            return;
        }
        Publication publication = publ.get(0);
        if(p.getPublications().contains(publication)){
            publication.getAuthorsblocked().add(p);
        }
        em.persist(publication);
    }

    public Person getPerson(Long personid) {
        List<Person> lp = em.createQuery(
                "select p from Person p where p.id =:id"
        ).setParameter("id", personid).getResultList();
        if(lp.size() == 1){
            return lp.get(0);
        }
        return null;
    }

    public void unBlockPublication(Long personid, Long publid) {
        Person p = getPerson(personid);
        List<Publication> publ = em.createQuery(
                "select p from Publication p where p.id=:publid").setParameter("publid", publid).getResultList();
        if(publ.size()==0){
            return;
        }
        Publication publication = publ.get(0);
        if(p.getPublications().contains(publication)){
            publication.getAuthorsblocked().remove(p);
        }
        em.persist(publication);

    }
}
