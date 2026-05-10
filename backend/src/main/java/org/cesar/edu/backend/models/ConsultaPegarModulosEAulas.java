package org.cesar.edu.backend.models;

import lombok.*;

import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaPegarModulosEAulas {
    private Modulo modulo;
    private List<Aula> aulas;
}
