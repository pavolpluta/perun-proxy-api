package cz.muni.ics.perunproxyapi.persistence.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents candidate from Perun.
 *
 * @author Pavol Pluta <pavol.pluta1@gmail.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Candidate {

    @NonNull private String firstName;
    @NonNull private String lastName;
    private UserExtSource userExtSource;
    private String middleName;
    private String titleAfter;
    private String titleBefore;
    private Map<String, String> attributes = new HashMap<>();

    public void setAttributes(@NonNull Map<String, String> attributes) {
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }

    public void addAttribute(@NonNull String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
    }
}
