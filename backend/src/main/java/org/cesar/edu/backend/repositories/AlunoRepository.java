package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Aluno;
import org.cesar.edu.backend.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class AlunoRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    public AlunoRepository(JdbcTemplate jdbcTemplate,  UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }
    private final RowMapper<Aluno> rowMapper = new RowMapper<Aluno>() {
        @Override
        public Aluno mapRow(ResultSet rs, int rowNum) throws SQLException {
            Aluno aluno = new Aluno();
            aluno.setCpf(rs.getString("cpf_aluno"));
            return aluno;
        }
    };
    public List<Aluno> findAll() {
        return jdbcTemplate.query("SELECT * FROM aluno", rowMapper);
    }
    public Aluno findByCpf(String cpf) {
        return jdbcTemplate.queryForObject("SELECT * FROM aluno WHERE cpf_aluno = ?", rowMapper, cpf);
    }
    public boolean save(Aluno aluno) {
        boolean salvo = userRepository.save((User) aluno);
        if (salvo) {
            int linhasAlteradas = jdbcTemplate.update("INSERT INTO aluno(cpf_aluno) VALUES (?)", aluno.getCpf());
            return linhasAlteradas > 0 ? true : false;
        } else {
            return false;
        }
    }
    public boolean update(Aluno aluno, String cpfUserAntigo) {
        boolean salvo = userRepository.update((User) aluno, cpfUserAntigo);
        if (salvo) {
            int linhasAlteradas = jdbcTemplate.update("UPDATE aluno SET cpf_aluno = ? WHERE cpf_aluno = ?", aluno.getCpf(), cpfUserAntigo);
            return linhasAlteradas > 0 ? true : false;
        }
        else  {
            return false;
        }
    }
    public boolean delete(String cpfAluno) {
        return userRepository.delete(cpfAluno);
    }
}
