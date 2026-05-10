package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.Aluno;
import org.cesar.edu.backend.models.ConsultaPegarAlunoComAulasNaoAssistidas;
import org.cesar.edu.backend.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AlunoRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public AlunoRepository(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
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
    private final RowMapper pegarAlunoComAulasNaoAssistidas = new RowMapper<ConsultaPegarAlunoComAulasNaoAssistidas>() {
        @Override
        public ConsultaPegarAlunoComAulasNaoAssistidas mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConsultaPegarAlunoComAulasNaoAssistidas consultaPegarAlunoComAulasNaoAssistidas = new ConsultaPegarAlunoComAulasNaoAssistidas();
            consultaPegarAlunoComAulasNaoAssistidas.setCpf(rs.getString("cpf_aluno"));
            consultaPegarAlunoComAulasNaoAssistidas.setNome_curso(rs.getString("nome_curso"));
            return consultaPegarAlunoComAulasNaoAssistidas;
        }
    };
    public List<Aluno> findAll() {
        return jdbcTemplate.query("SELECT * FROM aluno", rowMapper);
    }

    public Aluno findByCpf(String cpf) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM aluno WHERE cpf_aluno = ?", rowMapper, cpf);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
        } else {
            return false;
        }
    }

    public boolean delete(String cpfAluno) {
        return userRepository.delete(cpfAluno);
    }

    //retorna cursos que foram comprados mas que não tiveram nenhuma aula visualizada pelo aluno
    public List<ConsultaPegarAlunoComAulasNaoAssistidas> pegarAlunoComAulasNaoAssistidas() {
        String sql = """
                SELECT a.cpf_aluno, c.nome AS nome_curso
                FROM aluno a
                JOIN compra co
                ON a.cpf_aluno = co.cpf_aluno
                JOIN curso c
                ON co.id_curso = c.id_curso
                LEFT JOIN assistir ass
                ON co.id_curso = ass.id_curso AND co.cpf_aluno = ass.cpf_aluno
                WHERE ass.id_aula IS NULL;
                """;
        return jdbcTemplate.query(sql, pegarAlunoComAulasNaoAssistidas);
    }
}
