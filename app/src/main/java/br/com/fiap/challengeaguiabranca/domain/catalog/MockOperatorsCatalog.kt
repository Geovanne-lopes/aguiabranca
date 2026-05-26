package br.com.fiap.challengeaguiabranca.domain.catalog

data class MockOperator(
    val id: String,
    val name: String,
    val email: String
)

object MockOperatorsCatalog {

    val operators: List<MockOperator> = listOf(
        MockOperator("mock-op-joao", "João Pereira", "joao.pereira@innovatecorp.com"),
        MockOperator("mock-op-maria", "Maria Santos", "maria.santos@innovatecorp.com"),
        MockOperator("mock-op-pedro", "Pedro Oliveira", "pedro.oliveira@innovatecorp.com"),
        MockOperator("mock-op-lucia", "Lúcia Ferreira", "lucia.ferreira@innovatecorp.com"),
        MockOperator("mock-op-rafael", "Rafael Costa", "rafael.costa@innovatecorp.com"),
        MockOperator("demo-operador", "Ana Silva", "operador@innovatecorp.com")
    )

    fun findById(authorId: String): MockOperator? =
        operators.find { it.id == authorId }

    fun resolveName(authorId: String): String =
        findById(authorId)?.name ?: "Colaborador"

    fun resolveEmail(authorId: String): String? =
        findById(authorId)?.email
}
