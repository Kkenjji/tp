package tassist.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.parser.exceptions.ParseException;
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
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String classNumber;
    private final String studentId;
    private final String github;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String progress;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("classNumber") String classNumber,
            @JsonProperty("studentId") String studentId,
            @JsonProperty("github") String github, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("progress") String progress) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.classNumber = classNumber;
        this.studentId = studentId;
        this.github = github;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.progress = progress;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        classNumber = source.getClassNumber().value;
        studentId = source.getStudentId().value;
        github = source.getGithub().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        progress = String.valueOf(source.getProgress().value);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (classNumber == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    ClassNumber.class.getSimpleName()));
        }
        if (!ClassNumber.isValidClassNumber(classNumber)) {
            throw new IllegalValueException(ClassNumber.MESSAGE_CONSTRAINTS);
        }
        final ClassNumber modelClassNumber = new ClassNumber(classNumber);

        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    StudentId.class.getSimpleName()));
        }

        final StudentId modelStudentId = new StudentId(studentId);

        if (github == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Github.class.getSimpleName()));
        }
        final Github modelGithub = new Github(github);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        if (progress == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Progress.class.getSimpleName()));
        }

        String progressValue = progress.trim();
        if (!Progress.isValidProgress(progressValue)) {
            throw new ParseException(Progress.MESSAGE_CONSTRAINTS);
        }
        final Progress modelProgress = new Progress(progressValue);

        return new Person(modelName, modelPhone, modelEmail, modelClassNumber, modelStudentId,
                          modelGithub, modelTags, modelProgress);
    }
}
