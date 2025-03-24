package tassist.address.testutil;

import java.util.HashSet;
import java.util.Set;

import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Email;
import tassist.address.model.person.Github;
import tassist.address.model.person.Name;
import tassist.address.model.person.Person;
import tassist.address.model.person.Phone;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;
import tassist.address.model.tag.Tag;
import tassist.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_CLASS = "T01";
    public static final String DEFAULT_STUDENTID = "A0000000B";
    public static final String DEFAULT_GITHUB = "https://github.com/default";
    public static final String DEFAULT_PROGRESS = "0";

    private Name name;
    private Phone phone;
    private Email email;
    private ClassNumber classNumber;
    private StudentId studentId;
    private Github github;
    private Set<Tag> tags;
    private Progress progress;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        classNumber = new ClassNumber(DEFAULT_CLASS);
        studentId = new StudentId(DEFAULT_STUDENTID);
        github = new Github(DEFAULT_GITHUB);
        tags = new HashSet<>();
        progress = new Progress(DEFAULT_PROGRESS);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        classNumber = personToCopy.getClassNumber();
        studentId = personToCopy.getStudentId();
        github = personToCopy.getGithub();
        tags = new HashSet<>(personToCopy.getTags());
        progress = personToCopy.getProgress();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }


    /**
     * Sets the {@code Github} of the {@code Person} that we are building.
     */
    public PersonBuilder withGithub(String github) {
        this.github = new Github(github);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code ClassNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withClassNumber(String classNumber) {
        this.classNumber = new ClassNumber(classNumber);
        return this;
    }

    /**
     * Sets the {@code StudentId} of the {@code Person} that we are building.
     */
    public PersonBuilder withStudentId(String studentId) {
        this.studentId = new StudentId(studentId);
        return this;
    }

    /**
     * Sets the {@code Progress} of the {@code Person} that we are building.
     */
    public PersonBuilder withProgress(String progress) {
        this.progress = new Progress(progress);
        return this;
    }

    /**
     * Builds and returns a {@code Person} instance with the set attributes.
     */
    public Person build() {
        return new Person(name, phone, email, classNumber, studentId, github, tags, progress);
    }
}
