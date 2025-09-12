package com.codegym.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Toyota, Ford, BMW...

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Car> cars;
}
