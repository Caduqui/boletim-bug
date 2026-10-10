import java.math.RoundingMode

import spock.lang.Specification

// Testes do boletim.
// Bug 1: o loop de calcularMedia começava em 1 e ignorava a primeira nota.
// Bug 2: situacao usava > em vez de >= e não aprovava quem tirava exatamente a nota de corte.
// Bug 3: a nota de corte da recuperação estava em 6.5, e a regra da escola é 5.0.
class BoletimSpec extends Specification {

    // Regras da escola
    static final MEDIA_APROVACAO = 7.0
    static final MEDIA_RECUPERACAO = 5.0

    def boletim = new Boletim()

    // Roda o script capturando a saída em vez de mandar para o console
    def executarBoletim() {
        def saida = new StringWriter()
        new Boletim(new Binding(out: new PrintWriter(saida))).run()
        return saida.toString()
    }

    // ---------------------------------------------------------------
    // Bug 1: cálculo da média
    // ---------------------------------------------------------------
    def "calcularMedia considera a primeira nota"() {
        expect: "se a primeira nota fosse ignorada, a média seria 0"
        boletim.calcularMedia([9.0, 0.0, 0.0]) == 3.0
    }

    def "média de #aluno com notas #notas é #esperada"() {
        expect:
        boletim.calcularMedia(notas).setScale(2, RoundingMode.HALF_UP) == esperada

        where:
        aluno   | notas            | esperada
        "Ana"   | [8.0, 7.5, 9.0]  | 8.17
        "Bruno" | [5.0, 6.0, 7.0]  | 6.00
        "Carla" | [10.0, 4.0, 7.0] | 7.00
        "Diego" | [3.0, 9.0, 9.0]  | 7.00
    }

    def "o boletim impresso mostra a média esperada de cada aluno"() {
        when:
        def texto = executarBoletim()

        then:
        texto.contains("Ana: média 8.17")
        texto.contains("Bruno: média 6.00")
        texto.contains("Carla: média 7.00")
        texto.contains("Diego: média 7.00")
    }

    // ---------------------------------------------------------------
    // Bug 2: aprovação com média igual à nota de corte
    // ---------------------------------------------------------------
    def "média #media, em torno da nota de aprovação, resulta em #esperada"() {
        expect: "com > em vez de >=, a média 7.00 daria Recuperação"
        boletim.situacao(media, MEDIA_APROVACAO, MEDIA_RECUPERACAO) == esperada

        where:
        media | esperada
        6.99  | "Recuperação"
        7.00  | "Aprovado"
        7.01  | "Aprovado"
    }

    def "média calculada de #aluno dá exatamente a nota de corte e resulta em Aprovado"() {
        expect:
        boletim.situacao(boletim.calcularMedia(notas), MEDIA_APROVACAO, MEDIA_RECUPERACAO) == "Aprovado"

        where:
        aluno   | notas
        "Carla" | [10.0, 4.0, 7.0]
        "Diego" | [3.0, 9.0, 9.0]
    }

    def "o boletim impresso mostra como Aprovado quem tirou exatamente a nota de corte"() {
        when:
        def texto = executarBoletim()

        then:
        texto.contains("Carla: média 7.00 - Aprovado")
        texto.contains("Diego: média 7.00 - Aprovado")
    }

    // ---------------------------------------------------------------
    // Bug 3: nota de corte da recuperação
    // ---------------------------------------------------------------
    // A constante MEDIA_RECUPERACAO fica dentro do script e não é visível daqui,
    // então só o teste da saída impressa detecta se o valor dela voltar a ficar errado.
    def "o boletim impresso mostra como Recuperação quem tem média entre 5.0 e 6.5"() {
        when:
        def texto = executarBoletim()

        then: "com a nota de corte em 6.5, o Bruno sairia como Reprovado"
        texto.contains("Bruno: média 6.00 - Recuperação")
    }

    def "média #media, com a nota de corte da escola, resulta em #esperada"() {
        expect:
        boletim.situacao(media, MEDIA_APROVACAO, MEDIA_RECUPERACAO) == esperada

        where:
        media | esperada
        4.99  | "Reprovado"
        5.00  | "Recuperação"
        6.00  | "Recuperação"
        6.50  | "Recuperação"
    }
}
