package jairandersonsouza.mapper;

import jairandersonsouza.dto.PersonRequest;
import jairandersonsouza.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PersonMapper {

    public abstract Person toPerson(PersonRequest personRequest);

}
