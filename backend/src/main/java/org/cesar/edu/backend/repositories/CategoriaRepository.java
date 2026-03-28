package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Categoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoriaRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Categoria> categoriaRowMapper = (rs, rowNum) -> {
        Categoria categoria = new Categoria();
        categoria.setId_categoria(rs.getLong("id_categoria"));
        categoria.setNome(rs.getString("nome_da_categoria"));
        return categoria;
    };

    public List<Categoria> findAll() {
        String sql = "SELECT * FROM categoria";
        return jdbcTemplate.query(sql, categoriaRowMapper);
    }

    public Categoria findById(Long id) {
        String sql = "SELECT * FROM categoria WHERE id_categoria = ?";;
        return jdbcTemplate.queryForObject(sql, categoriaRowMapper, id);
    }

    public boolean save(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome_da_categoria) VALUES (?)";
        return jdbcTemplate.update(sql, categoria.getNome()) > 0;
    }

    public boolean update(Categoria categoria, Long idAntigo) {
        String sql = "UPDATE categoria SET nome_da_categoria = ? WHERE id_categoria = ?";
        return jdbcTemplate.update(sql, categoria.getNome(), idAntigo) > 0;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}