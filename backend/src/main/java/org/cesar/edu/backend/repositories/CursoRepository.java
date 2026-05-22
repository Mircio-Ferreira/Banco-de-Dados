package org.cesar.edu.backend.repositories;

import org.cesar.edu.backend.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    private final RowMapper<ConsultaCursoComCompras> cursoComComprasRowMapper = new RowMapper<ConsultaCursoComCompras>() {
        public ConsultaCursoComCompras mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConsultaCursoComCompras cursoComCompras = new ConsultaCursoComCompras();
            cursoComCompras.setId_curso(rs.getLong("id_curso"));
            cursoComCompras.setNome_curso(rs.getString("nome"));
            cursoComCompras.setPreco(rs.getDouble("preco"));
            cursoComCompras.setTotal_compras(rs.getLong("total_compras"));
            cursoComCompras.setReceita_estimada(rs.getDouble("receita_estimada"));
            return cursoComCompras;
        }
    };
    private final ResultSetExtractor<List<ConsultaPegarModulosEAulas>> pegarModulosEAulasExtractor = rs -> {
        Map<Long, ConsultaPegarModulosEAulas> modulos = new LinkedHashMap<>();

        while (rs.next()) {
            Long idModulo = rs.getLong("id_modulo");

            if (!modulos.containsKey(idModulo)) {
                Modulo modulo = new Modulo();
                modulo.setId_modulo(rs.getLong("id_modulo"));
                modulo.setTitulo(rs.getString("titulo_modulo"));

                ConsultaPegarModulosEAulas consulta = new ConsultaPegarModulosEAulas();
                consulta.setModulo(modulo);
                consulta.setAulas(new ArrayList<>());

                modulos.put(idModulo, consulta);
            }

            Long idAula = rs.getObject("id_aula", Long.class);

            if (idAula != null) {
                Aula aula = new Aula();
                aula.setId_aula(rs.getLong("id_aula"));
                aula.setTitulo(rs.getString("titulo_aula"));

                modulos.get(idModulo).getAulas().add(aula);
            }
        }

        return new ArrayList<>(modulos.values());
    };

    private final RowMapper<ConsultaCursoPremium> pegarCursosPremiumRowMapper = new RowMapper<ConsultaCursoPremium>() {
        public ConsultaCursoPremium mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConsultaCursoPremium cursoPremium = new ConsultaCursoPremium();
            cursoPremium.setId_curso(rs.getLong("id_curso"));
            cursoPremium.setNome_curso(rs.getString("nome_curso"));
            cursoPremium.setPreco(rs.getBigDecimal("preco"));
            return cursoPremium;
        }
    };
    private final RowMapper<ViewResumoGeralCurso> viewResumoGeralCursoRowMapper =
            (ResultSet rs, int rowNum) -> {
                ViewResumoGeralCurso resumo = new ViewResumoGeralCurso();

                resumo.setIdCurso(rs.getLong("id_curso"));
                resumo.setNomeCurso(rs.getString("nome_curso"));
                resumo.setPreco(rs.getBigDecimal("preco"));
                resumo.setTotalCompras(rs.getLong("total_compras"));
                resumo.setReceitaEstimada(rs.getBigDecimal("receita_estimada"));
                resumo.setMediaPrecoGeral(rs.getBigDecimal("media_preco_geral"));
                resumo.setClassificacaoPreco(rs.getString("classificacao_preco"));

                return resumo;
            };

    private final RowMapper<LogPrecoCurso> logPrecoCursoRowMapper =
            (ResultSet rs, int rowNum) -> {
                LogPrecoCurso log = new LogPrecoCurso();

                log.setIdLog(rs.getLong("id_log"));
                log.setIdCurso(rs.getLong("id_curso"));
                log.setPrecoAntigo(rs.getBigDecimal("preco_antigo"));
                log.setPrecoNovo(rs.getBigDecimal("preco_novo"));
                log.setDataAlteracao(rs.getTimestamp("data_alteracao").toLocalDateTime());
                log.setUsuarioBanco(rs.getString("usuario_banco"));

                return log;
            };

    public List<Curso> findAll() {
        return jdbcTemplate.query("select * from curso", cursoRowMapper);
    }

    public Curso findById(Long id_curso) {
        return jdbcTemplate.queryForObject("SELECT * FROM curso WHERE id_curso = ?", cursoRowMapper, id_curso);
    }

    public Curso findByNome(String nome) {
        String sql = "SELECT * FROM curso WHERE nome = ?";

        List<Curso> resultados = jdbcTemplate.query(sql, cursoRowMapper, nome);

        if (resultados.isEmpty()) {
            return null;
        }

        return resultados.get(0);
    }

    public boolean save(Curso curso) {
        int linhasAlteradas = jdbcTemplate.update("INSERT INTO curso(nome, preco, descricao) VALUES (?, ?, ?)", curso.getNome_curso(), curso.getPreco(), curso.getDescricao_curso());
        return linhasAlteradas > 0;
    }

    public boolean delete(Long id_curso) {
        return jdbcTemplate.update("DELETE FROM curso WHERE id_curso = ?", id_curso) > 0;
    }

    public boolean update(Curso curso, Long id_cursoAntigo) {
        int linhasAlteradas = jdbcTemplate.update("UPDATE curso SET nome = ?, preco = ?, descricao = ? WHERE id_curso = ?", curso.getNome_curso(), curso.getPreco(), curso.getDescricao_curso(), id_cursoAntigo);
        return linhasAlteradas > 0;
    }

    //consulta 1
    public List<ConsultaCursoComCompras> cursosComCompras() {
        String sql = "SELECT c.id_curso, c.nome, c.preco, COUNT(co.cpf_aluno) AS total_compras, COUNT(co.cpf_aluno) * c.preco AS receita_estimada FROM curso c JOIN compra co ON co.id_curso = c.id_curso GROUP BY c.id_curso, c.nome, c.preco HAVING COUNT(co.cpf_aluno) >= 1;";
        List<ConsultaCursoComCompras> resultados = jdbcTemplate.query(sql, cursoComComprasRowMapper);
        return resultados;
    }

    //consulta 2
    public List<ConsultaPegarModulosEAulas> pegarModulosEAulas(Long id_curso) {
        String sql = """
                    SELECT 
                        m.id_modulo,
                        m.titulo AS titulo_modulo,
                        a.id_aula,
                        a.titulo AS titulo_aula
                    FROM curso c
                    JOIN modulo m 
                        ON m.id_curso = c.id_curso
                    LEFT JOIN aula a 
                        ON m.id_modulo = a.id_modulo 
                       AND m.id_curso = a.id_curso
                    WHERE c.id_curso = ?
                    ORDER BY m.id_modulo, a.id_aula;
                """;

        return jdbcTemplate.query(sql, pegarModulosEAulasExtractor, id_curso);
    }

    //consulta 3
    public List<ConsultaCursoPremium> pegarCursosBaratos() {
        String sql = """
                SELECT
                c.nome AS nome_curso,
                c.id_curso,
                c.preco
                FROM curso c
                WHERE c.preco < (
                    SELECT 
                    AVG(c.preco) * 0.7 
                    FROM curso c
                )
                """;
        return jdbcTemplate.query(sql, pegarCursosPremiumRowMapper);
    }

    //function 1
    public Integer pegarHorasTotais(Long id_curso) {
        String sql = """
                        SELECT calcular_carga_horaria_total(?);
                """;
        Integer carga_horaria;

        try {
            carga_horaria = jdbcTemplate.queryForObject(sql, Integer.class, id_curso);

        } catch (Exception e) {
            return 0;
        }
        return carga_horaria;
    }

    //procedure 1
    public boolean aplicarDescontoEmCategoria(String categoria, Double desconto) {
        String sql = """
                        CALL aplicar_desconto_em_categoria(?,?);
                """;
        try {
            jdbcTemplate.update(sql, categoria, desconto);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    //view 2
    public List<ViewResumoGeralCurso> viewResumoGeralCurso() {
        String sql = """
                        SELECT *
                        FROM vw_resumo_geral_cursos;
                """;
        return jdbcTemplate.query(sql, viewResumoGeralCursoRowMapper);
    }

    public List<ViewResumoGeralCurso> viewResumoGeralCursos(Long id_curso) {
        String sql = """
                        SELECT *
                        FROM vw_resumo_geral_cursos c
                        WHERE c.id_curso = ?;
                """;
        return jdbcTemplate.query(sql, viewResumoGeralCursoRowMapper, id_curso);
    }

    //Trigger 1
    public List<LogPrecoCurso> pegarHistoricoPrecoCurso(Long id_curso) {
        String sql = """
                        SELECT *
                        FROM log_preco_cursos p
                        WHERE p.id_curso = ?;
                """;
        return jdbcTemplate.query(sql, logPrecoCursoRowMapper, id_curso);
    }

}
