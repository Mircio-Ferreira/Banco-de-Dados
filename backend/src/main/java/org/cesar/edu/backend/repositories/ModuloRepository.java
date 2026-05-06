package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Modulo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ModuloRepository {
    private final JdbcTemplate jdbcTemplate;

    public  ModuloRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Modulo> moduloRowMapper = new RowMapper<Modulo>() {
        public Modulo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Modulo modulo = new Modulo();
            modulo.setId_modulo(rs.getLong("id_modulo"));
            modulo.setId_curso(rs.getLong("id_curso"));
            modulo.setTitulo(rs.getString("titulo"));
            modulo.setCargaHoraria(rs.getInt("carga_horaria"));
            modulo.setDescricao_curso(rs.getString("descricao"));
            return modulo;
        }
    };

    public List<Modulo> findAll() {
        return jdbcTemplate.query("select * from modulo", moduloRowMapper);
    }
    public Modulo findById(Long id) {
        Modulo modulo;
        try {
            modulo = jdbcTemplate.queryForObject("select * from modulo where id_modulo = ?", moduloRowMapper, id);
        } catch (DataAccessException e) {
            return null;
        }
        return modulo;
    }
    public boolean save(Modulo modulo) {
        String sql = "INSERT INTO modulo(id_curso,titulo,carga_horaria,descricao) VALUES(?,?,?,?,?)";
        return jdbcTemplate.update(sql,modulo.getId_curso(),modulo.getTitulo(),modulo.getCargaHoraria(),modulo.getDescricao_curso()) > 0;
    }
    public boolean delete(Long id) {
        String sql = "delete from modulo where id_modulo = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
    public boolean update(Modulo modulo, Long id) {
        String sql = "UPDATE modulo SET titulo = ? ,carga_horaria = ? ,descricao = ? WHERE id_modulo = ?";
        return jdbcTemplate.update(sql,modulo.getTitulo(),modulo.getCargaHoraria(),modulo.getDescricao_curso(),id) > 0;
    }
}
