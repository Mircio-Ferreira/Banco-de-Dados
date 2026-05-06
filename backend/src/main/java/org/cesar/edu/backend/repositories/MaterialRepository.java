package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Material;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MaterialRepository {
    private final JdbcTemplate jdbcTemplate;
    public MaterialRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Material> rowMapper = new RowMapper<Material>() {
        public Material mapRow(ResultSet rs, int rowNum) throws SQLException {
            Material material = new Material();
            material.setId_material(rs.getLong("id_material"));
            material.setId_aula(rs.getLong("id_aula"));
            material.setId_modulo(rs.getLong("id_modulo"));
            material.setId_curso(rs.getLong("id_curso"));
            material.setNome(rs.getString("nome"));
            material.setLink_material(rs.getString("link_material"));
            return material;
        }
    };
    public List<Material> findAll() {
        return jdbcTemplate.query("SELECT * FROM material", rowMapper);
    }
    public Material findById(Long id_material, Long id_aula,  Long id_modulo, Long id_curso) {
        try{
            String sql = "SELECT * FROM material WHERE id_material = ? AND id_aula = ? AND id_modulo = ? AND id_curso = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id_material, id_aula, id_modulo, id_curso);
        }
        catch(Exception e){
            return null;
        }
    }
    public boolean save(Material material) {
        String sql = "INSERT INTO material(id_aula, id_modulo, id_curso, nome, link_material) VALUES(?,?,?,?,?)";
        return jdbcTemplate.update(sql, material.getId_aula(),material.getId_modulo(),material.getId_curso(),material.getNome(),material.getLink_material()) > 0;
    }
    public boolean delete(Long id_material, Long id_aula, Long id_modulo, Long id_curso) {
        String sql = "DELETE FROM material WHERE id_material = ? AND id_aula = ? AND id_modulo = ? AND id_curso = ?";
        return jdbcTemplate.update(sql, id_material, id_aula, id_modulo, id_curso) > 0;
    }
    public boolean update(Material material, Long id_material, Long id_aula, Long id_modulo, Long id_curso){
        String sql = "UPDATE material SET nome = ?, link_material = ? WHERE id_material = ? AND id_aula = ? AND id_modulo = ? AND id_curso = ?";
        return jdbcTemplate.update(sql,material.getNome(),material.getLink_material(), id_material, id_aula, id_modulo, id_curso) > 0;
    }
}
