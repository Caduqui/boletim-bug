# Boletim da turma

Script que calcula a média de cada aluno a partir de três notas
e informa a situação final:

- Média >= 7.0: Aprovado
- Média >= 5.0: Recuperação
- Abaixo disso: Reprovado

## Pré-requisitos

- Java 17 ou superior instalado (confira com `java -version`)

Não é preciso instalar o Gradle nem o Groovy: o projeto usa o Gradle Wrapper,
que baixa tudo automaticamente na primeira execução.

## Como executar

Na pasta do projeto, rode:

Linux / macOS:

    ./gradlew run -q

Windows:

    gradlew.bat run -q

A opção `-q` esconde as mensagens do Gradle e mostra apenas a saída do programa.
A primeira execução pode demorar um pouco, pois as dependências serão baixadas.

> Se aparecer "Permission denied" no Linux/macOS, rode `chmod +x gradlew` e tente novamente.

## Estrutura do projeto

    cacandoBugMediaEscolar/
    ├── build.gradle                     # configuração do build
    ├── settings.gradle                  # nome do projeto
    ├── gradlew / gradlew.bat            # Gradle Wrapper
    └── src/main/groovy/Boletim.groovy   # código do boletim

## Problema relatado

Os professores conferiram as notas manualmente e o resultado
esperado é:

    Ana: média 8.17 - Aprovado
    Bruno: média 6.00 - Recuperação
    Carla: média 7.00 - Aprovado
    Diego: média 7.00 - Aprovado

Mas o script está mostrando valores diferentes.

## Seu desafio

Existem **pelo menos 3 bugs** escondidos no `Boletim.groovy`.
Seu objetivo é encontrar e corrigir todos, com a ajuda de um agente de IA,
até que a saída do script fique **idêntica** ao resultado esperado acima.

Os bugs se escondem uns atrás dos outros: corrigir um deles pode fazer
aparecer um problema que antes não dava para ver. Por isso, não pare no
primeiro acerto.

### Dicas para caçar os bugs

- **Rode o script depois de cada correção** e compare a saída, linha por
  linha, com o resultado esperado. Se ainda houver diferença, ainda há bug.
- **Um código sem erros não é um código correto.** O programa pode rodar
  sem nenhuma mensagem de erro e ainda assim seguir uma regra diferente da
  que a escola definiu.
- **Dê contexto para a IA.** Ela só sabe o que você mostra a ela. Compare
  as respostas quando você envia apenas o código e quando envia também as
  regras da escola e o resultado esperado descritos neste README.
- **Preste atenção nos casos de fronteira.** Pense no que deveria acontecer
  com um aluno que tira exatamente a nota de corte.
- **Entenda antes de aceitar.** Peça para a IA explicar por que cada linha
  estava errada. Se a explicação não fizer sentido para você, pergunte de
  novo.

### Checklist

- [x] A média de todos os alunos está igual à esperada
- [] A situação de todos os alunos está igual à esperada
- [x] Consigo explicar, com minhas palavras, cada bug que corrigi

## Sobre o bug 1

### O problema

A função `calcularMedia` em `Boletim.groovy` estava com um erro de índice no loop:

```groovy
def calcularMedia(List notas) {
    def soma = 0
    for (int i = 1; i < notas.size(); i++) {  // ERRO: começa em 1
        soma += notas[i]
    }
    return soma / notas.size()
}
```

O loop `for` iniciava em `i = 1` em vez de `i = 0`. Como as listas em Groovy (e na maioria das linguagens) são indexadas a partir de **0**, isso fazia com que a **primeira nota de cada aluno fosse ignorada** ao calcular a média.

### Exemplo prático

Para a aluna **Ana**, com notas `[8.0, 7.5, 9.0]`:

- **Antes (bug)**: somava apenas os índices 1 e 2 → `7.5 + 9.0 = 16.5`, depois dividia por 3 → `5.5`
- **Depois (correto)**: somava todos os índices 0, 1 e 2 → `8.0 + 7.5 + 9.0 = 24.5`, depois dividia por 3 → `8.17`

### A correção

Mudei o início do loop de `i = 1` para `i = 0`:

```groovy
def calcularMedia(List notas) {
    def soma = 0
    for (int i = 0; i < notas.size(); i++) {  // Correto: começa em 0
        soma += notas[i]
    }
    return soma / notas.size()
}
```

Agora todas as notas são somadas e a média é calculada corretamente.

### Como identificar

