package tassist.address.model.person;

import static tassist.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import tassist.address.commons.util.ToStringBuilder;
import tassist.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {
    public static final String PLACEHOLDER_COURSE = "placeholder";
    public static final String PLACEHOLDER_TEAM = "placeholder";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final StudentId studentId;
    // Data fields
    private final ClassNumber classNumber;
    private final Progress progress;
    private final Set<Tag> tags = new HashSet<>();
    private final Github github;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, ClassNumber classNumber,
                StudentId studentId, Github github, Set<Tag> tags, Progress progress) {
        requireAllNonNull(name, phone, email, classNumber, studentId, github, tags, progress);

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.classNumber = classNumber;
        this.studentId = studentId;
        this.github = github;
        this.tags.addAll(tags);
        this.progress = progress;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public ClassNumber getClassNumber() {
        return classNumber;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public Progress getProgress() {
        return progress;
    }

    public Github getGithub() {
        return github;
    }

    public String getCourse() {
        return PLACEHOLDER_COURSE;
    }

    public String getTeam() {
        return PLACEHOLDER_TEAM;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && studentId.equals(otherPerson.studentId)
                && github.equals(otherPerson.github)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, classNumber, studentId, github, tags, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("classNumber", classNumber)
                .add("studentId", studentId)
                .add("github", github)
                .add("tags", tags)
                .add("progress", progress)
                .toString();
    }

}
