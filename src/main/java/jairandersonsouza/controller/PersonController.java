package jairandersonsouza.controller;

import jairandersonsouza.dto.PersonRequest;
import jairandersonsouza.mapper.PersonMapper;
import jairandersonsouza.model.Person;
import jairandersonsouza.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/person")
public class PersonController {

    @Autowired
    private PersonService service;
    @Autowired
    private PersonMapper personMapper;

    @PostMapping
    public Person save(@RequestBody PersonRequest personRequest) {
        return this.service.save(this.personMapper.toPerson(personRequest));
    }

    @PutMapping
    public Person update(@RequestBody Person person) {
        return this.service.update(person);
    }

    @GetMapping
    @RequestMapping("/{id}")
    public Person getById(@PathVariable("id") Long id) {
        return this.service.getById(id);
    }

    @GetMapping
    public List<Person> getAll(@RequestParam(name = "page", required = true, defaultValue = "0") int page, @RequestParam(name = "size", required = true, defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return this.service.getAll(pageable);
    }

}