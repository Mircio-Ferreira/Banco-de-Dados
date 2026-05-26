function setDescontoStatus(tipo, msg) {
            const el = document.getElementById("descontoStatus");
            el.className = `desconto-status ${tipo}`;
            el.textContent = msg;
        }

        async function aplicarDesconto() {
            const categoria = document.getElementById("descontoCategoria").value.trim();
            const desconto = parseFloat(document.getElementById("descontoValor").value);
            const btn = document.getElementById("btnAplicarDesconto");

            if (!categoria) {
                setDescontoStatus("err", "Informe o nome da categoria.");
                return;
            }
            if (isNaN(desconto) || desconto < 0 || desconto > 100) {
                setDescontoStatus("err", "Desconto deve estar entre 0 e 100.");
                return;
            }

            btn.disabled = true;
            setDescontoStatus("info", `Aplicando ${desconto}% em "${categoria}"...`);

            try {
                const res = await fetch("http://localhost:8080/api/v1/curso/desconto-geral", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-User-CPF": currentUser.cpf
                    },
                    body: JSON.stringify({ categoria, desconto })
                });

                const texto = await res.text();
                if (!res.ok) throw new Error(texto || "Falha ao aplicar desconto.");

                setDescontoStatus("ok",
                    `Desconto de ${desconto}% aplicado aos cursos da categoria "${categoria}".`);

                document.getElementById("descontoCategoria").value = "";
                document.getElementById("descontoValor").value = "";

                await reloadUser();
            } catch (err) {
                console.error(err);
                setDescontoStatus("err", "Erro: " + err.message);
            } finally {
                btn.disabled = false;
            }
        }