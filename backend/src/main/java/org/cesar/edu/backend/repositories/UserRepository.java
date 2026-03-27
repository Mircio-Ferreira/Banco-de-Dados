package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate template;
    public UserRepository(JdbcTemplate template) {
        this.template = template;
    }
    private final RowMapper<User> usuarioRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User usuario = new User();
            usuario.setCpf(rs.getString("cpf"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
            usuario.setSenha(rs.getString("senha"));
            usuario.setLogradouro(rs.getString("logradouro"));
            usuario.setNumero(rs.getInt("numero"));
            usuario.setCep(rs.getString("cep"));
            return usuario;
        }
    };
    public List<User> findAll() {
        return template.query("SELECT * FROM usuario",usuarioRowMapper);
    }
    public User findByCpf(String cpf) {
        return template.queryForObject("SELECT * FROM usuario WHERE cpf = ?",usuarioRowMapper,cpf);
    }
    public boolean save(User user) {
        int linhasAlteradas = template.update("INSERT INTO usuario VALUES (?,?,?,?,?,?,?)",
                user.getCpf(),
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getLogradouro(),
                user.getNumero(),
                user.getCep());
        return linhasAlteradas > 0? true:false;
    }
    public boolean update(User userAtual, String cpfUserAntigo){
        int linhasAlteradas = template.update("UPDATE usuario SET cpf = ?, nome = ?, email = ?, senha = ?, logradouro = ?, numero = ?, cep = ? WHERE cpf = ?",
                userAtual.getCpf(),
                userAtual.getNome(),
                userAtual.getEmail(),
                userAtual.getSenha(),
                userAtual.getLogradouro(),
                userAtual.getNumero(),
                userAtual.getCep(),
                cpfUserAntigo);
        return linhasAlteradas > 0? true:false;
    }
    public boolean delete(String cpf){
        return template.update("DELETE FROM usuario WHERE cpf = ?",cpf) > 0 ? true : false;
    }
}
