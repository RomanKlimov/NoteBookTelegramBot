package ru.itis.bot.models;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @Column(name = "dateOfBirth")
    private String date;

    @Override
    public String toString() {
        return "Name: '" + this.name + "', City: '" + this.city + "', DateOfBirth: '" + this.date + "'";
    }

}
