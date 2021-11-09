package com.example.softwareeng.controller;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import com.example.softwareeng.services.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
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
        return new ResponseEntity(personRepository.getPerson(personid), HttpStatus.OK);
    }

    @PutMapping(value = "/person", consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity addPerson(@RequestBody Person person){
        personRepository.addPerson(person);
        return new ResponseEntity(person, HttpStatus.OK);
    }

    @PutMapping("/block")
    public ResponseEntity<Publication> blockPublication(@RequestParam("personid") Long personid, @RequestParam("publid") Long publid ){
        personRepository.blockPublication(personid, publid);
        return new ResponseEntity("blocked",HttpStatus.OK);
    }

    @PutMapping("/unblock")
    public ResponseEntity<Publication> unBlockPublication(@RequestParam("personid") Long personid, @RequestParam("publid") Long publid ){
        personRepository.unBlockPublication(personid, publid);
        return new ResponseEntity("unblocked",HttpStatus.OK);
    }

}
