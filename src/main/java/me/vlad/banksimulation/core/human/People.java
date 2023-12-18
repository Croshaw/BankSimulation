package me.vlad.banksimulation.core.human;

public class People {
    private String surname;
    private String name;
    private String patronymic;

    public People(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(surname, name, patronymic);
    }
    public String toShortString() {
        return "%s %c. %c.".formatted(surname, name.charAt(0), patronymic.charAt(0));
    }
}
