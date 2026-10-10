// Calcula a média de cada aluno e informa a situação final.

// ---------------------------------------------------------------
// 1. Dados da turma
// ---------------------------------------------------------------
def alunos = [
    "Ana"  : [8.0, 7.5, 9.0],
    "Bruno": [5.0, 6.0, 7.0],
    "Carla": [10.0, 4.0, 7.0],
    "Diego": [3.0, 9.0, 9.0],
]

// ---------------------------------------------------------------
// 2. Regras da escola
// ---------------------------------------------------------------
final MEDIA_APROVACAO = 7.0
final MEDIA_RECUPERACAO = 6.5

// ---------------------------------------------------------------
// 3. Cálculo da média
// ---------------------------------------------------------------
def calcularMedia(List notas) {
    def soma = 0
    for (int i = 0; i < notas.size(); i++) {
        soma += notas[i]
    }
    return soma / notas.size()
}

// ---------------------------------------------------------------
// 4. Situação final do aluno
// ---------------------------------------------------------------
def situacao(media, mediaAprovacao, mediaRecuperacao) {
    if (media >= mediaAprovacao) {
        return "Aprovado"
    } else if (media >= mediaRecuperacao) {
        return "Recuperação"
    } else {
        return "Reprovado"
    }
}

// ---------------------------------------------------------------
// 5. Impressão do boletim
// ---------------------------------------------------------------
println "=== Boletim da turma ==="
alunos.each { nome, notas ->
    def media = calcularMedia(notas)
    def mediaFormatada = String.format(Locale.US, "%.2f", media)
    println "${nome}: média ${mediaFormatada} - ${situacao(media, MEDIA_APROVACAO, MEDIA_RECUPERACAO)}"
}