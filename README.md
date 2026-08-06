# supple-date

[![Java 8+](https://img.shields.io/badge/Java-8%2B-orange)](https://www.oracle.com/java/)
[![Maven Package](https://img.shields.io/badge/GitHub%20Packages-io.github.dlduarte%3Asupple--date-blue)](https://github.com/dlduarte/supple-date/packages)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Biblioteca Java para **manipulação, conversão e formatação de datas** com uma API fluente e agnóstica de tipo.

O centro da biblioteca é o `Temporality`: você entrega **qualquer** tipo de data (`String`, `Long`, `Date`, `Calendar`, `LocalDate`, `LocalDateTime`, `LocalTime`, `GregorianCalendar`…), manipula com uma API encadeável e devolve em **qualquer outro** tipo — sem escrever `SimpleDateFormat`, `DateTimeFormatter` ou conversões manuais entre a API legada e a `java.time`.

```java
// String "17/05/2024" -> primeiro dia útil do mês seguinte -> java.util.Date
Date resultado = Temporality.of("17/05/2024", Chronos.BAR_D4)
        .plus(1, ChronoUnit.MONTHS)
        .startOfMonth()
        .preventWeekend()
        .parse(Date.class);
```

---

## Sumário

- [Instalação](#instalação)
  - [GitHub Packages (Maven)](#github-packages-maven)
  - [GitHub Packages (Gradle)](#github-packages-gradle)
  - [JitPack (sem autenticação)](#jitpack-sem-autenticação)
- [Conceitos](#conceitos)
- [Criando um Temporality](#criando-um-temporality)
- [Convertendo com parse](#convertendo-com-parse)
- [Catálogo de padrões: Chronos](#catálogo-de-padrões-chronos)
- [Manipulação de datas](#manipulação-de-datas)
- [Comparações](#comparações)
- [Diferença entre datas](#diferença-entre-datas)
- [Tempo por extenso](#tempo-por-extenso)
- [Fusos horários](#fusos-horários)
- [Tipos suportados e converters](#tipos-suportados-e-converters)
- [Criando seu próprio converter](#criando-seu-próprio-converter)
- [Usando a fábrica diretamente](#usando-a-fábrica-diretamente)
- [Exceções](#exceções)
- [Receitas](#receitas)
- [Limitações conhecidas](#limitações-conhecidas)
- [Migrando da 2.x para a 3.x](#migrando-da-2x-para-a-3x)
- [Build local](#build-local)
- [Licença](#licença)

---

## Instalação

**Coordenadas:**

| | |
|---|---|
| `groupId` | `io.github.dlduarte` |
| `artifactId` | `supple-date` |
| `version` | `3.0.0` |

A biblioteca **não tem dependências em runtime** (o Lombok é usado apenas em tempo de compilação, com escopo `provided`) e roda em **Java 8 ou superior**.

### GitHub Packages (Maven)

O GitHub Packages exige autenticação **mesmo para pacotes públicos**. São dois passos.

**1.** Adicione o repositório e a dependência no seu `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github-dlduarte</id>
        <url>https://maven.pkg.github.com/dlduarte/supple-date</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.dlduarte</groupId>
        <artifactId>supple-date</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

**2.** Adicione as credenciais no seu `~/.m2/settings.xml`. O `id` do `<server>` precisa bater exatamente com o `id` do `<repository>`:

```xml
<settings>
  <servers>
    <server>
      <id>github-dlduarte</id>
      <username>SEU_USUARIO_GITHUB</username>
      <password>SEU_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

> O token precisa apenas do escopo `read:packages`. Crie em **Settings → Developer settings → Personal access tokens (classic)**.

Dentro do GitHub Actions, use o token embutido — não é preciso criar nada:

```yaml
- run: mvn -B install
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### GitHub Packages (Gradle)

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/dlduarte/supple-date")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.dlduarte:supple-date:3.0.0")
}
```

### JitPack (sem autenticação)

Se você não quiser lidar com tokens, o JitPack compila a tag do repositório e serve o artefato sem exigir login:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.dlduarte</groupId>
    <artifactId>supple-date</artifactId>
    <version>v3.0.0</version>
</dependency>
```

> Pelo JitPack o `groupId` é `com.github.dlduarte` e a versão é a **tag** (`v3.0.0`), não o número da versão. O pacote Java continua sendo `io.github.dlduarte.suppledate`.

Você também pode baixar o `.jar` diretamente da [página de releases](https://github.com/dlduarte/supple-date/releases).

---

## Conceitos

A biblioteca tem quatro peças:

| Peça | Papel |
|---|---|
| **`Temporality`** | A data em si. Internamente guarda um `LocalDateTime`: toda entrada é convertida para ele, toda saída sai dele. |
| **`Chronos`** | Enum com os padrões de data mais usados (`dd/MM/yyyy`, `yyyy-MM-dd HH:mm:ss`…), para você não digitar strings de pattern. |
| **`PatternDateConverter`** | Contrato que ensina a biblioteca a converter um tipo qualquer de/para `LocalDateTime`. |
| **`WritingFormat`** | Descreve como um `Duration` vira texto por extenso ("2 dias e 3 horas"). |

O fluxo é sempre o mesmo:

```
qualquer tipo ──(converter)──► LocalDateTime ──(manipulação)──► LocalDateTime ──(converter)──► qualquer tipo
                               └────────────────  Temporality  ────────────────┘
```

---

## Criando um Temporality

Existem duas famílias de fábricas: `of(...)` para uma data que você já tem, e `now(...)` para o instante atual.

```java
// A partir de String: o pattern é obrigatório
Temporality t1 = Temporality.of("17/05/2024", "dd/MM/yyyy");
Temporality t2 = Temporality.of("17/05/2024", Chronos.BAR_D4);       // mesma coisa, sem string mágica

// A partir de tipos que já carregam a informação: pattern desnecessário
Temporality t3 = Temporality.of(LocalDate.of(2024, 5, 17));
Temporality t4 = Temporality.of(LocalDateTime.of(2024, 5, 17, 14, 30));
Temporality t5 = Temporality.of(LocalTime.of(23, 45));               // assume a data de hoje
Temporality t6 = Temporality.of(new Date());
Temporality t7 = Temporality.of(Calendar.getInstance());
Temporality t8 = Temporality.of(new GregorianCalendar());

// A partir de Long: o pattern descreve como ler os dígitos
Temporality t9  = Temporality.of(17052024L, Chronos.BASIC_D4);       // ddMMyyyy
Temporality t10 = Temporality.of(20240517143000L, Chronos.BASIC_D4HMS_ISO);

// Agora
Temporality agora  = Temporality.now();
Temporality agora2 = Temporality.now(Chronos.BAR_D4HMS);

// Atalho pelo próprio Chronos — idêntico às chamadas acima
Temporality t11 = Chronos.BAR_D4.of("17/05/2024");
Temporality t12 = Chronos.BAR_D4HMS.now();
```

O pattern informado na criação vira o **pattern padrão** da instância: ele é usado no `toString()` e como fallback do `parse()`.

```java
Temporality t = Temporality.of("17/05/2024 14:30:00", Chronos.BAR_D4HMS);

System.out.println(t);                        // 17/05/2024 14:30:00
System.out.println(t.parse(String.class));    // 17/05/2024 14:30:00 — herdou o pattern

// Sem pattern na criação, o toString() cai no ISO
System.out.println(Temporality.of(LocalDate.of(2024, 5, 17)));   // 2024-05-17 00:00:00
```

---

## Convertendo com parse

`parse(Class<T>)` devolve a data no tipo que você pedir.

```java
Temporality t = Temporality.of("17/05/2024 14:30:00", Chronos.BAR_D4HMS);

LocalDate         d1 = t.parse(LocalDate.class);          // 2024-05-17
LocalDateTime     d2 = t.parse(LocalDateTime.class);      // 2024-05-17T14:30
LocalTime         d3 = t.parse(LocalTime.class);          // 14:30
Date              d4 = t.parse(Date.class);               // Fri May 17 14:30:00 BRT 2024
Calendar          d5 = t.parse(Calendar.class);
GregorianCalendar d6 = t.parse(GregorianCalendar.class);
```

Para `String` e `Long`, o pattern define a saída:

```java
String s1 = t.parse(String.class, Chronos.BASIC_D4);        // "17052024"
String s2 = t.parse(String.class, "EEEE, dd 'de' MMMM");    // "sexta-feira, 17 de maio"
String s3 = t.parse(Chronos.TRACE_D4HMS_ISO);               // atalho: "2024-05-17 14:30:00"

Long   l1 = t.parse(Long.class, Chronos.BASIC_D4);          // 17052024
Long   l2 = t.parse(Long.class, Chronos.BASIC_D4HMS);       // 17052024143000
```

> O `LongConverter` formata a data como texto e depois remove tudo que não for dígito — então `parse(Long.class, Chronos.BAR_D4)` também devolve `17052024`, porque as barras são descartadas.

Conversão direta entre dois formatos de texto:

```java
String iso = Temporality.of("17/05/2024", Chronos.BAR_D4)
        .parse(String.class, Chronos.TRACE_D4_ISO);   // "2024-05-17"
```

`Temporality` também é um tipo conversível, o que permite reabrir uma data com outro pattern padrão:

```java
Temporality outro = t.parse(Temporality.class, Chronos.BAR_D4);
System.out.println(outro);   // 17/05/2024
```

---

## Catálogo de padrões: Chronos

Todos os valores do enum, com o que produzem para **17/05/2024 14:30:00**:

### Sem separador

| Constante | Pattern | Saída |
|---|---|---|
| `BASIC_D4` | `ddMMyyyy` | `17052024` |
| `BASIC_D4HMS` | `ddMMyyyyHHmmss` | `17052024143000` |
| `BASIC_D2` | `ddMMyy` | `170524` |
| `BASIC_D2HMS` | `ddMMyyHHmmss` | `170524143000` |
| `BASIC_D4_ISO` | `yyyyMMdd` | `20240517` |
| `BASIC_D4HMS_ISO` | `yyyyMMddHHmmss` | `20240517143000` |
| `BASIC_D2_ISO` | `yyMMdd` | `240517` |
| `BASIC_D2HMS_ISO` | `yyMMddHHmmss` | `240517143000` |
| `BASIC_HM` | `HHmm` | `1430` |

### Separado por barra

| Constante | Pattern | Saída |
|---|---|---|
| `BAR_D4` | `dd/MM/yyyy` | `17/05/2024` |
| `BAR_D4HM` | `dd/MM/yyyy HH:mm` | `17/05/2024 14:30` |
| `BAR_D4HMS` | `dd/MM/yyyy HH:mm:ss` | `17/05/2024 14:30:00` |
| `BAR_D2` | `dd/MM/yy` | `17/05/24` |
| `BAR_D2HM` | `dd/MM/yy HH:mm` | `17/05/24 14:30` |
| `BAR_D2HMS` | `dd/MM/yy HH:mm:ss` | `17/05/24 14:30:00` |
| `BAR_D4_ISO` | `yyyy/MM/dd` | `2024/05/17` |
| `BAR_D4HM_ISO` | `yyyy/MM/dd HH:mm` | `2024/05/17 14:30` |
| `BAR_D4HMS_ISO` | `yyyy/MM/dd HH:mm:ss` | `2024/05/17 14:30:00` |
| `BAR_D2_ISO` | `yy/MM/dd` | `24/05/17` |

### Separado por traço

| Constante | Pattern | Saída |
|---|---|---|
| `TRACE_D4` | `dd-MM-yyyy` | `17-05-2024` |
| `TRACE_D4HM` | `dd-MM-yyyy HH:mm` | `17-05-2024 14:30` |
| `TRACE_D4HMS` | `dd-MM-yyyy HH:mm:ss` | `17-05-2024 14:30:00` |
| `TRACE_D2` | `dd-MM-yy` | `17-05-24` |
| `TRACE_D2HM` | `dd-MM-yy HH:mm` | `17-05-24 14:30` |
| `TRACE_D2HMS` | `dd-MM-yy HH:mm:ss` | `17-05-24 14:30:00` |
| `TRACE_D4_ISO` | `yyyy-MM-dd` | `2024-05-17` |
| `TRACE_D4HM_ISO` | `yyyy-MM-dd HH:mm` | `2024-05-17 14:30` |
| `TRACE_D4HMS_ISO` | `yyyy-MM-dd HH:mm:ss` | `2024-05-17 14:30:00` |
| `TRACE_D2_ISO` | `yy-MM-dd` | `24-05-17` |

### Seguros para nome de arquivo

Sem `:`, que é inválido em caminhos no Windows.

| Constante | Pattern | Saída |
|---|---|---|
| `FILE_D2HM` | `yy-MM-dd HH mm` | `24-05-17 14 30` |
| `FILE_D4HM` | `yyyy-MM-dd HH mm` | `2024-05-17 14 30` |

Cada constante também funciona como fábrica:

```java
Chronos.BAR_D4.of("17/05/2024");                     // == Temporality.of(String, pattern)
Chronos.BAR_D4.of("17/05/2024", ZoneId.of("UTC"));
Chronos.FILE_D4HM.now();                             // == Temporality.now(pattern)
Chronos.BAR_D4.getPattern();                         // "dd/MM/yyyy"
```

Você não está preso ao enum — qualquer pattern de [`DateTimeFormatter`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) funciona como `String`:

```java
Temporality.now().parse(String.class, "'Relatório de' MMMM 'de' yyyy");
```

---

## Manipulação de datas

Todos os métodos de manipulação **alteram a instância** e devolvem `this`, permitindo encadeamento.

```java
Temporality t = Temporality.of("17/05/2024 14:30:00", Chronos.BAR_D4HMS);

t.plus(1, ChronoUnit.MONTHS);              // 17/06/2024 14:30:00
t.minus(10, ChronoUnit.DAYS);              // 07/06/2024 14:30:00
t.change(1, ChronoField.DAY_OF_MONTH);     // 01/06/2024 14:30:00
```

| Método | O que faz |
|---|---|
| `plus(long, ChronoUnit)` | Soma a quantidade na unidade informada |
| `minus(long, ChronoUnit)` | Subtrai a quantidade na unidade informada |
| `change(long, ChronoField)` | Define o valor absoluto de um campo |
| `startOfDay()` | Zera hora, minuto, segundo e nano → `00:00:00` |
| `endOfDay()` | Vai para o último instante do dia → `23:59:59.999999999` |
| `startOfMonth()` | Primeiro dia do mês às `00:00:00` |
| `endOfMonth()` | Último dia do mês às `23:59:59.999999999` |
| `preventWeekend()` | Se cair no sábado ou domingo, empurra para a segunda-feira |

```java
Chronos.BAR_D4HMS.of("17/05/2024 14:30:00").startOfDay();     // 17/05/2024 00:00:00
Chronos.BAR_D4HMS.of("17/05/2024 14:30:00").endOfDay();       // 17/05/2024 23:59:59
Chronos.BAR_D4HMS.of("17/05/2024 14:30:00").startOfMonth();   // 01/05/2024 00:00:00
Chronos.BAR_D4HMS.of("17/05/2024 14:30:00").endOfMonth();     // 31/05/2024 23:59:59

Chronos.BAR_D4.of("18/05/2024").preventWeekend();   // 20/05/2024 (sábado → segunda)
Chronos.BAR_D4.of("19/05/2024").preventWeekend();   // 20/05/2024 (domingo → segunda)
Chronos.BAR_D4.of("17/05/2024").preventWeekend();   // 17/05/2024 (sexta, não mexe)
```

> ⚠️ **`Temporality` é mutável.** Encadear é conveniente, mas se você precisa preservar a data original, crie uma cópia antes:
> ```java
> Temporality original = Chronos.BAR_D4.of("17/05/2024");
> Temporality copia = Temporality.of(original.parse(LocalDateTime.class), Chronos.BAR_D4);
> copia.plus(1, ChronoUnit.MONTHS);   // original permanece em 17/05/2024
> ```

Exemplo prático — intervalo do mês para uma consulta:

```java
Date inicio = Chronos.BAR_D4.of("17/05/2024").startOfMonth().parse(Date.class);
Date fim    = Chronos.BAR_D4.of("17/05/2024").endOfMonth().parse(Date.class);
```

---

## Comparações

Todos os comparadores aceitam **qualquer tipo suportado** como argumento — só exigem pattern quando o outro valor é `String` ou `Long`.

```java
Temporality t = Chronos.BAR_D4.of("17/05/2024");

t.isBefore(LocalDate.of(2024, 12, 25));      // true
t.isBefore("20/05/2024", "dd/MM/yyyy");      // true

t.isAfter(LocalDate.of(2024, 1, 1));         // true
t.isAfter("01/01/2024", "dd/MM/yyyy");       // true

t.isEqual(LocalDate.of(2024, 5, 17));        // true
t.isEqual("17/05/2024", "dd/MM/yyyy");       // true
```

`isBetween` é **inclusivo** nas duas pontas:

```java
// Com tipos que não precisam de pattern
t.isBetween(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));       // true

// Com um pattern para ambas as datas
t.isBetween("01/01/2024", "31/12/2024", "dd/MM/yyyy");                   // true

// Com patterns diferentes para cada ponta
t.isBetween("01/01/2024", "2024-12-31", "dd/MM/yyyy", "yyyy-MM-dd");     // true
```

---

## Diferença entre datas

`until` devolve a diferença **com sinal** na unidade escolhida (negativa se a outra data for anterior):

```java
Temporality t = Chronos.BAR_D4.of("17/05/2024");

t.until(LocalDate.of(2024, 12, 25), ChronoUnit.DAYS);       // 222
t.until("25/12/2024", "dd/MM/yyyy", ChronoUnit.MONTHS);     // 7
t.until(LocalDate.of(2024, 1, 1), ChronoUnit.DAYS);         // -137
```

Funciona com qualquer `ChronoUnit`: `SECONDS`, `MINUTES`, `HOURS`, `DAYS`, `WEEKS`, `MONTHS`, `YEARS`…

```java
long minutosRestantes = Temporality.now().until(prazoFinal, ChronoUnit.MINUTES);
long idade = Chronos.BAR_D4.of(dataNascimento).until(LocalDate.now(), ChronoUnit.YEARS);
```

`fullDate` faz a mesma conta, mas devolve o resultado **por extenso** e **sempre positivo** (usa valor absoluto):

```java
Temporality t = Chronos.BAR_D4HMS.of("17/05/2024 14:30:00");

t.fullDate(LocalDateTime.of(2024, 5, 20, 16, 0));       // "3 dias, 1 hora e 30 minutos"
t.fullDate("20/05/2024 16:00:00", Chronos.BAR_D4HMS);   // "3 dias, 1 hora e 30 minutos"
t.fullDate();                                           // diferença até agora
```

---

## Tempo por extenso

### Formato padrão

`Temporality.timeElapsedInWriting(Duration)` já vem com um formato em português:

```java
Duration d = Duration.ofDays(400).plusHours(3).plusMinutes(5).plusSeconds(30);

Temporality.timeElapsedInWriting(d);
// "1 ano, 35 dias, 3 horas, 5 minutos e 30 segundos"

Temporality.timeElapsedInWriting(Duration.ofHours(5).plusMinutes(2));
// "5 horas e 2 minutos"

Temporality.timeElapsedInWriting(Duration.ofHours(1));
// "1 hora"      (singular automático)

Temporality.timeElapsedInWriting(Duration.ofSeconds(30));
// "Agora"       (abaixo de 1 minuto)
```

Repare que:

- unidades com valor **zero são omitidas** — nada de "0 horas";
- o **singular/plural** é escolhido pelo valor;
- o **último separador** é diferente dos demais (`", "` entre os itens, `" e "` antes do último).

### Customizando com WritingFormat

Um `WritingFormat` define o texto de cada unidade e o que exibir quando a duração é insignificante. As unidades são posicionais e **opcionais** — informe só até onde quiser detalhar.

```java
WritingFormat.of(ifNow)
WritingFormat.of(ifNow, ano)
WritingFormat.of(ifNow, ano, dia)
WritingFormat.of(ifNow, ano, dia, hora)
WritingFormat.of(ifNow, ano, dia, hora, minuto)
WritingFormat.of(ifNow, ano, dia, hora, minuto, segundo)
```

**Formato compacto**, sem espaço entre número e unidade:

```java
WritingFormat compacto = WritingFormat.of("1m",
        UnitFormat.of("y", false),
        UnitFormat.of("d", false),
        UnitFormat.of("h", false),
        UnitFormat.of("m", false));

Temporality.timeElapsedInWriting(d, compacto);
// "1y, 35d, 3h e 5m"
```

**Inglês**, com plural explícito:

```java
WritingFormat english = WritingFormat.of(
        FormattingOption.of(",", " and"),
        "just now",
        UnitFormat.of("year", "years"),
        UnitFormat.of("day", "days"),
        UnitFormat.of("hour", "hours"),
        UnitFormat.of("minute", "minutes"),
        UnitFormat.of("second", "seconds"));

Temporality.timeElapsedInWriting(d, english);
// "1 year, 35 days, 3 hours, 5 minutes and 30 seconds"

Temporality.timeElapsedInWriting(Duration.ofDays(1).plusHours(1), english);
// "1 day and 1 hour"
```

**Granularidade reduzida** — informe menos unidades e as demais somem do texto:

```java
WritingFormat soDias = WritingFormat.of("hoje",
        UnitFormat.of("ano", "anos"),
        UnitFormat.of("dia", "dias"));

Temporality.timeElapsedInWriting(d, soDias);
// "1 ano e 35 dias"
```

#### UnitFormat

| Fábrica | Uso |
|---|---|
| `UnitFormat.of("dia")` | Mesmo texto no singular e no plural, com espaço antes |
| `UnitFormat.of("dia", "dias")` | Singular e plural distintos, com espaço antes |
| `UnitFormat.of("d", false)` | Mesmo texto, **sem** espaço antes (`"3d"`) |
| `UnitFormat.of("dia", "dias", false)` | Singular/plural distintos, sem espaço |

### Customizando separadores com FormattingOption

`FormattingOption` controla como os pedaços são unidos. Um espaço é sempre acrescentado após o separador.

```java
FormattingOption.of(",", " e")   // "1 ano, 2 dias e 3 horas"   (padrão)
FormattingOption.of(" /")        // "1 y / 2 d / 3 h / 4 m"     (mesmo separador em toda a lista)
FormattingOption.of(";", ";")    // "1 ano; 2 dias; 3 horas"
```

Passe-o como primeiro argumento de qualquer sobrecarga de `WritingFormat.of`:

```java
WritingFormat barras = WritingFormat.of(
        FormattingOption.of(" /"),
        "now",
        UnitFormat.of("y"), UnitFormat.of("d"), UnitFormat.of("h"), UnitFormat.of("m"));

barras.write(1, 2, 3, 4, 0);
// "1 y / 2 d / 3 h / 4 m"
```

### Escrevendo valores diretamente

Se você já tem os números decompostos, chame `write` sem passar por um `Duration`:

```java
english.write(1, 2, 3, 4, 5);
// "1 year, 2 days, 3 hours, 4 minutes and 5 seconds"
```

---

## Fusos horários

Todas as fábricas e o `parse` aceitam um `ZoneId`, usado na conversão de e para tipos baseados em `Instant` (`Date`, `Calendar`, `GregorianCalendar`):

```java
Temporality.of(new Date(), ZoneId.of("America/Sao_Paulo"));
Temporality.of("2024-05-17 14:30:00", Chronos.TRACE_D4HMS_ISO, ZoneId.of("UTC"));
Temporality.now(Chronos.BAR_D4HMS, ZoneId.of("Asia/Tokyo"));

Chronos.BAR_D4HMS.of("17/05/2024 14:30:00", ZoneId.of("UTC"));

Date emUtc = Chronos.BAR_D4HMS.of("17/05/2024 14:30:00")
        .parse(Date.class, ZoneId.of("UTC"));
```

Quando o `ZoneId` é omitido, usa-se `ZoneId.systemDefault()`.

> ⚠️ O `ZoneId` é aplicado apenas pelos converters que lidam com `Instant`. Converters puramente locais (`LocalDate`, `LocalDateTime`, `LocalTime`, `String`) mantêm os campos como estão — veja [Limitações conhecidas](#limitações-conhecidas).

---

## Tipos suportados e converters

| Tipo | Converter | Precisa de pattern? |
|---|---|---|
| `java.lang.String` | `StringConverter` | **Sim** |
| `java.lang.Long` | `LongConverter` | **Sim** |
| `java.util.Date` | `DateConverter` | Não |
| `java.util.Calendar` | `CalendarConverter` | Não |
| `java.util.GregorianCalendar` | `GregorianCalendarConverter` | Não |
| `java.time.LocalDate` | `LocalDateConverter` | Não |
| `java.time.LocalDateTime` | `LocalDateTimeConverter` | Não |
| `java.time.LocalTime` | `LocalTimeConverter` | Não |
| `Temporality` | `TemporalityConverter` | Não |

Os converters implementam uma de duas interfaces:

- **`PatternDateConverter<T>`** — o tipo é ambíguo e precisa de um pattern para ser interpretado (texto, número).
- **`NoPatternDateConverter<T>`** — o tipo já carrega a informação temporal; o pattern é ignorado.

Alguns detalhes de comportamento que valem conhecer:

- `LocalDate` → `LocalDateTime` entra à **meia-noite** (`atStartOfDay`).
- `LocalTime` → `LocalDateTime` assume a **data de hoje**.
- `Long` é convertido para texto e delegado ao `StringConverter`; na volta, tudo que não é dígito é removido.
- O lookup do converter é feito pela classe **exata** do objeto — subclasses não registradas caem em `TypeNotFoundException`.

---

## Criando seu próprio converter

Para ensinar a biblioteca a lidar com um tipo novo, implemente a interface adequada e registre no `DateConverterFactory`. A partir daí o tipo funciona em **todos** os pontos da API — `of`, `parse`, `isBefore`, `until`, `isBetween`…

Exemplo com `java.sql.Timestamp` (tipo que já carrega data e hora, logo `NoPatternDateConverter`):

```java
public class TimestampConverter implements NoPatternDateConverter<Timestamp> {

    @Override
    public LocalDateTime toLocalDateTime(Timestamp date, ZoneId zoneId) {
        return date.toLocalDateTime();
    }

    @Override
    public Timestamp fromLocalDateTime(LocalDateTime date, ZoneId zoneId) {
        return Timestamp.valueOf(date);
    }
}
```

Registre uma única vez, na inicialização da aplicação:

```java
DateConverterFactory.add(new TimestampConverter());

// A partir daqui o tipo é cidadão de primeira classe:
Temporality t     = Temporality.of(Timestamp.valueOf("2024-05-17 14:30:00"));
Timestamp   back  = Chronos.BAR_D4.of("17/05/2024").parse(Timestamp.class);
```

Se o seu tipo precisa de pattern para ser interpretado, implemente `PatternDateConverter<T>`:

```java
public class InstantConverter implements PatternDateConverter<Instant> {

    @Override
    public LocalDateTime toLocalDateTime(Instant date, String pattern, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date, zoneId);
    }

    @Override
    public Instant fromLocalDateTime(LocalDateTime date, String pattern, ZoneId zoneId) {
        return date.atZone(zoneId).toInstant();
    }
}
```

> O tipo alvo é descoberto por reflexão, a partir do argumento genérico da interface. Portanto **declare o genérico explicitamente** (`implements NoPatternDateConverter<Timestamp>`), sem usar um tipo cru — caso contrário o registro falha com `IllegalArgumentException: Cannot determine type of converter`.

Registrar um converter para um tipo já suportado **substitui** o padrão — útil, por exemplo, para mudar como `Long` é interpretado (epoch millis em vez de dígitos formatados).

---

## Usando a fábrica diretamente

Se você quer só a conversão, sem passar pela API fluente:

```java
// Qualquer tipo → LocalDateTime
LocalDateTime ldt  = DateConverterFactory.toLocalDateTime("17/05/2024", "dd/MM/yyyy");
LocalDateTime ldt2 = DateConverterFactory.toLocalDateTime(new Date(), null, ZoneId.of("UTC"));

// LocalDateTime → qualquer tipo
String texto = DateConverterFactory.fromLocalDateTime(
        LocalDateTime.of(2024, 5, 17, 14, 30), String.class, "dd/MM/yyyy HH:mm");   // "17/05/2024 14:30"

Date data = DateConverterFactory.fromLocalDateTime(
        LocalDateTime.now(), Date.class, null, ZoneId.of("UTC"));

// Obtendo o converter de um tipo
PatternDateConverter<Date> conv = DateConverterFactory.of(Date.class);
```

---

## Exceções

Todas estendem `RuntimeException` — não é preciso declarar `throws`.

| Exceção | Quando é lançada |
|---|---|
| `PatternRequiredException` | O tipo exige pattern (`String`, `Long`) e nenhum foi informado |
| `TypeNotFoundException` | Não há converter registrado para o tipo |
| `NullPointerException` | Argumento anotado com `@NonNull` recebeu `null` |
| `DateTimeParseException` | O texto não casa com o pattern informado |

```java
Temporality.of("17/05/2024");
// PatternRequiredException: Converter for type 'String' need a pattern

Temporality.of(LocalDate.now()).parse(String.class);
// PatternRequiredException: Converter for type 'String' need a pattern

Temporality.of(new StringBuilder("x"));
// TypeNotFoundException: Converter for type 'StringBuilder' not found

Temporality.of((Object) null);
// NullPointerException: date is marked non-null but is null

Temporality.of("32/13/2024", Chronos.BAR_D4);
// java.time.format.DateTimeParseException
```

---

## Receitas

**Trocar o formato de uma string de data**

```java
String iso = Chronos.BAR_D4.of("17/05/2024").parse(String.class, Chronos.TRACE_D4_ISO);
// "2024-05-17"
```

**Nome de arquivo com timestamp**

```java
String arquivo = "relatorio " + Chronos.FILE_D4HM.now() + ".pdf";
// "relatorio 2024-05-17 14 30.pdf"
```

**Intervalo do mês corrente para uma query**

```java
Date de  = Temporality.now().startOfMonth().parse(Date.class);
Date ate = Temporality.now().endOfMonth().parse(Date.class);
```

**Vencimento em dia útil, 30 dias à frente**

```java
LocalDate vencimento = Temporality.now()
        .plus(30, ChronoUnit.DAYS)
        .preventWeekend()
        .parse(LocalDate.class);
```

**Idade em anos completos**

```java
long idade = Chronos.BAR_D4.of("17/05/1990").until(LocalDate.now(), ChronoUnit.YEARS);
```

**"Publicado há…" para uma UI**

```java
WritingFormat ha = WritingFormat.of("agora mesmo",
        UnitFormat.of("ano", "anos"),
        UnitFormat.of("dia", "dias"),
        UnitFormat.of("hora", "horas"),
        UnitFormat.of("minuto", "minutos"));

String texto = "há " + Temporality.timeElapsedInWriting(
        Duration.between(publicadoEm, LocalDateTime.now()).abs(), ha);
// "há 3 dias, 1 hora e 30 minutos"
```

**Converter um `Long` de banco legado em `LocalDate`**

```java
LocalDate d = Temporality.of(20240517L, Chronos.BASIC_D4_ISO).parse(LocalDate.class);
```

**Verificar se hoje está dentro de uma vigência**

```java
boolean vigente = Temporality.now().isBetween(inicioVigencia, fimVigencia);
```

**Normalizar um `Calendar` legado para `LocalDateTime`**

```java
LocalDateTime ldt = Temporality.of(calendarLegado).parse(LocalDateTime.class);
```

---

## Limitações conhecidas

Comportamentos verificados na versão 3.0.0 que valem atenção:

1. **Durações múltiplas exatas de 365 dias caem no texto de "agora".**
   `WritingFormat.write` decide se a duração é insignificante olhando apenas dias, horas e minutos — e os dias vêm do resto da divisão por 365. Assim, `Duration.ofDays(365)` produz `"Agora"` em vez de `"1 ano"`.

2. **O ano é fixado em 365 dias.** O cálculo do tempo por extenso não considera anos bissextos, então textos longos podem divergir em um dia.

3. **`BASIC_HM` só serve para saída.** Um pattern apenas de horário não consegue produzir um `LocalDateTime` completo na entrada:
   ```java
   Temporality.of("1430", Chronos.BASIC_HM);                  // DateTimeParseException
   Temporality.now().parse(String.class, Chronos.BASIC_HM);   // "1430" ✔
   ```

4. **`now(ZoneId)` não converte o relógio para o fuso.** O instante atual é capturado com `LocalDateTime.now()` no fuso do sistema. Para obter o horário de outro fuso:
   ```java
   Temporality.of(LocalDateTime.now(ZoneId.of("Asia/Tokyo")), Chronos.BAR_D4HMS);
   ```

5. **`parse(String.class, pattern, zoneId)` não desloca o horário.** A formatação de texto apenas rotula o `LocalDateTime` com o fuso; para converter de fato, passe por `Date`/`Instant`.

6. **`Temporality` é mutável e não é thread-safe.** Não compartilhe a mesma instância entre threads.

---

## Migrando da 2.x para a 3.x

A 3.0.0 é uma release **breaking de empacotamento** — a API é idêntica, só mudaram as coordenadas e o pacote Java.

| | 2.x | 3.x |
|---|---|---|
| `groupId` | `br.com.dld` | `io.github.dlduarte` |
| Pacote Java | `br.com.dld.suppledate` | `io.github.dlduarte.suppledate` |
| Repositório | Nexus privado | GitHub Packages |
| Lombok | escopo `compile` (vazava para o consumidor) | escopo `provided` |

A migração é uma substituição de texto no projeto consumidor:

```bash
grep -rl 'br.com.dld.suppledate' src/ | xargs sed -i 's/br\.com\.dld\.suppledate/io.github.dlduarte.suppledate/g'
```

Nenhuma assinatura de método mudou — depois de ajustar os `import`, o código compila sem alterações.

---

## Build local

```bash
mvn clean install
```

Compila com **JDK 8–24** (o Lombok 1.18.38 ainda não suporta o JDK 25) e gera três artefatos:

- `supple-date-3.0.0.jar`
- `supple-date-3.0.0-sources.jar`
- `supple-date-3.0.0-javadoc.jar`

Para publicar uma nova versão, atualize a `<version>` no `pom.xml` e crie a tag — o workflow [`publish.yml`](.github/workflows/publish.yml) cuida do resto:

```bash
git tag v3.0.1 && git push origin v3.0.1
```

---

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
