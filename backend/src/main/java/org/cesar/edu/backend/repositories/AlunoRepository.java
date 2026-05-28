package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
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
            consultaPegarAlunoComAulasNaoAssistidas.setNome_aluno(rs.getString("nome_aluno"));
            consultaPegarAlunoComAulasNaoAssistidas.setNome_curso(rs.getString("nome_curso"));
            consultaPegarAlunoComAulasNaoAssistidas.setIdCurso(rs.getLong("id_curso"));
            return consultaPegarAlunoComAulasNaoAssistidas;
        }
    };
    private final RowMapper<ViewProgressoAlunoCurso> viewProgressoAlunoCursoRowMapper =
            (ResultSet rs, int rowNum) -> {
                ViewProgressoAlunoCurso progresso = new ViewProgressoAlunoCurso();

                progresso.setCpfAluno(rs.getString("cpf_aluno"));
                progresso.setIdCurso(rs.getLong("id_curso"));
                progresso.setNomeCurso(rs.getString("nome_curso"));
                progresso.setTotalAulas(rs.getLong("total_aulas"));
                progresso.setAulasAssistidas(rs.getLong("aulas_assistidas"));
                progresso.setPercentualConclusao(rs.getBigDecimal("percentual_conclusao"));

                return progresso;
            };

    private final RowMapper<AlunoInativo> alunoInativoRowMapper = (rs, rowNum) -> {
        AlunoInativo alunoInativo = new AlunoInativo();

        alunoInativo.setCpfAluno(rs.getString("cpf_aluno"));
        alunoInativo.setNomeAluno(rs.getString("nome_aluno"));

        alunoInativo.setIdCurso(rs.getLong("id_curso"));
        alunoInativo.setNomeCurso(rs.getString("nome_curso"));

        alunoInativo.setDataCompra(rs.getDate("data_compra").toLocalDate());

        Date ultimaAulaAssistida = rs.getDate("ultima_aula_assistida");
        if (ultimaAulaAssistida != null) {
            alunoInativo.setUltimaAulaAssistida(ultimaAulaAssistida.toLocalDate());
        }

        alunoInativo.setDataReferenciaInatividade(
                rs.getDate("data_referencia_inatividade").toLocalDate()
        );

        alunoInativo.setDiasInativo(rs.getInt("dias_inativo"));
        alunoInativo.setMotivo(rs.getString("motivo"));

        alunoInativo.setDataAtualizacao(
                rs.getTimestamp("data_atualizacao").toLocalDateTime()
        );

        return alunoInativo;
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

    //consulta 4
    //retorna cursos que foram comprados mas que não tiveram nenhuma aula visualizada pelo aluno
//    Curso com maior abandono inicial
//    Total de alunos que compraram mas nunca começaram
//    Ranking de cursos com baixa ativação
    public List<ConsultaPegarAlunoComAulasNaoAssistidas> pegarAlunoComAulasNaoAssistidas() {
        String sql = """
                SELECT a.cpf_aluno, u.nome AS nome_aluno, c.nome AS nome_curso, c.id_curso
                FROM aluno a
                JOIN usuario u
                ON a.cpf_aluno = u.cpf
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

    //procedure 2
    public void atualizarAlunosInativos() {
        String sql = """
                        CALL atualizar_alunos_inativos();
                """;
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException e) {
            throw new RuntimeException("Erro ao atualizar alunos inativos", e);
        }
    }

    public List<AlunoInativo> alunosInativos(){
        String sql = """
                SELECT *
                FROM alunos_inativos;
                """;
        return jdbcTemplate.query(sql,alunoInativoRowMapper);
    }

    //view 1
    public List<ViewProgressoAlunoCurso> pegarTodosProgressosAlunosCurso() {
        String sql = """
                SELECT *
                FROM vw_progresso_aluno_curso;
                """;
        return jdbcTemplate.query(sql, viewProgressoAlunoCursoRowMapper);
    }

    public List<ViewProgressoAlunoCurso> pegarProgressoAlunoCurso(String cpfAluno) {
        String sql = """
                        SELECT *
                        FROM vw_progresso_aluno_curso
                        WHERE cpf_aluno = ? ;
                """;
        List<ViewProgressoAlunoCurso> progresso;
        try {
            progresso = jdbcTemplate.query(sql, viewProgressoAlunoCursoRowMapper, cpfAluno);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return progresso;
    }
}
