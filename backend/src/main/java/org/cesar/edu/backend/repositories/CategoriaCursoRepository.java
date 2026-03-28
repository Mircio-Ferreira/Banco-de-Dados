package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.CursoCategoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoriaCursoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaCursoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CursoCategoria> cursoCategoriaRowMapper = (rs, rowNum) -> {
        CursoCategoria cc = new CursoCategoria();
        cc.setId_curso(rs.getLong("id_curso"));
        cc.setId_categoria(rs.getLong("id_categoria"));
        return cc;
    };

    public List<CursoCategoria> findAll() {
        return jdbcTemplate.query("SELECT * FROM possui", cursoCategoriaRowMapper);
    }

    public List<CursoCategoria> findByCurso(Long id_curso) {
        String sql = "SELECT * FROM possui WHERE id_curso = ?";
        return jdbcTemplate.query(sql, cursoCategoriaRowMapper, id_curso);
    }

    public List<CursoCategoria> findByCategoria(Long id_categoria) {
        String sql = "SELECT * FROM possui WHERE id_categoria = ?";
        return jdbcTemplate.query(sql, cursoCategoriaRowMapper, id_categoria);
    }

    public boolean save(CursoCategoria cursoCategoria) {
        String sql = "INSERT INTO possui (id_curso, id_categoria) VALUES (?, ?)";
        return jdbcTemplate.update(sql, cursoCategoria.getId_curso(), cursoCategoria.getId_categoria()) > 0;
    }

    public boolean delete(CursoCategoria cursoCategoria) {
        String sql = "DELETE FROM possui WHERE id_curso = ? AND id_categoria = ?";
        return jdbcTemplate.update(sql, cursoCategoria.getId_curso(), cursoCategoria.getId_categoria()) > 0;
    }
}