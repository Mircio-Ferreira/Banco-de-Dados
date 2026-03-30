package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Curso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CursoRepository {
    private final JdbcTemplate jdbcTemplate;
    public CursoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Curso> cursoRowMapper = new RowMapper<Curso>() {
        public Curso mapRow(ResultSet rs, int rowNum) throws SQLException {
            Curso curso = new Curso();
            curso.setId_curso(rs.getLong("id_curso"));
            curso.setNome_curso(rs.getString("nome"));
            curso.setPreco(rs.getDouble("preco"));
            curso.setDescricao_curso(rs.getString("descricao"));
            return curso;
        }
    };
    public List<Curso> findAll() {
        return jdbcTemplate.query("select * from curso", cursoRowMapper);
    }
    public Curso findById(Long id_curso) {
        return jdbcTemplate.queryForObject("SELECT * FROM curso WHERE id_curso = ?", cursoRowMapper, id_curso);
    }
    public Curso findByNome(String nome_curso) {
        return jdbcTemplate.queryForObject("SELECT * FROM curso WHERE nome = ?", cursoRowMapper, nome_curso);
    }
    public boolean save(Curso curso) {
        int linhasAlteradas = jdbcTemplate.update("INSERT INTO curso(nome, preco, descricao) VALUES (?, ?, ?)", curso.getNome_curso(), curso.getPreco(), curso.getDescricao_curso());
        return linhasAlteradas > 0;
    }
    public boolean delete(Long id_curso) {
        return jdbcTemplate.update("DELETE FROM curso WHERE id_curso = ?", id_curso) > 0;
    }
    public boolean update(Curso curso, Long id_cursoAntigo) {
        int linhasAlteradas = jdbcTemplate.update("UPDATE curso SET nome = ?, preco = ?, descricao = ? WHERE id_curso = ?", curso.getNome_curso(),curso.getPreco(), curso.getDescricao_curso(), id_cursoAntigo);
        return linhasAlteradas > 0;
    }
}
