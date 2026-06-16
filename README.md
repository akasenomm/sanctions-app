# Sanctions Name Screening Service

A Spring Boot REST service that screens a input name against a stored list
of sanctioned people and reports whether a match exists. Matching is fuzzy and
order-independent, so it tolerates reordering, missing tokens, noise words,
abbreviations, typos, and substring variants.

## Tech stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA
- H2 in-memory database
- Apache Commons Text (Levenshtein for typo detection, Jaro-Winkler for fuzzy match)
- Swagger UI
- JUnit

## Running

Requires **Java 21** and **Maven 3.9+** installed on `PATH`.

```bash
mvn spring-boot:run
```

On Windows (PowerShell), set `JAVA_HOME` first if needed:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.x.x'
mvn spring-boot:run
```

The app starts on http://localhost:8080 and seeds the assignment example
sanctioned names on startup:

| Sanctioned name    | Example query that should match      |
|--------------------|--------------------------------------|
| Osama Bin Laden    | `Osama Laden`, `Bin Laden, Osama`, … |
| Robert             | `Bert`                               |
| Madis              | `Madus`                              |
| Joe Luis Webb      | `Joe L. Webb`                        |
| Mr. John Smith     | `John Smith`                         |

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:sanctions`)

Run the tests with:

```bash
mvn test
```

## API

### Screen a name

`POST /api/screening`

```bash
curl -X POST http://localhost:8080/api/screening \
  -H "Content-Type: application/json" \
  -d '{"name": "Bin Laden, Osama"}'
```

```json
{
  "match": true,
  "matches": [
    { "id": 1, "fullName": "Osama Bin Laden", "score": 1.0 }
  ]
}
```

When multiple list entries match, all of them are returned, sorted by score
(highest first):

```json
{
  "match": true,
  "matches": [
    { "id": 1, "fullName": "Osama Bin Laden", "score": 1.0 },
    { "id": 2, "fullName": "Osama bin Ladin", "score": 0.96 }
  ]
}
```

When nothing matches:

```json
{
  "match": false,
  "matches": []
}
```

### Manage sanctioned names

| Method | Path                  | Description            |
|--------|-----------------------|------------------------|
| GET    | `/api/sanctions`      | List all entries       |
| GET    | `/api/sanctions/{id}` | Get one entry          |
| POST   | `/api/sanctions`      | Create an entry        |
| PUT    | `/api/sanctions/{id}` | Update an entry        |
| DELETE | `/api/sanctions/{id}` | Delete an entry        |


## Screening performance

Sanctioned names are kept in an in-memory cache (`SanctionedNameCache`) with
precomputed tokens. The cache is rebuilt on startup and
updated on every create, update, or delete. Each screening request tokenizes
the submitted name once, then compares it against the cached token lists.

## Matching algorithm

A name is treated as a set of tokens, and matching runs as follows:

1. **Normalize** — lower-case, remove letter markings  (`José` -> `jose`), strip
   punctuation, collapse whitespace. (`NameNormalizer`)
2. **Tokenize + drop noise words** — split into tokens and remove noise words
   (`the`, `to`, `mr`, `and`, ...).
3. **Per-token rules** — each token pair is classified (`TokenMatcher`):
   - **Exact** — identical after normalization.
   - **Initial** — one side is a single-letter abbreviation (`l` / `luis`).
   - **Substring** — the shorter token (≥3 chars) appears inside the longer
     (`bert` / `robert`).
   - **Fuzzy** — Apache Commons Text `LevenshteinDistance` within one edit
     per four characters (minimum one edit for short tokens); `JaroWinklerSimilarity`
     supplies the confidence score when a fuzzy match is accepted.
4. **Aggregate** — order-independent: each token in the smaller bag takes its
   best match in the larger bag. A ranking score is derived from average token
   confidence and the smaller/larger size ratio.
   (`NameMatcher`)
5. **Decide** — rule-based: every smaller-bag token must match; a single-token
   query against a multi-token name requires an exact hit (so `bert` matches
   `robert` but not `robert mueller`).


## Notes

For this type of task I would actually use Elasticsearch which is specialized for
fuzzy matching and which scales incredibly well, but I figured this task was
give to showcase Java skills not skills for integrating tools.

