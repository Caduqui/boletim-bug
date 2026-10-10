import java.math.RoundingMode

import spock.lang.Specification

// Testes do cálculo da média.
// Cobrem o bug 1: o loop de calcularMedia começava em 1 e ignorava a primeira nota.
class BoletimSpec extends Specification {

    def boletim = new Boletim()

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
        given: "um script cuja saída é capturada em vez de ir para o console"
        def saida = new StringWriter()
        def script = new Boletim(new Binding(out: new PrintWriter(saida)))

        when:
        script.run()

        then:
        def texto = saida.toString()
        texto.contains("Ana: média 8.17")
        texto.contains("Bruno: média 6.00")
        texto.contains("Carla: média 7.00")
        texto.contains("Diego: média 7.00")
    }
}
