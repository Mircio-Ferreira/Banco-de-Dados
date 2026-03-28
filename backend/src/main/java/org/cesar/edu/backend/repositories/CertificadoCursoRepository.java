package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.CertificadoCurso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class CertificadoCursoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CertificadoCursoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CertificadoCurso> certificadoRowMapper = (rs, rowNum) -> {
        CertificadoCurso c = new CertificadoCurso();
        c.setId_certificado(rs.getLong("id_certificado"));
        c.setId_curso(rs.getLong("id_curso_concluido"));
        c.setCpf_aluno(rs.getString("cpf_aluno_graduado"));
        c.setData_certificado(rs.getDate("data_certificado").toLocalDate());
        return c;
    };

    public List<CertificadoCurso> findAll() {
        return jdbcTemplate.query("SELECT * FROM certificado_curso", certificadoRowMapper);
    }

    public List<CertificadoCurso> findByAluno(String cpf) {
        String sql = "SELECT * FROM certificado_curso WHERE cpf_aluno_graduado = ?";
        return jdbcTemplate.query(sql, certificadoRowMapper, cpf);
    }

    public boolean save(CertificadoCurso certificado) {
        String sql = "INSERT INTO certificado_curso (id_curso_concluido, cpf_aluno_graduado) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                certificado.getId_curso(),
                certificado.getCpf_aluno()) > 0;
    }

    public boolean delete(Long idCertificado, Long idCurso, String cpf) {
        String sql = "DELETE FROM certificado_curso WHERE id_certificado = ? " +
                "AND id_curso_concluido = ? AND cpf_aluno_graduado = ?";
        return jdbcTemplate.update(sql, idCertificado, idCurso, cpf) > 0;
    }
}