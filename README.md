# GuideAut Backend (Spring Boot)

API backend do **GuideAut** usando **Spring Boot 3.5.x**, **Java 21**, **JWT** para autenticação, **H2** em memória (dev) e **Swagger UI** para documentação/testes.

---

## 📘 Sumário
- [Stack](#stack)
- [Pré-requisitos](#pré-requisitos)
- [Como rodar (dev)](#como-rodar-dev)
- [Configuração](#configuração)
- [Swagger / Testes rápidos](#swagger--testes-rápidos)
- [Autenticação JWT](#autenticação-jwt)
- [Banco de dados (H2)](#banco-de-dados-h2)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Padrão de commits](#padrão-de-commits)
- [Fluxo de branches e PRs](#fluxo-de-branches-e-prs)
- [Checklist de contribuição](#checklist-de-contribuição)
- [Publicar no GitHub](#publicar-no-github)
- [Licença](#licença)

---

## 🧱 Stack
- **Java 21**
- **Spring Boot 3.5.x**
- **Spring Web**, **Spring Data JPA**, **Spring Security**
- **H2** (ambiente de desenvolvimento)
- **Springdoc OpenAPI (Swagger)**
- **JWT (jjwt)**
- **Maven** (`./mvnw` wrapper)

---

## ⚙️ Pré-requisitos
- **Java 21** (Temurin/Adoptium recomendado)
- *(Opcional)* **cURL** para smoke tests

---

## ▶️ Como rodar (dev)

```bash
# Subir a aplicação
./mvnw clean spring-boot:run
```

A API ficará disponível em:

👉 [http://localhost:8080](http://localhost:8080)

---

## ⚙️ Configuração

Arquivo principal: `src/main/resources/application.yaml`

```yaml
server:
  port: 8080

jwt:
  secret: "troque-por-uma-chave-aleatoria-32+bytes-aaaaaaaaaaaaaaaaaaaaaaaaaa"
  access-minutes: 30
  refresh-days: 7

spring:
  application:
    name: project
  datasource:
    url: jdbc:h2:mem:guideaut;DB_CLOSE_DELAY=-1
    driverClassName: org.h2.Driver
    username: sa
    password: sa
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate.format_sql: true
  h2:
    console:
      enabled: true
```

**CORS:** configurado em `SecurityConfig` via `corsConfigurationSource()` e `http.cors(...)`.  
> Ajuste as origens permitidas (ex.: `http://localhost:5173`) ao integrar com o front-end.

---

## 🧪 Swagger / Testes rápidos

- Swagger UI → [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON → [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### 🧭 Fluxo rápido no Swagger

1. Faça **POST** em `/auth/login` com:
   ```json
   { "email": "admin@guideaut.com", "password": "admin123" }
   ```

2. Copie o `accessToken`.

3. Clique em **Authorize** (ícone do cadeado), selecione `bearerAuth` e cole **apenas o token** (o Swagger já aplica o prefixo `Bearer`).

4. Teste:
   - `GET /me` → dados do usuário autenticado.  
   - `GET /admin/users` → exige papel **ADMIN**.

---

### 🧩 cURL (opcional)

```bash
# login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@guideaut.com","password":"admin123"}'

# usando o token (substitua $TOKEN)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/me
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/admin/users
```

---

## 🔐 Autenticação JWT

- `POST /auth/login` → retorna `accessToken` (Bearer) e `refreshToken`.
- O filtro `JwtAuthFilter` valida `Authorization: Bearer <token>` e popula o contexto com as **roles** do usuário.
- Endpoints de refresh poderão ser adicionados futuramente.

---

## 💾 Banco de dados (H2)

- Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:guideaut`
- Usuário: `sa`
- Senha: `sa`

### Seed inicial (DataInitializer)
Cria o usuário **admin**:
```yaml
email: admin@guideaut.com
senha: admin123
papel: ADMIN
```

---

## 🗂️ Estrutura de pastas

```
src/main/java/com/guideaut/project
├── auth/               # AuthController, AuthService, DTOs
├── bootstrap/          # DataInitializer (seed)
├── config/             # SecurityConfig (CORS, filtros, stateless)
├── identity/           # Usuario, Papel, Permissao, enums
├── repo/               # Repositórios JPA
├── security/           # JwtService, JwtAuthFilter
├── token/              # RefreshToken e repo
└── web/                # UserController (/me, /admin/users)
```

---

## 🧩 Padrão de commits

Usamos **Conventional Commits**:

```
<tipo>(<escopo>): <descrição>
```

**Tipos comuns:**  
`feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.

**Exemplos:**
```
feat(auth): adicionar login com JWT
fix(user): corrigir NPE ao buscar papeis
docs(readme): instruções de swagger e h2
chore: atualizar dependências do jjwt
```

**Escopos sugeridos:** `auth`, `user`, `admin`, `security`, `config`, `docs`, `build`, `ci`.

---

## 🌱 Fluxo de branches e PRs

**Branches principais:**
- `main`: estável e versionada (merge via PR, protegida)
- `develop`: integração contínua
- `feature/*`: novas funcionalidades (ex.: `feature/auth-refresh-token`)
- `fix/*`: correções (ex.: `fix/security-nullpointer`)
- `chore/*`, `docs/*`, etc.

**Regras de PR:**
- Título em formato Conventional Commits (ex.: `feat(auth): suporte a refresh token (#123)`)
- Descreva objetivo, passos de teste e impacto
- 1 review obrigatório antes do merge
- Preferir **squash merge** para manter histórico limpo

---

## ✅ Checklist de contribuição

- [ ] Commit seguindo Conventional Commits  
- [ ] Testado localmente (`./mvnw spring-boot:run`)  
- [ ] Sem quebras de contrato nos endpoints  
- [ ] Atualizou README/Swagger se necessário  
- [ ] PR com descrição e cenários de teste  

