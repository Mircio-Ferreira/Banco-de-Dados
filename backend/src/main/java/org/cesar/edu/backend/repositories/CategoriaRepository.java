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


    public List<Categoria> findCategoriasByCursoId(Long cursoId) {
        String sql = "SELECT * FROM categoria WHERE curso_id = ?";
        return jdbcTemplate.query(sql, categoriaRowMapper, cursoId);
    }
    public Categoria findById(Long id) {
        String sql = "SELECT * FROM categoria WHERE id_categoria = ?";
        List<Categoria> resultados = jdbcTemplate.query(sql, categoriaRowMapper, id);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public Categoria findByNome(String nome) {
        String sql = "SELECT * FROM categoria WHERE nome_da_categoria = ?";
        List<Categoria> resultados = jdbcTemplate.query(sql, categoriaRowMapper, nome);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean save(String categoria) {
        String sql = "INSERT INTO categoria (nome_da_categoria) VALUES (?)";
        return jdbcTemplate.update(sql, categoria) > 0;
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