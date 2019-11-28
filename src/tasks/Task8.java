package tasks;

import common.Person;
import common.Task;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
convertPersonToString -> перенести в класс Person в качесте get проперти -> переименовать в getFullName
getPersonNames -> переименовали в getPersonNamesMap
countEven -> метод нарущает единственность ответсвенности данного класса, его надо перенести в другой класс
если очень лень после рефакторинга класс надо бы переименовать в PersonUtils и сделать все методы статическими
если не лень его надо продолжть рефакторинг до момента когда класс исчезнет,
т.к. теперь он содержит набор не связанных между собой процедур без данных
 */
public class Task8 implements Task {

  //Не хотим выдывать апи нашу фальшивую персону, поэтому конвертим начиная со второй
  public static List<String> getNames(List<Person> persons) {
    return persons.stream().skip(1).map(Person::getFirstName).collect(Collectors.toList());
  }

  //ну и различные имена тоже хочется
  public static Set<String> getDifferentNames(List<Person> persons) {
    return new HashSet<>(getNames(persons));
  }

  // этот метод должен быть проперти класса Person
  public static String getFullName(Person person) {
    return Stream.of(person.getSecondName(), person.getFirstName(), person.getMiddleName())
        .filter(x -> x != null)
        .collect(Collectors.joining(" "));
  }

  // словарь id персоны -> ее имя
  // в целом ок, но создание new HashMap<>(1); с первоначальным размером словаря 1 не очень
  public static Map<Integer, String> getPersonNamesMap(Collection<Person> persons) {
    return persons.stream().collect(Collectors.toMap(p -> p.getId(), p -> getFullName(p), (p1, p2) -> p1));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public static boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    var personSet = new HashSet<>(persons1);
    return persons2.stream().anyMatch(person -> personSet.contains(person));
  }

  // размытая ответсвенность класса, надо перенесте в другой
  // использовалась переменная класса count, вдруг кто то решился бы запустить метод в несколько потоков??
  public static long countEven(Stream<Integer> numbers) {
    return numbers.filter(num -> num % 2 == 0).count();
  }

  @Override
  public boolean check() {
    if (!getNamesTest()) return false;

    if (!getDifferentNamesTest()) return false;

    if(!convertPersonToStringTest()) return false;

    if (!getPersonNamesTest()) return false;

    if (!hasSamePersonsTest()) return false;

    if (!countEvenTest()) return false;

    boolean codeSmellsGood = false;
    boolean reviewerDrunk = true;
    return codeSmellsGood || reviewerDrunk;
  }

  private boolean getNamesTest(){
    Instant time = Instant.now();
    var persons1 = List.of(
            new Person(1, "Fake Name", time.minusSeconds(1)),
            new Person(2, "Person 2", time),
            new Person(3, "Person 3", time.plusSeconds(1))
    );
    var names = getNames(persons1);
    if (names.size() != 2) {
      return false;
    }
    if (names.stream().anyMatch(n -> "Fake Name".equals(n))){
      return false;
    }
    return true;
  }

  private boolean getDifferentNamesTest(){
    Instant time = Instant.now();
    var persons1 = List.of(
            new Person(1, "Fake Name", time.minusSeconds(1)),
            new Person(2, "Person 2", time),
            new Person(2, "Person 2", time.plusSeconds(1))
    );
    var differentNames = getDifferentNames(List.copyOf(persons1));
    if (differentNames.size() != 1 || !differentNames.stream().allMatch(x ->"Person 2".equals(x))){
      return false;
    }
    return true;
  }

  private boolean convertPersonToStringTest(){
    var person = new Person(1, "Test", "Testov", Instant.now());
    var personName = getFullName(person);
    if (!"Testov Test".equals(personName)) return false;
    person = new Person(1, "Test", Instant.now());
    if (!"Test".equals(getFullName(person))) return false;

    return true;
  }

  private boolean getPersonNamesTest(){
    var time = Instant.now();
    var persons1 = List.of(
            new Person(1, "Fake Name", time.minusSeconds(1)),
            new Person(1, "Person 2", time),
            new Person(3, "Person 3", time.plusSeconds(1))
    );
    var map = getPersonNamesMap(persons1);
    if (map.size() == 2){
      return true;
    }
    return false;
  }

  private boolean hasSamePersonsTest(){
    var time = Instant.now();
    var persons1 = List.of(
            new Person(1, "Person 1", time.minusSeconds(1)),
            new Person(2, "Person 2", time)
    );
    var persons2 = List.of(
            new Person(1, "Person 1", time.minusSeconds(1)),
            new Person(3, "Person 3", time)
    );
    var persons3 = List.of(
            new Person(4, "Person 4", time.minusSeconds(1)),
            new Person(5, "Person 5", time)
    );

    if (!hasSamePersons(persons1, persons2)) return false;
    if (hasSamePersons(persons1, persons3)) return false;
    return true;
  }

  private boolean countEvenTest(){
    return countEven(Stream.of(1, 2, 3, 4, 5)) == 2;
  }
}
