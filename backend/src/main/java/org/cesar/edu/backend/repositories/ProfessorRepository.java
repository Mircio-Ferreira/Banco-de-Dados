package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Professor;
import org.cesar.edu.backend.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProfessorRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public ProfessorRepository(JdbcTemplate jdbcTemplate,  UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    private final RowMapper<Professor> rowMapper = new RowMapper<Professor>() {
        public Professor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Professor p = new Professor();
            p.setCpf(rs.getString("cpf_professor"));
            return  p;
        }
    };
    public List<Professor> findAll() {
        return jdbcTemplate.query("select * from professor", rowMapper);
    }
    public Professor findByCpf(String cpf_professor) {
        return jdbcTemplate.queryForObject("SELECT * FROM professor WHERE cpf_professor = ?", rowMapper, cpf_professor);
    }
    public boolean save(Professor professor) {
        boolean sucesso = userRepository.save((User) professor);
        if(sucesso){
            int linhasAlteradas = jdbcTemplate.update("INSERT INTO professor (cpf_professor) VALUES (?)", professor.getCpf());
            return linhasAlteradas > 0 ? true : false;
        }
        else{
            return false;
        }
    }
    public boolean update(Professor professorNovo, String cpfAntigo){
        boolean sucesso = userRepository.update((User) professorNovo,  cpfAntigo);
        if(sucesso){
            int linhasAlteradas = jdbcTemplate.update("UPDATE professor SET cpf_professor = ? WHERE cpf_professor = ?", professorNovo.getCpf(), cpfAntigo);
            return linhasAlteradas > 0 ? true : false;
        }
        else{
            return false;
        }
    }
    public boolean delete(String cpfProfessor){
        return userRepository.delete(cpfProfessor);
    }
}
