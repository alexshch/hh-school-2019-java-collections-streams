package tasks;

import common.Area;
import common.Person;
import common.Task;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Имеются
- коллекция персон Collection<Person>
- словарь Map<Integer, Set<Integer>>, сопоставляющий каждой персоне множество id регионов
- коллекция всех регионов Collection<Area>
На выходе хочется получить множество строк вида "Имя - регион". Если у персон регионов несколько, таких строк так же будет несколько
 */
public class Task6 implements Task {

  private Set<String> getPersonDescriptions(Collection<Person> persons,
                                            Map<Integer, Set<Integer>> personAreaIds,
                                            Collection<Area> areas) {

    var areaNames = areas.stream().collect(Collectors.toMap(item -> item.getId(), item -> item.getName()));
    return persons.stream()
        .flatMap(person -> (personAreaIds.containsKey(person.getId()) ? personAreaIds.get(person.getId()) : new HashSet<>()).stream()
        .map(areaId -> person.getFirstName() + " - " + areaNames.get(areaId)))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean check() {
    List<Person> persons = List.of(
        new Person(1, "Oleg", Instant.now()),
        new Person(2, "Vasya", Instant.now()),
         new Person(3, "Peotr", Instant.now())
    );
    Map<Integer, Set<Integer>> personAreaIds = Map.of(1, Set.of(1, 2), 2, Set.of(2, 3), 3,  new HashSet<>());
    List<Area> areas = List.of(new Area(1, "Moscow"), new Area(2, "Spb"), new Area(3, "Ivanovo"));
    var set = getPersonDescriptions(persons, personAreaIds, areas);
    return getPersonDescriptions(persons, personAreaIds, areas)
        .equals(Set.of("Oleg - Moscow", "Oleg - Spb", "Vasya - Spb", "Vasya - Ivanovo"));
  }
}
