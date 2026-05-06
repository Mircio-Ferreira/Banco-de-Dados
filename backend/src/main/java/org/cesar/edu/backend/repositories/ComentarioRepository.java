package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Comentario;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ComentarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public ComentarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comentario> rowMapper = new RowMapper<Comentario>() {
        public Comentario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comentario comentario = new Comentario();

            comentario.setId_comentario(rs.getLong("id_comentario"));
            comentario.setId_aula(rs.getLong("id_aula"));
            comentario.setId_curso(rs.getLong("id_curso"));
            comentario.setCpf_aluno(rs.getString("cpf_aluno"));
            comentario.setCpf_professor(rs.getString("cpf_professor"));
            comentario.setData_criacao(rs.getObject("data_criacao", LocalDate.class));
            comentario.setConteudo(rs.getString("conteudo"));
            comentario.setComentario_pai(rs.getLong("comentario_pai"));

            if (rs.wasNull()) {
                comentario.setComentario_pai(null);
            }

            return comentario;
        }
    };

    public List<Comentario> findAll() {
        String sql = "SELECT * FROM comentario";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Comentario findById(Long id_comentario) {
        try {
            String sql = "SELECT * FROM comentario WHERE id_comentario = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id_comentario);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Comentario> findByAula(Long id_aula) {
        String sql = "SELECT * FROM comentario WHERE id_aula = ?";
        return jdbcTemplate.query(sql, rowMapper, id_aula);
    }

    public List<Comentario> findByCurso(Long id_curso) {
        String sql = "SELECT * FROM comentario WHERE id_curso = ?";
        return jdbcTemplate.query(sql, rowMapper, id_curso);
    }

    public List<Comentario> findRespostas(Long comentario_pai) {
        String sql = "SELECT * FROM comentario WHERE comentario_pai = ?";
        return jdbcTemplate.query(sql, rowMapper, comentario_pai);
    }

    public boolean save(Comentario comentario) {
        String sql = "INSERT INTO comentario(id_aula, id_curso, cpf_aluno, cpf_professor, conteudo, comentario_pai) VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                comentario.getId_aula(),
                comentario.getId_curso(),
                comentario.getCpf_aluno(),
                comentario.getCpf_professor(),
                comentario.getConteudo(),
                comentario.getComentario_pai()
        ) > 0;
    }

    public boolean delete(Long id_comentario) {
        String sql = "DELETE FROM comentario WHERE id_comentario = ?";
        return jdbcTemplate.update(sql, id_comentario) > 0;
    }

    public boolean update(Comentario comentario, Long id_comentario) {
        String sql = "UPDATE comentario SET conteudo = ?, comentario_pai = ? WHERE id_comentario = ?";

        return jdbcTemplate.update(
                sql,
                comentario.getConteudo(),
                comentario.getComentario_pai(),
                id_comentario
        ) > 0;
    }
}