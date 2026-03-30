package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Telefone;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TelefoneRepository {
    private final JdbcTemplate jdbcTemplate;
    public TelefoneRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Telefone> telefoneRowMapper = new RowMapper<Telefone>() {
        public Telefone mapRow(ResultSet rs, int rowNum) throws SQLException {
            Telefone telefone = new Telefone();
            telefone.setCpf_usuario(rs.getString("cpf"));
            telefone.setNumero(rs.getString("numero"));
            return telefone;
        }
    };
    public List<Telefone> findAll() {
        return jdbcTemplate.query("SELECT * FROM telefone", telefoneRowMapper);
    }
    public List<Telefone> findByCpf(String cpf_usuario) {
        return jdbcTemplate.query("SELECT * FROM telefone WHERE cpf = ?", telefoneRowMapper, cpf_usuario);
    }
    public boolean save(Telefone telefone) {
        int linhasAfetadas = jdbcTemplate.update("INSERT INTO telefone VALUES (?, ?)",telefone.getCpf_usuario(),telefone.getNumero());
        return linhasAfetadas > 0? true:false;
    }
    public boolean update(Telefone telefone, String novoNumero) {
        int linhasAfetadas = jdbcTemplate.update("UPDATE telefone(numero) SET ? WHERE numero = ?",novoNumero,telefone.getNumero());
        return linhasAfetadas > 0? true:false;
    }
    public boolean delete(String cpf_usuario) {
        int linhasAfetadas = jdbcTemplate.update("DELETE FROM telefone WHERE cpf = ?",cpf_usuario);
        return linhasAfetadas > 0? true:false;
    }
}
