package tassist.address.model.util;

import static tassist.address.model.person.ClassNumber.DEFAULT_CLASS;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import tassist.address.model.AddressBook;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Email;
import tassist.address.model.person.Github;
import tassist.address.model.person.Name;
import tassist.address.model.person.Person;
import tassist.address.model.person.Phone;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;
import tassist.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    public static final Github DEFAULT_GITHUB = new Github("https://github.com/default");

    public static Person[] getSamplePersons() {
        return new Person[]{
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                        new ClassNumber("T01"), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("friends"), new Progress("20")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                        new ClassNumber("T04"), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("colleagues", "friends"), new Progress("35")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                        new ClassNumber("R01"), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("neighbours"), new Progress("80")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                        new ClassNumber(DEFAULT_CLASS), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("family"), new Progress("15")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                        new ClassNumber("R10"), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("classmates"), new Progress("60")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                        new ClassNumber("T04"), new StudentId("A0000000B"),
                    DEFAULT_GITHUB, getTagSet("colleagues"), new Progress("77"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
