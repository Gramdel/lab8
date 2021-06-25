package collection;

import java.io.Serializable;
import java.util.ArrayList;

public class Organization implements Serializable {
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Long annualTurnover; //Поле может быть null, Значение поля должно быть больше 0
    private final Long employeesCount; //Поле может быть null, Значение поля должно быть больше 0
    private final OrganizationType type; //Поле может быть null
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    public Organization(String name, Long annualTurnover, Long employeesCount, OrganizationType type) {
        this.name = name;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Organization o = (Organization) obj;

        if (annualTurnover == null) {
            if (o.annualTurnover != null) {
                return false;
            }
        } else {
            if (o.annualTurnover == null || !annualTurnover.equals(o.annualTurnover)) {
                return false;
            }
        }

        if (employeesCount == null) {
            if (o.employeesCount != null) {
                return false;
            }
        } else {
            if (o.employeesCount == null || !employeesCount.equals(o.employeesCount)) {
                return false;
            }
        }

        if (type == null) {
            if (o.type != null) {
                return false;
            }
        } else {
            if (o.type == null || !type.equals(o.type)) {
                return false;
            }
        }

        return name.equals(o.name);
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + ", " + annualTurnover + ", " + employeesCount + ", " + type + "}";
    }

    public String getName() {
        return name;
    }

    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    public Long getEmployeesCount() {
        return employeesCount;
    }

    public OrganizationType getType() {
        return type;
    }

    public void createId(ArrayList<Organization> organizations) {
        Integer id = 1;
        boolean isUnique;
        do {
            isUnique = true;
            for (Organization manufacturer : organizations) {
                if (manufacturer.getId() != null && manufacturer.getId().equals(id)) {
                    isUnique = false;
                    id++;
                    break;
                }
            }
        } while (!isUnique);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}