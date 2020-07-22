package cz.muni.ics.perunproxyapi.persistence.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Virtual Organization (Vo) object model.
 *
 * @author Dominik Frantisek Bucik <bucik@.ics.muni.cz>
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class Vo extends Model {

    @Getter
    private String name;
    @Getter
    private String shortName;

    public Vo() {
    }

    public Vo(Long id, String name, String shortName) {
        super(id);
        this.setName(name);
        this.setShortName(shortName);
    }

    public void setName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("name can't be null or empty");
        }

        this.name = name;
    }

    public void setShortName(String shortName) {
        if (shortName == null || shortName.length() == 0) {
            throw new IllegalArgumentException("shortName can't be null or empty");
        }

        this.shortName = shortName;
    }
}
