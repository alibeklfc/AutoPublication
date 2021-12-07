package com.example.softwareeng.controller;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import com.example.softwareeng.services.PersonRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@EnableScheduling
public class PersonController {
    private PersonRepository personRepository;

    @Autowired
    public PersonController(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @GetMapping("/people")
    public ResponseEntity<Person> people() {
        return new ResponseEntity(personRepository.getPeople(), HttpStatus.OK);
    }

    @GetMapping("/person")
    public ResponseEntity<Person> getPerson(@RequestParam("personid") Long personid) {
        Person p = personRepository.getPerson(personid);
        if(p == null){
            return new ResponseEntity("", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(personRepository.getPerson(personid), HttpStatus.OK);
    }

    @PutMapping(value = "/person", consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity addPerson(@RequestBody Person person){
        personRepository.addPerson(person);
        return new ResponseEntity(person, HttpStatus.OK);
    }

    @PutMapping("/block")
    public ResponseEntity<Publication> blockPublication(@RequestParam("personid") Long personid, @RequestParam("publid") Long publid, @RequestParam("token") String token ){
        int temp = personRepository.blockPublication(personid, publid, token);
        if (temp == 0){
            return new ResponseEntity("unsuccessful attempt to block",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("{\"response\": \"blocked\"}",HttpStatus.OK);
    }

    @PutMapping("/unblock")
    public ResponseEntity<Publication> unBlockPublication(@RequestParam("personid") Long personid, @RequestParam("publid") Long publid, @RequestParam("token") String token ){
        int temp =  personRepository.unBlockPublication(personid, publid, token);
        if (temp == 0){
            return new ResponseEntity("unsuccessful attempt to unblock",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("{\"response\": \"unblocked\"}",HttpStatus.OK);
    }

    @GetMapping("/sendEmail")
    public ResponseEntity<Person> sendEmail(@RequestParam("personid") Long personid) {
        personRepository.sendEmail(personid);
        System.out.println(personid);
        return new ResponseEntity("\"{\\\"response\\\": \\\"sent\\\"}", HttpStatus.OK);
    }

    @Scheduled(fixedRate = 86400000)
    public void changeToken() {

        List<Person> lp = personRepository.getPeople();
        for(Person p: lp){
            personRepository.changeToken(p);
        }
        System.out.println("HEre");
    }

}
