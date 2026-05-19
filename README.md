# CI/CD com GitHub Actions - Spring Boot + Maven

Aplicação Spring Boot com pipeline CI/CD completa utilizando GitHub Actions e deploy automático em EC2.

## 📋 Descrição do Projeto

Este projeto implementa um pipeline de integração contínua e implantação contínua (CI/CD) para uma aplicação Spring Boot com Maven. O pipeline automatiza:

- **Build** - Compilação do projeto Maven
- **Testes** - Execução de testes unitários
- **Docker** - Construção e publicação da imagem no Docker Hub
- **Deploy** - Implantação automática em EC2 via GitHub Runner

## 🛠️ Tecnologias

- **Spring Boot** 4.0.6
- **Java** 21
- **Maven** - Gerenciador de dependências
- **Docker** - Containerização
- **GitHub Actions** - Pipeline CI/CD
- **AWS EC2** - Infraestrutura de produção

## 🏗️ Estrutura da Aplicação

```
actions/
├── src/
│   ├── main/
│   │   ├── java/com/sfl/actions/
│   │   │   ├── ActionsApplication.java
│   │   │   ├── controller/UserController.java
│   │   │   ├── model/UserModel.java
│   │   │   ├── repository/UserRepository.java
│   │   │   └── service/UserService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/sfl/actions/
│       ├── ActionsApplicationTests.java
│       ├── controller/UserControllerTest.java
│       └── service/UserServiceTest.java
├── pom.xml
├── Dockerfile
├── .dockerignore
└── .github/workflows/
    ├── ci.yml
    └── cd.yml
```

## 🚀 Pipeline CI/CD

### Fluxo de Execução

```
Push/PR na branch main
    ↓
[1] Build (./mvnw clean compile)
    ↓
[2] Test (./mvnw test)
    ↓
[3] Docker (build + push no Docker Hub)
    ↓
[4] Deploy na EC2 (runner self-hosted)
    ↓
Verificar saúde da aplicação
```

### Job: Build
- Checkout do código
- Setup de Java 21
- Cache Maven
- Compilação do projeto

### Job: Test
- Execução de testes unitários
- Relatórios automáticos

### Job: Docker
- Login no Docker Hub
- Build da imagem multi-stage
- Push com tag `latest`

### Job: Deploy (self-hosted)
- Pull da imagem do Docker Hub
- Parada do container anterior
- Remoção do container antigo
- Inicialização do novo container na porta 8080
- Verificação de saúde

## 📦 Dockerfile

A imagem utiliza multi-stage build para otimização:

**Stage 1 (Builder):**
- Imagem Maven 3.9 com Java 21
- Copia código-fonte
- Executa `mvn clean package`

**Stage 2 (Runtime):**
- Imagem JRE slim (menor tamanho)
- Copia JAR do stage anterior
- Expõe porta 8080
- Executa a aplicação

## 🔐 Configuração de Secrets

Configure os seguintes secrets no GitHub para que o pipeline funcione:

| Secret | Descrição |
|--------|-----------|
| `DOCKERHUB_USERNAME` | Seu username no Docker Hub |
| `DOCKERHUB_TOKEN` | Token de acesso pessoal do Docker Hub |

**Como adicionar secrets:**
1. Vá para Settings → Secrets and variables → Actions
2. Clique em "New repository secret"
3. Adicione `DOCKERHUB_USERNAME` e `DOCKERHUB_TOKEN`

## 💻 Como Rodar Localmente

### Com Maven

```bash
# Compilar
./mvnw clean compile

# Rodar testes
./mvnw test

# Executar aplicação
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### Com Docker

```bash
# Build da imagem
docker build -t actions:latest .

# Executar container
docker run -d -p 8080:8080 --name actions-app actions:latest

# Verificar logs
docker logs -f actions-app

# Parar container
docker stop actions-app
docker rm actions-app
```

## 🔍 Verificação de Saúde

Após o deploy, verifique o status da aplicação:

```bash
curl http://IP-EC2:8080/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

## ⚙️ Configuração Manual na EC2

### 1. GitHub Runner (Self-Hosted)

Na instância EC2:

```bash
# Criar diretório
mkdir actions-runner && cd actions-runner

# Baixar runner (substituir versão e token conforme necessário)
curl -O -L https://github.com/actions/runner/releases/download/v2.x.x/actions-runner-linux-x64-2.x.x.tar.gz
tar xzf ./actions-runner-linux-x64-2.x.x.tar.gz

# Configurar runner
./config.sh --url https://github.com/ShirleyFerr/CI-CD-with-GithubActions --token SEU_TOKEN

# Executar em background
nohup ./run.sh > output.log 2>&1 &
```

### 2. Docker na EC2

```bash
# Verificar instalação
docker ps

# Se necessário, configurar permissões
sudo usermod -aG docker $USER
```

### 3. Security Group da EC2

Liberar porta 8080:
- **Protocol:** TCP
- **Port Range:** 8080
- **Source:** Seu IP ou 0.0.0.0/0

## 📊 Status do Pipeline

Acesse o status do pipeline em:
`https://github.com/ShirleyFerr/CI-CD-with-GithubActions/actions`

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| Docker push falha | Verificar secrets DOCKERHUB_USERNAME e DOCKERHUB_TOKEN |
| Deploy não inicia | Verificar se GitHub Runner está ativo com `./run.sh` |
| Container não sobe | Verificar logs: `docker logs actions-app` |
| Porta 8080 já em uso | `docker ps` e parar container anterior |

## 📝 Próximos Passos

- [ ] Configurar GitHub Runner na EC2
- [ ] Adicionar secrets no repositório
- [ ] Testar pipeline completo com push
- [ ] Monitorar logs de deployment
- [ ] Configurar notificações no Slack (opcional)

## 📧 Suporte

Para dúvidas sobre o pipeline, consulte a [documentação do GitHub Actions](https://docs.github.com/en/actions).

:)