Esse bug não gerava erro de execução — o script rodava normalmente. Mas a saída estava completamente distorcida porque uma nota de cada aluno não entrava no cálculo. Isso mostra que é importante **testar com dados reais** e **comparar com o resultado esperado**, e não confiar apenas em que o programa "roda sem erro".

## Sobre o bug 2

### O problema

A função `situacao` em `Boletim.groovy` estava usando um operador de comparação estrito (`>`) ao verificar se o aluno estava aprovado:

```groovy
if (media > mediaAprovacao) {
    return "Aprovado"
} else if (media >= mediaRecuperacao) {
    return "Recuperação"
}
```

As regras da escola estabelecem que a **média mínima de aprovação é 7.0**. Mas o operador `>` significa "estritamente maior que", o que fazia com que um aluno com **média exatamente 7.0** não fosse considerado aprovado e caísse na condição de Recuperação.

### Exemplo prático

Para um aluno com média **exatamente 7.0** (como a **Carla**):

- **Antes (bug)**: `7.0 > 7.0` é `false` → vai para `else if` → **Recuperação** ❌
- **Depois (correto)**: `7.0 >= 7.0` é `true` → **Aprovado** ✅

### A correção

Alteramos o operador de `>` (maior que) para `>=` (maior ou igual a):

```groovy
if (media >= mediaAprovacao) {
    return "Aprovado"
} else if (media >= mediaRecuperacao) {
    return "Recuperação"
}
```

Agora a lógica fica:

| Média | Situação |
|---|---|
| >= 7.0 | **Aprovado** |
| >= 6.5 e < 7.0 | **Recuperação** |
| < 6.5 | **Reprovado** |

### Como identificar

Assim como o bug 1, esse erro não causava nenhuma exceção — o programa rodava perfeitamente e apenas classificava os alunos com média exatamente igual à nota de corte de forma errada. Isso reforça a dica do checklist: **preste atenção nos casos de fronteira**, ou seja, no que deve acontecer com um aluno que tira **exatamente** a nota de corte (neste caso, a Carla com 7.0). Testar apenas com notas "claras" (como 8.0 ou 6.0) não revela esse tipo de problema — é preciso testar os valores limites da regra.

## Sobre o bug 3

### O problema

A constante que define a nota de corte da recuperação, na seção "Regras da escola" do `Boletim.groovy`, estava com o valor errado:

```groovy
final MEDIA_APROVACAO = 7.0
final MEDIA_RECUPERACAO = 6.5  // ERRO: a regra da escola é 5.0
```

As regras da escola estabelecem que o aluno com **média >= 5.0** (e abaixo de 7.0) fica em **Recuperação**. Com a constante em `6.5`, todo aluno com média entre 5.0 e 6.5 era classificado como **Reprovado**, mesmo tendo direito à recuperação.

A função `situacao` estava correta: ela apenas compara a média com o valor que recebe. O erro estava no valor passado para ela.

### Exemplo prático

Para o aluno **Bruno**, com notas `[5.0, 6.0, 7.0]` e média `6.00`:

- **Antes (bug)**: `6.0 >= 7.0` é `false` → `6.0 >= 6.5` é `false` → **Reprovado** ❌
- **Depois (correto)**: `6.0 >= 7.0` é `false` → `6.0 >= 5.0` é `true` → **Recuperação** ✅

### A correção

Mudei o valor da constante de `6.5` para `5.0`:

```groovy
final MEDIA_APROVACAO = 7.0
final MEDIA_RECUPERACAO = 5.0  // Correto: igual à regra da escola
```

Agora a lógica segue as regras da escola:

| Média | Situação |
|---|---|
| >= 7.0 | **Aprovado** |
| >= 5.0 e < 7.0 | **Recuperação** |
| < 5.0 | **Reprovado** |

### Como identificar

Esse bug estava escondido atrás do bug 1. Enquanto a média era calculada errada, a situação de quase todos os alunos saía diferente da esperada, e não dava para saber se a culpa era da média ou da nota de corte. Só depois de corrigir a média (e a comparação do bug 2) sobrou uma única diferença na saída: o Bruno aparecia como Reprovado em vez de Recuperação.

Ele também é um exemplo da dica "um código sem erros não é um código correto": não há nada de errado na lógica do programa, apenas um número que não bate com a regra definida pela escola. Por isso foi preciso comparar o código com as regras descritas no início deste README, e não só ler o código procurando erros.
