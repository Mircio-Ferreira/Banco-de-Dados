package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Leciona;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LecionaRepository {
    private final JdbcTemplate jdbcTemplate;
    public LecionaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Leciona> lecionaRowMapper = new RowMapper<Leciona>() {
        public Leciona mapRow(ResultSet rs, int rowNum) throws SQLException {
            Leciona leciona = new Leciona();
            leciona.setCpf_professor(rs.getString("cpf_professor"));
            leciona.setId_curso(rs.getLong("id_curso"));
            return leciona;
        }
    };
    public List<Leciona> findAll() {
        return jdbcTemplate.query("SELECT * FROM Leciona", lecionaRowMapper);
    }
    public List<Leciona> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM leciona WHERE id_curso = ?", lecionaRowMapper, id);
    }
    public List<Leciona> findByCpf(String cpf) {
        return jdbcTemplate.query("SELECT * FROM leciona WHERE cpf_professor = ?", lecionaRowMapper, cpf);
    }
    public List<Leciona> findAllByIdCurso(Long idCurso) {
        String sql = "SELECT * FROM Leciona WHERE id_curso = ?";
        return jdbcTemplate.query(sql, lecionaRowMapper, idCurso);
    }
    public boolean save(Leciona leciona) {
        int linhasAlteradas = jdbcTemplate.update("INSERT INTO leciona (cpf_professor, id_curso) VALUES (?, ?)", leciona.getCpf_professor(), leciona.getId_curso());
        return linhasAlteradas > 0;
    }
    public boolean delete(Leciona leciona) {
        return jdbcTemplate.update("DELETE FROM leciona WHERE cpf_professor = ? AND id_curso = ?", leciona.getCpf_professor(), leciona.getId_curso()) > 0;
    }
    public boolean deleteByIdCurso(Long id_curso) {
        String sql = "DELETE FROM leciona WHERE id_curso = ?";

        try {
            jdbcTemplate.update(sql, id_curso);

            return true;

        } catch (Exception e) {
            System.err.println("Erro ao deletar vínculo Leciona para o curso " + id_curso + ": " + e.getMessage());
            return false;
        }
    }
}
