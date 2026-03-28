package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Compra;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class CompraRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompraRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Compra> compraRowMapper = (rs, rowNum) -> {
        Compra compra = new Compra();
        compra.setId_curso(rs.getLong("id_curso"));
        compra.setCpf_aluno(rs.getString("cpf_aluno"));
        compra.setData_compra(rs.getDate("data_compra").toLocalDate());
        return compra;
    };

    public List<Compra> findAll() {
        return jdbcTemplate.query("SELECT * FROM compra", compraRowMapper);
    }

    public List<Compra> findByAluno(String cpf_aluno) {
        String sql = "SELECT * FROM compra WHERE cpf_aluno = ?";
        return jdbcTemplate.query(sql, compraRowMapper, cpf_aluno);
    }

    public Compra findByIdCpf(Long id_curso, String cpf_aluno) {
        String sql = "SELECT * FROM compra WHERE id_curso = ? AND cpf_aluno = ?";
        return jdbcTemplate.queryForObject(sql, compraRowMapper, id_curso, cpf_aluno);
    }

    public boolean save(Compra compra) {
        if (compra.getData_compra() == null) {
            String sql = "INSERT INTO compra (id_curso, cpf_aluno) VALUES (?, ?)";
            return jdbcTemplate.update(sql, compra.getId_curso(), compra.getCpf_aluno()) > 0;
        }

        String sql = "INSERT INTO compra (id_curso, cpf_aluno, data_compra) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                compra.getId_curso(),
                compra.getCpf_aluno(),
                Date.valueOf(compra.getData_compra())) > 0;
    }

    public boolean delete(Long id_curso, String cpf_aluno) {
        String sql = "DELETE FROM compra WHERE id_curso = ? AND cpf_aluno = ?";
        return jdbcTemplate.update(sql, id_curso, cpf_aluno) > 0;
    }

    public boolean updateData(Compra compra) {
        String sql = "UPDATE compra SET data_compra = ? WHERE id_curso = ? AND cpf_aluno = ?";
        return jdbcTemplate.update(sql,
                Date.valueOf(compra.getData_compra()),
                compra.getId_curso(),
                compra.getCpf_aluno()) > 0;
    }
}