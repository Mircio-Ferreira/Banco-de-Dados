package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Aula;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AulaRepository {
    private final JdbcTemplate jdbcTemplate;

    public AulaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Aula> rowMapper = new RowMapper<Aula>() {
        public Aula mapRow(ResultSet rs, int rowNum) throws SQLException {
            Aula aula = new Aula();
            aula.setId_aula(rs.getLong("id_aula"));
            aula.setId_modulo(rs.getLong("id_modulo"));
            aula.setId_curso(rs.getLong("id_curso"));
            aula.setTitulo(rs.getString("titulo"));
            aula.setDescricao_aula(rs.getString("descricao"));
            aula.setLink(rs.getString("link_do_video"));
            return aula;
        }
    };

    public List<Aula> findAll() {
        return jdbcTemplate.query("select * from Aula", rowMapper);
    }
    public Aula findById(long id_aula, Long id_modulo, Long id_curso) {
        Aula aula = new Aula();
        try{
            String sql = "SELECT * FROM aula WHERE id_aula = ? AND id_modulo = ? AND id_curso = ?";
            aula = jdbcTemplate.queryForObject(sql, rowMapper, id_aula, id_modulo, id_curso);
            return aula;
        }
        catch(Exception e){
            return null;
        }
    }
    public boolean save(Aula aula) {
        String sql = "INSERT INTO aula(id_modulo,id_curso,titulo,descricao,link_do_video) VALUES(?,?,?,?,?)";
        return jdbcTemplate.update(sql,aula.getId_modulo(),aula.getId_curso(),aula.getTitulo(),aula.getDescricao_aula(),aula.getLink()) > 0;
    }
    public boolean delete(Long id_aula, Long id_modulo, Long id_curso) {
        String sql = "DELETE FROM aula WHERE id_aula = ? AND id_modulo = ? AND id_curso = ?";
        return jdbcTemplate.update(sql, id_aula, id_modulo, id_curso) > 0;
    }
    public boolean update(Aula aula, Long id_aula, Long id_modulo, Long id_curso) {
        String sql = "UPDATE aula SET titulo = ?, descricao = ?, link_do_video = ? WHERE id_aula = ? AND id_curso = ? AND id_modulo = ?";
        return jdbcTemplate.update(sql,aula.getTitulo(),aula.getDescricao_aula(),aula.getLink(),id_aula,id_curso,id_modulo) > 0;
    }
}
