package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.AssistirAula;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class AssistirAulaRepository {
    private final JdbcTemplate jdbcTemplate;
    public AssistirAulaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<AssistirAula> rowMapper = new RowMapper<AssistirAula>() {
        public AssistirAula mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssistirAula assistirAula = new AssistirAula();
            assistirAula.setId_aula(rs.getLong("id_aula"));
            assistirAula.setId_modulo(rs.getLong("id_modulo"));
            assistirAula.setId_curso(rs.getLong("id_curso"));
            assistirAula.setCpf_aluno(rs.getString("cpf_aluno"));
            assistirAula.setData_assistida(rs.getObject("data_assistida", LocalDate.class));
            return assistirAula;
        }
    };
    public List<AssistirAula> findAll() {
        return jdbcTemplate.query("select * from assistir", rowMapper);
    }
    public AssistirAula findById(String cpf, Long id_aula, Long id_modulo, Long id_curso) {
        try{
            String sql = "SELECT * FROM assistir WHERE cpf_aluno = ? AND id_aula = ? AND id_modulo = ? AND id_curso = ?";
            return jdbcTemplate.queryForObject(sql,rowMapper, cpf, id_aula, id_modulo, id_curso);
        }
        catch(Exception e){
            return null;
        }
    }
    public boolean save(AssistirAula assistirAula) {
        try {
            String sql = "INSERT INTO assistir(cpf_aluno, id_aula, id_modulo, id_curso) VALUES (?, ?, ?, ?) ";

            int rowsAffected = jdbcTemplate.update(
                    sql,
                    assistirAula.getCpf_aluno(),
                    assistirAula.getId_aula(),
                    assistirAula.getId_modulo(),
                    assistirAula.getId_curso()
            );

            return rowsAffected > 0;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean delete(String cpf, Long id_aula, Long id_modulo, Long id_curso) {
        try {
            String sql = "DELETE FROM assistir WHERE cpf_aluno = ? AND id_aula = ? AND id_modulo = ? AND id_curso = ?";

            int rowsAffected = jdbcTemplate.update(
                    sql,
                    cpf,
                    id_aula,
                    id_modulo,
                    id_curso
            );

            return rowsAffected > 0;

        } catch (Exception e) {
            return false;
        }
    }
}
