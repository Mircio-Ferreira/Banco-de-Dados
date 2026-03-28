package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.CertificadoProfessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CertificacoesRepository {
    private final JdbcTemplate jdbcTemplate;

    public CertificacoesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<CertificadoProfessor> certificadoRowMapper = new RowMapper<CertificadoProfessor>() {
        @Override
        public CertificadoProfessor mapRow(ResultSet rs, int rowNum) throws SQLException {
            CertificadoProfessor c = new CertificadoProfessor();
            c.setCpf_professor(rs.getString("cpf_professor"));
            c.setTitulo_certificado(rs.getString("titulo_certificado"));
            return c;
        }
    };

    public List<CertificadoProfessor> findAll() {
        return jdbcTemplate.query("SELECT * FROM certificacoes",certificadoRowMapper);
    }

    public List<CertificadoProfessor> findByCpf(String cpf) {
        return jdbcTemplate.query("SELECT * FROM certificacoes WHERE cpf_professor = ?",certificadoRowMapper,cpf);
    }

    public boolean save(CertificadoProfessor certificadoProfessor) {
        int linhasAlteradas = jdbcTemplate.update("INSERT INTO certificacoes VALUES (?,?)",certificadoProfessor.getCpf_professor(),certificadoProfessor.getTitulo_certificado());
        return linhasAlteradas > 0? true:false;
    }
    public boolean update(CertificadoProfessor certificadoNovo, CertificadoProfessor certificadoAntigo) {
        int linhasAlteradas = jdbcTemplate.update("UPDATE certificacoes SET cpf_professor = ?, titulo_certificado = ? WHERE cpf_professor = ? AND titulo_certificado = ?",
                certificadoNovo.getCpf_professor(), certificadoNovo.getTitulo_certificado(), certificadoAntigo.getCpf_professor(),certificadoAntigo.getTitulo_certificado());
        return linhasAlteradas > 0? true:false;
    }
    public boolean delete(CertificadoProfessor certificado){
        return jdbcTemplate.update("DELETE FROM certificacoes WHERE cpf_professor = ? AND titulo_certificado = ?",certificado.getCpf_professor(), certificado.getTitulo_certificado()) > 0;
    }
    public boolean deleteByCpf(String cpf) {
        int linhasAlteradas = jdbcTemplate.update("DELETE FROM certificacoes WHERE cpf_professor = ?", cpf);

        return linhasAlteradas >= 0;
    }
}
